import os
import os.path
import shlex
import subprocess
import time

# Need to specifically monkey_patch threading since we couldn't at server start
import eventlet
threading = eventlet.import_patched('threading')

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
    'py': 'python {0}/{1}.py',
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
    return ('.' in filename and
            filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS)


def directory_for_submission(submission):
    return os.path.join(
        app.config['DATA_FOLDER'], 'submits', str(submission.job))


def directory_for_problem(submission):
    return os.path.join(app.config['DATA_FOLDER'], 'problems', str(submission.pid))


def evaluate(submission, uploaded_file, time_limit=10):
    """Attempts to compile (if necessary) then execute a given file.

    :param submission: the newly created submission
    :param uploaded_file: the uploaded file
    :return: the status of the submission (one of the status constants above)
    """
    directory = directory_for_submission(submission)
    os.mkdir(directory)
    uploaded_file.save(os.path.join(directory, uploaded_file.filename))
    status = compile_submission(submission, uploaded_file)
    if status == COMPILATION_SUCCESS:
        status = execute_submission(submission, uploaded_file, time_limit)
    return status


def compile_submission(submission, uploaded_file):
    """Compile the submission."""
    directory = directory_for_submission(submission)
    filename = uploaded_file.filename
    name, ext = filename.rsplit('.', 1)
    # Don't compile file types that we can't compile.
    if COMPILE_COMMAND[submission.file_type] is None:
        return COMPILATION_SUCCESS
    result = subprocess.call(
        shlex.split(COMPILE_COMMAND[submission.file_type].format(os.path.join(directory, name))),
        stderr=open(os.path.join(directory, 'error.txt'), 'w')
    )
    if result == 0:
        return COMPILATION_SUCCESS
    else:
        submission.update_status('compile')
        submission.emit_status('compile', -1)
        return COMPILATION_ERROR


def execute_submission(submission, uploaded_file, time_limit):
    """Run the submission.

    TODO(djshuckerow): This method magically got out-of-hand.  Refactor.
    This method:
        1. detects all the input files associated with this problem.
        2. runs the submission with each input file
        3. checks the performance of the submission for errors (TLE, RTE, etc)
        4. compares the output against correct test output
    """
    # Initial setup
    problem = submission.get_problem()
    problem_directory = directory_for_problem(submission)
    submission_directory = directory_for_submission(submission)
    filename = uploaded_file.filename
    name, ext = filename.rsplit('.', 1)
    input_path = os.path.join(problem_directory, 'in')
    output_path = os.path.join(problem_directory, 'out')

    # Iterate over all the input files.
    for fname in os.listdir(input_path):
        f = os.path.join(input_path, fname)
        if os.path.isfile(f):
            # Prepare to run the test file.
            test_number = int(fname.split('.')[0].strip('in'))
            out_file = 'out{0}.txt'.format(test_number)
            submission.emit_status('running', test_number)
            max_runtime = time_limit * TIMEOUT_MULTIPLIER[submission.file_type]
            execution = JudgementThread(
                submission, uploaded_file, f, out_file, max_runtime)
            start_time = time.time()
            execution.start()
            execution.join(max_runtime)

            # Check the execution for timeouts and runtime errors.
            if time.time() >= start_time + max_runtime:
                execution.process.kill()
                submission.update_status('timeout')
                submission.emit_status('timeout', test_number)
                return TIMELIMIT_EXCEEDED
            elif execution.process.poll() != 0:
                submission.update_status('runtime')
                submission.emit_status('runtime', test_number)
                return RUNTIME_ERROR
            result_path = os.path.join(submission_directory, 'out')

            # The execution is completed.  Check its correctness.
            with open(os.path.join(output_path, out_file)) as correct_result, \
                 open(os.path.join(result_path, out_file)) as submission_result:
                correct_lines = correct_result.readlines()
                submission_lines = submission_result.readlines()
                if len(submission_lines) != len(correct_lines):
                    submission.update_status('wrong')
                    submission.emit_status('incorrect', test_number)
                    return WRONG_ANSWER

                for gl, sl in zip(correct_lines, submission_lines):
                    if gl.rstrip('\r\n') != sl.rstrip('\r\n'):
                        submission.update_status('wrong')
                        submission.emit_status('incorrect', test_number)
                        return WRONG_ANSWER

    # The answer is correct if all the tests complete without any failure.
    submission.update_status('good')
    submission.emit_status('correct', test_number)
    return CORRECT_ANSWER


class JudgementThread(threading.Thread):
    """Pass judgement on a submission by running it on a thread.

    This runs a separate thread containing the submission in a subprocess.

    Timeout is tricky to handle -- thread.join(timeout) doesn't appear to
    work properly inside of Flask.
    """

    def __init__(self, submit, uploaded_file, in_file, out_file, limit):
        """Create the JudgementThread.

        :param submit: the newly created submission
        :param uploaded_file: the file uploaded from flask
        :param in_file: the input file that is going to be read in
        :param out_file: the output file that is going to be written to
        :param limit: the time limit for execution.
        """
        threading.Thread.__init__(self)
        self.submit = submit
        self.uploaded_file = uploaded_file
        self.in_file = in_file
        self.out_file = out_file
        self.process = None
        self.limit = limit
        # Final setup.
        directory = directory_for_submission(submit)
        output_path = os.path.join(directory, 'out')
        if (not os.path.exists(output_path)):
            os.mkdir(output_path)

    def run(self):
        """Execute a subprocess and keep the pointer to that subprocess."""
        start_time = time.time()
        self.process = self.judge_as_subprocess()
        # TODO(djshuckerow): Get this thread to join() properly.
        while self.process.poll() is None:
            time.sleep(0.1)
            if time.time() > start_time + self.limit:
                # The try is to avoid a race condition where the process
                # finishes between the if and the kill statements.
                try:
                    self.process.kill()
                except:
                    pass

    def judge_as_subprocess(self):
        """Run the program to judge as a subprocess.

        This routes the output to /data/submits/job/out. The input is read from
        the location at which it is supposed to be found:

        <code>
        /data/problems/pid/in(test_num).txt.
        </code>
        """
        submit, uploaded_file = self.submit, self.uploaded_file
        in_file, out_file = self.in_file, self.out_file
        directory = directory_for_submission(submit)
        filename = uploaded_file.filename
        name, _ = filename.rsplit('.', 1)
        input_path = os.path.join(directory_for_problem(submit), 'in')
        output_path = os.path.join(directory, 'out')
        return subprocess.Popen(
            shlex.split(RUN_COMMAND[self.submit.file_type].format(directory, name)),
            stdin=open(os.path.join(input_path, in_file)),
            stdout=open(os.path.join(output_path, out_file), 'w'),
            stderr=open(os.path.join(directory, 'error.txt'), 'w'))
