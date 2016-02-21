import os
import os.path
import shlex
import subprocess
import threading
import time

from app import app
from app.modules.flasknado.flasknado import Flasknado


ALLOWED_EXTENSIONS = ['java', 'c', 'cpp', 'py', 'go']
COMPILE_COMMAND = {
    'java': 'javac {0}.java',
    'py': None,
    'py3': None,
    'c': 'gcc {0}.c -o {0}',
    'cpp': 'g++ {0}.cpp -o {0}',
    'go': 'go build -o {0} {0}.go'
}
RUN_COMMAND = {
    'java': 'java -cp {0}/ {1}',
    'py': 'python2.7 {0}/{1}.py',
    'py3': 'python3 {0}/{1}.py',
    'c': '{0}/{1}',
    'cpp': '{0}/{1}',
    'go': '{0}/{1}'
}
TIMEOUT_MULTIPLIER = {
    'java': 1.5,
    'py': 2,
    'py3': 2,
    'c': 1,
    'cpp': 1,
    'go': 1
}
DB_STATUS = ['', 'compile', 'start', 'runtime', 'timeout', 'wrong', 'good']
EVENT_STATUS = ['', 'compile', 'running', 'runtime', 'timeout', 'incorrect',
                'correct']
COMPILATION_ERROR = 1
COMPILATION_SUCCESS = 2
RUNTIME_ERROR = 3
TIMELIMIT_EXCEEDED = 4
WRONG_ANSWER = 5
CORRECT_ANSWER = 6


def allowed_filetype(filename):
    """Check to see if the filename is an allowed type or not"""
    return ('.' in filename and
            filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS)

class Judge:
    """A container for a judging instance. It contains the submission, the
    source code that goes along with the submission, and the time limit of the
    problem.
    """

    def __init__(self, submission, uploaded_file, time_limit):
        """Create a new Judgement instance.

        :param submission: the submission to be judged
        :param uploaded_file: the source code file
        :param time_limit: the time limit for execution
        """
        self.submission = submission
        self.uploaded_file = uploaded_file
        self.directory_for_submission = os.path.join(
            app.config['DATA_FOLDER'], 'submits', str(self.submission.job))
        self.directory_for_problem = os.path.join(
            app.config['DATA_FOLDER'], 'problems', str(self.submission.pid))
        self.time_limit = time_limit


    def run_threaded(self):
        """Runs the Judgement on a new thread

        :return: the thread that was created for the execution
        """
        thread = threading.Thread(target=self.run)
        thread.daemon = False
        thread.start()
        return thread


    def run(self):
        """Attempts to compile (if necessary) then execute a given file.

        :return: one of the status constants representing the success
        """
        status = self._compile_submission()
        self._update_status(status, -1)

        if status == COMPILATION_SUCCESS:
            status, test_number, max_time = self._execute_submission()
            self._update_status(status, test_number)
        else:
            max_time = -1

        return status, max_time


    def _update_status(self, status, test_number):
        """Updates the status of the submission and notifies the clients that
        the submission has a new status.
        """
        self.submission.update_status(DB_STATUS[status])
        Flasknado.emit('status', {
            'submissionId': self.submission.job,
            'problemId': self.submission.pid,
            'username': self.submission.username,
            'submitTime': self.submission.submit_time,
            'testNum': test_number,
            'status': EVENT_STATUS[status]
        })


    def _compile_submission(self):
        """Compile the submission if it needs compilation. A programming
        language that does not need compilation will return COMPILATION_SUCCESS.
        """
        directory = self.directory_for_submission
        filename = self.uploaded_file.filename
        name, _ = filename.rsplit('.', 1)

        # Don't compile file types that we can't compile.
        if COMPILE_COMMAND[self.submission.file_type] is None:
            return COMPILATION_SUCCESS

        result = subprocess.call(
            shlex.split(COMPILE_COMMAND[self.submission.file_type]
                        .format(os.path.join(directory, name))),
            stderr=open(os.path.join(directory, 'error.txt'), 'w')
        )

        if result == 0:
            return COMPILATION_SUCCESS
        else:
            return COMPILATION_ERROR


    def _execute_submission(self):
        """Run the submission.

        This method:
            1. detects all the input files associated with this problem.
            2. runs the submission with each input file
            3. checks the performance of the submission for errors
            4. compares the output against correct test output
        """
        # Initial setup
        input_path = os.path.join(self.directory_for_problem, 'in')
        output_path = os.path.join(self.directory_for_submission, 'out')
        max_runtime = (self.time_limit *
                TIMEOUT_MULTIPLIER[self.submission.file_type])

        # Final setup.
        output_path = os.path.join(self.directory_for_submission, 'out')
        if not os.path.exists(output_path):
            os.mkdir(output_path)

        max_time = 0
        # Iterate over all the input files.
        for fname in os.listdir(input_path):
            f = os.path.join(input_path, fname)
            if os.path.isfile(f):
                # Prepare to run the test file.
                test_number = int(fname.split('.')[0].strip('in'))
                out_file = 'out{0}.txt'.format(test_number)

                start_time = time.time()
                process = self._create_process(f, out_file)
                try:
                    process.communicate(timeout=max_runtime)
                except subprocess.TimeoutExpired:
                    process.kill()
                    return TIMELIMIT_EXCEEDED, test_number, max_runtime

                end_time = time.time()
                max_time = max(max_time, end_time - start_time)
                if process.poll() != 0:
                    return RUNTIME_ERROR, test_number, max_time

                result_path = os.path.join(self.directory_for_problem, 'out')

                # The execution is completed.  Check its correctness.
                with open(os.path.join(output_path, out_file)) as correct, \
                     open(os.path.join(result_path, out_file)) as sub_result:
                    correct_lines = correct.readlines()
                    submission_lines = sub_result.readlines()
                    if len(submission_lines) != len(correct_lines):
                        return WRONG_ANSWER, test_number, max_time

                    for gl, sl in zip(correct_lines, submission_lines):
                        if gl.rstrip('\r\n') != sl.rstrip('\r\n'):
                            return WRONG_ANSWER, test_number, max_time

        # The answer is correct if all the tests complete without any failure.
        return CORRECT_ANSWER, test_number, max_time


    def _create_process(self, in_file, out_file):
        """Run the program to judge as a subprocess on a separate thread.

        Creates a process with the correct handlers to route the output to
        /data/submits/job/out.

        :param in_file: the input file that is going to be read in
        :param out_file: the output file that is going to be written to
        """

        # Configure all of the directories and input/output files
        directory = self.directory_for_submission
        name, _ = self.uploaded_file.filename.rsplit('.', 1)
        input_path = os.path.join(self.directory_for_problem, 'in')
        output_path = os.path.join(directory, 'out')

        # Create the subprocess
        process = subprocess.Popen(
            shlex.split(RUN_COMMAND[self.submission.file_type]
                    .format(directory, name)),
            stdin=open(os.path.join(input_path, in_file)),
            stdout=open(os.path.join(output_path, out_file), 'w'),
            stderr=open(os.path.join(directory, 'error.txt'), 'w'))

        return process
