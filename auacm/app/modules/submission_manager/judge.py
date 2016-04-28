import os
import os.path
import shlex
import subprocess
import threading
import time

from app import app


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

    def __init__(self, pid, submission_path, uploaded_file, time_limit, on_status=None):
        """Create a new Judgement instance.

        :param pid: the problem identifier
        :param submission_path: the path to where the execution should be stored
        :param uploaded_file: the source code file
        :param time_limit: the time limit for execution
        :param on_status: a function that takes two parameters (status,
                test_number) where status is one of the status constants and
                test_number is the number of tests that have successfully
                completed.
        """
        self.pid = pid
        self.uploaded_file = uploaded_file
        self.file_type = uploaded_file.filename.rsplit('.')[1].lower()
        self.time_limit = (time_limit * TIMEOUT_MULTIPLIER[self.file_type])
        self.on_status = on_status
        problem_path = os.path.join(
            app.config['DATA_FOLDER'], 'problems', str(self.pid))
        self.submission_path = submission_path
        self.prob_input_path = os.path.join(problem_path, 'in')
        self.prob_output_path = os.path.join(problem_path, 'out')
        self.sub_output_path = os.path.join(self.submission_path, 'out')
        if not os.path.exists(self.sub_output_path):
            os.mkdir(self.sub_output_path)

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
        """This method is invoked during the different events of the judging of
        the problem (e.g. compilation, success, or failure). If the on_status
        parameter is defined, then it will call back with the parameters
        (status, test_number) where status is one of the status integer
        constants and test_number is the highest test number that was
        completed.

        :param status: one of the status integer constants
        :param test_number: the highest test number that was completed
        """
        if self.on_status is not None:
            self.on_status(status, test_number)

    def _compile_submission(self):
        """Compile the submission if it needs compilation. A programming
        language that does not need compilation will return COMPILATION_SUCCESS.

        :return: either COMPILATION_SUCCESS or COMPILATION_ERROR
        """
        directory = self.submission_path
        filename = self.uploaded_file.filename
        name, _ = filename.rsplit('.', 1)

        # Don't compile file types that we can't compile.
        if COMPILE_COMMAND[self.file_type] is None:
            return COMPILATION_SUCCESS

        result = subprocess.call(
            shlex.split(COMPILE_COMMAND[self.file_type]
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

        :return: a tuple containing a status code, the highest test number
                completed, and the most time that any one of the tests took to
                run
        """
        max_time = 0
        # Iterate over all the input files.
        for fname in os.listdir(self.prob_input_path):
            f = os.path.join(self.prob_input_path, fname)
            if os.path.isfile(f):
                # Prepare to run the test file.
                test_number = int(fname.split('.')[0].strip('in'))
                out_file = 'out{0}.txt'.format(test_number)

                start_time = time.time()
                process = self._create_process(f, out_file)
                try:
                    # Set a time limit for the process's execution and wait for
                    # it to terminate.
                    process.communicate(timeout=self.time_limit)
                except subprocess.TimeoutExpired:
                    # If the process times out, then we will kill it outselves
                    process.kill()
                    return TIMELIMIT_EXCEEDED, test_number, self.time_limit
                end_time = time.time()
                max_time = max(max_time, end_time - start_time)

                if process.poll() != 0:
                    # If the process's exit code was nonzero, then it had a
                    # runtime error.
                    return RUNTIME_ERROR, test_number, max_time

                # The execution is completed.  Check its correctness.
                with open(os.path.join(
                          self.prob_output_path, out_file)) as correct, \
                     open(os.path.join(
                          self.sub_output_path, out_file)) as sub_result:
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
        name, _ = self.uploaded_file.filename.rsplit('.', 1)

        # Create the subprocess
        process = subprocess.Popen(
            shlex.split(RUN_COMMAND[self.file_type]
                        .format(self.submission_path, name)),
            stdin=open(os.path.join(self.prob_input_path, in_file)),
            stdout=open(os.path.join(self.sub_output_path, out_file), 'w'),
            stderr=open(os.path.join(self.submission_path, 'error.txt'), 'w'))

        return process
