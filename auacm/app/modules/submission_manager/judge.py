import os
import os.path
import shlex
import subprocess
import time
import threading

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
    return ('.' in filename and
            filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS)

class Judge:

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
        """Runs the Judgement on a new thread"""
        thread = threading.Thread(target=self.run, args=(self))
        thread.daemon = False
        thread.start()


    def run(self):
        """Attempts to compile (if necessary) then execute a given file.

        :return: the status of the submission (one of the status constants above)
        """
        status = self.compile_submission()
        self.update_status(status, -1)

        if status == COMPILATION_SUCCESS:
            status, test_number = self.execute_submission()
            self.update_status(status, test_number)

        return status


    def update_status(self, status, test_number):
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


    def compile_submission(self):
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


    def execute_submission(self):
        """Run the submission.

        This method:
            1. detects all the input files associated with this problem.
            2. runs the submission with each input file
            3. checks the performance of the submission for errors (TLE, RTE, etc)
            4. compares the output against correct test output
        """
        # Initial setup
        problem_directory = self.directory_for_problem
        submission_directory = self.directory_for_submission
        input_path = os.path.join(problem_directory, 'in')
        output_path = os.path.join(problem_directory, 'out')
        max_runtime = self.time_limit * TIMEOUT_MULTIPLIER[self.submission.file_type]

        # Final setup.
        output_path = os.path.join(submission_directory, 'out')
        if (not os.path.exists(output_path)):
            os.mkdir(output_path)

        # Iterate over all the input files.
        for fname in os.listdir(input_path):
            f = os.path.join(input_path, fname)
            if os.path.isfile(f):
                # Prepare to run the test file.
                test_number = int(fname.split('.')[0].strip('in'))
                out_file = 'out{0}.txt'.format(test_number)

                process = self.create_process(f, out_file)
                start_time = self.judge_process(process, max_runtime)

                # Check the execution for timeouts and runtime errors.
                if time.time() >= start_time + max_runtime:
                    try:
                        process.kill()
                    except:
                        pass
                    return TIMELIMIT_EXCEEDED, test_number
                elif process.poll() != 0:
                    return RUNTIME_ERROR, test_number

                result_path = os.path.join(problem_directory, 'out')

                # The execution is completed.  Check its correctness.
                with open(os.path.join(output_path, out_file)) as correct_result, \
                     open(os.path.join(result_path, out_file)) as submission_result:
                    correct_lines = correct_result.readlines()
                    submission_lines = submission_result.readlines()
                    if len(submission_lines) != len(correct_lines):
                        return WRONG_ANSWER, test_number

                    for gl, sl in zip(correct_lines, submission_lines):
                        if gl.rstrip('\r\n') != sl.rstrip('\r\n'):
                            return WRONG_ANSWER, test_number

        # The answer is correct if all the tests complete without any failure.
        return CORRECT_ANSWER, test_number


    def create_process(self, in_file, out_file):
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
            shlex.split(RUN_COMMAND[self.submission.file_type].format(directory, name)),
            stdin=open(os.path.join(input_path, in_file)),
            stdout=open(os.path.join(output_path, out_file), 'w'),
            stderr=open(os.path.join(directory, 'error.txt'), 'w'))

        return process


    def judge_process(self, process, limit):
        """Run the process and make sure that it doesn't run for too long. In
        the event that it runs for more than its allotted time limit, execution
        will stop and the process will be killed.

        :param process: the process to be executed
        :param limit: the most time that the process can execute for
        :return: the time that the process started
        """
        # Actually run the process and time it
        start_time = time.time()
        while process.poll() is None:
            time.sleep(0.1)  # wait for 1/10 of a second to check on the problem.
            if time.time() > start_time + limit:
                # The try is to avoid a race condition where the process
                # finishes between the if and the kill statements.
                try:
                    process.kill()
                except:
                    pass
        return start_time
