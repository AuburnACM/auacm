import itertools
import os
import subprocess
import threading

from os import path

from app import app


ALLOWED_EXTENSIONS = ['java', 'c', 'cpp', 'py', 'go']

COMPILE_COMMAND = {
    "java": "javac {0}/{1}.java",
    "py": "",
    "c": "gcc {0}.cc -o {0}",
    "cpp": "g++ {0}.cpp -o {0}",
    "go": "go build {0}.go"
}
RUN_COMMAND = {
    "java": "java {0}",
    "py": "python {0}.py",
    "c": "./{0}",
    "cpp": "./{0}",
    "go": "./{0}"
}
TIMEOUT_MULTIPLIER = {
    "java": 1.5,
    "py": 2,
    "c": 1,
    "cpp": 1,
    "go": 1
}
PENDING = 0
COMPILATION_ERROR = 1
COMPILATION_SUCCESS = 2
RUNTIME_ERROR = 3
TIMELIMIT_EXCEEDED = 4
WRONG_ANSWER = 5
CORRECT_ANSWER = 6


def allowed_filetype(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS

def directory_for_submission(submission):
    return join(app.config["DATA_FOLDER"], "submits", str(submission.job))
    
    
def directory_for_problem(pid):
    return join(app.config["DATA_FOLDER"], "problems", pid)


def evaluate(submission, uploaded_file, problem):
    '''Attempts to compile (if necessary) then execute a given file.

    :param submission: the newly created submission
    :param uploaded_file: the uploaded file
    :return: None
    '''
    directory = directory_for_submission(submission)
    os.mkdir(directory)
    uploaded_file.save(join(directory, uploaded_file.filename))
    status = compile_submission(submission, uploaded_file)
    if status == COMPILATION_SUCCESS:
        execute_submission(submission, uploaded_file, problem)
    return status
    

def compile_submission(submission, uploaded_file):
    '''Compile the submission.'''
    directory = directory_for_submission(submission)
    filename = uploaded_file.filename
    name, ext = filename.rsplit(".", 1)[0], filename.rsplit(".", 1)[-1]
    result = subprocess.call(
        COMPILE_COMMAND[ext].format(directory, name, ext),
        stderr=open(path.join(directory, "error.txt"), "w")
    )
    if result == 0:
        return COMPILATION_SUCCESS
    else:
        return COMPILATION_ERROR


def execute_submission(submission, uploaded_file, problem):
    '''Run the submission.'''
    problem_directory = directory_for_problem(submission.pid)
    filename = uploaded_file.filename
    name, ext = filename.rsplit(".", 1)[0], filename.rsplit(".", 1)[-1]
    input_path = path.join(problem_directory, "in")
    output_path = path.join(problem_directory, "out")
    for f in os.listdir(input_path):
        if path.isfile(f):
            test_number = int(f.split(".")[0].strip("in"))
            out_file = "out{0}.txt".format(test_number)
            # TODO(djshuckerow): emit submission status with a pipe
            submission.emit_status("running", test_number)
            execution = _JudgementThread(
                args=(submission, submission_file, f, test_number))
            execution.join(problem.timeout * TIMEOUT_MULTIPLIER[ext])
            if execution.is_alive():
                execution.process.kill()
                submission.update_status("timeout")
                submission.emit_status("timeout", test_number)
                return TIMELIMIT_EXCEEDED
            elif execution.process.returncode is not 0:
                submission.update_status("runtime")
                submission.emit_status("runtime", test_number)
                return RUNTIME_ERROR
            result_path = path.join(directory_for_submission, "out")
            with open(join(output_path, out_file)) as golden_result, \
                 open(join(result_path, out_file)) as submission_result:
                golden_lines = golden_result.readlines()
                submission_lines = submission_result.readlines()
                if len(submission_lines) != len(golden_lines):
                    submission.update_status("wrong")
                    submission.emit_status("incorrect", test_number)
                    return WRONG_ANSWER
                # Use itertools.izip instead of zip to save memory.
                for gl, sl in itertools.izip(golden_lines, submission_lines):
                    if gl.rstrip() != sl.rstrip():
                        submission.update_status("wrong")
                        submission.emit_status("incorrect", test_number)
                        return WRONG_ANSWER
    # The answer is correct if all the tests complete without any failure.
    submission.update_status("good")
    submission.emit_status("correct", test_number)
    return CORRECT_ANSWER


class _JudgementThread(threading.Thread):
    '''This thread will be used to pass judgement on a submission.'''
    
    def __init__(self, **kwargs):
        threading.Thread.__init__(self, kwargs)
        self.process = None
        
    def run():
        '''Execute a subprocess and keep the pointer to that subprocess.'''
        self.process = self.judge_as_subprocess(*self.args)

    def judge_as_subprocess(submission, uploaded_file, in_file, out_file):
        '''Returns (and starts) the process handle for the execution.
        
        It routes the output to /data/submits/job/out. The input is read from
        the location at which it is supposed to be found:
    
        <code> 
        /data/problems/pid/in(test_num).txt.
        </code>
    
        :param submission: the newly created submission
        :param uploaded_file: the file uploaded from flask
        :param in_file: the input file that is going to be read in
        :param out_file: the output file that is going to be written to
        :return: a Popen object, the new process handle
        '''
        directory = directory_for_submission(submission)
        filename = uploaded_file.filename
        name, ext = filename.rsplit(".", 1)[0], filename.rsplit(".", 1)[-1]
        input_path = join(directory_for_problem(submission.pid), 'in')
        output_path = join(directory, 'out')
        os.mkdir(output_path)
        return Popen(
            RUN_COMMAND[ext].format(directory, name, ext),
            stdin=open(path.join(input_path, in_file)),
            stdout=open(path.join(output_path, out_file), "w"),
            stderr=open(path.join(directory, "error.txt"), "w")
        )
