import time
import subprocess
from subprocess import Popen
from time import sleep
from threading import Thread, Lock

from flask import request
from flask.ext.login import current_user, login_required
from app import app, socketio
from app.database import Base, session
from app.util import serve_response, serve_error
from .models import Submission
import os
from os.path import isfile, join

ALLOWED_EXTENSIONS = ['java', 'c', 'cpp', 'c++', 'py', 'go']
dblock = Lock()


def allowed_filetype(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def directory_for_submission(submission):
    return join(app.config['DATA_FOLDER'], 'submits', str(submission.job))


def directory_for_problem(pid):
    return join(app.config['DATA_FOLDER'], 'problems', pid)


@socketio.on('connect', namespace="/judge")
def onConnection():
    pass


@app.route("/api/submit", methods=["POST"])
@login_required
def submit():
    """
    Retrieves the submission information from the request, creates a submission, then begins the submissions execution.
    The response simply returns an empty object for data. (In the future, this will return the handle to a web socket).

    :return: serves a 200 request code and an empty object if it is a good request, 403 if the filetype is unsupproted,
            400 if the required fields are missing.
    """

    uploaded_file = request.files['file']
    if not uploaded_file:
        return serve_error('file must be uploaded', response_code=400)
    if not allowed_filetype(uploaded_file.filename):
        return serve_error('filename not allowed', response_code=403)
    if not request.form['pid']:
        return serve_error('the field \'pid\' must be specified', response_code=400)

    attempt = Submission(username=current_user.username,
                         pid=request.form['pid'],
                         submit_time=int(time.time()),
                         auto_id=0,
                         file_type=uploaded_file.filename.rsplit('.', 1)[1].lower(),
                         result='compile')
    session.add(attempt)
    session.flush()
    session.commit()
    session.refresh(attempt)
    directory = directory_for_submission(attempt)
    os.mkdir(directory)
    uploaded_file.save(join(directory, uploaded_file.filename))
    thread = Thread(target=start_execution, args=(attempt, uploaded_file))
    thread.start()
    return serve_response({
        'submissionId' : attempt.job
    })


def start_execution(submission, uploaded_file):
    """
    Attempts to compile (if necessary) then execute a given file.

    :param submission: the newly created submission
    :param uploaded_file: the uploaded file
    :return: None
    """
    if submission.file_type == 'java':
        if compile_java(submission, uploaded_file):
            return
    elif submission.file_type == 'c' or submission.file_type == 'cpp' or submission.file_type == 'c++':
        result = compile_c(submission, uploaded_file)
        if not result:
            return
    execute(submission, uploaded_file)


def compile_java(submission, uploaded_file):
    location = join(directory_for_submission(submission), uploaded_file.filename)
    if subprocess.call(['javac', location],
                       stderr=open(join(directory_for_submission(submission), 'error.txt'), 'w')):
        return True
    else:
        update_submission_status(submission, 'compile', -1)
        return False


def compile_c(submission, uploaded_file):
    location = join(directory_for_submission(submission), uploaded_file.filename)
    if subprocess.call(['g++', location],
                       stderr=open(join(directory_for_submission(submission), 'error.txt'), 'w')):
        return True
    else:
        update_submission_status(submission, 'compile', -1)
        return False


def update_submission_status(submission, status, test_num):
    """
    Updates the submission's status in the database.

    :param submission: the newly created submission
    :param status: the status of the submission
    :return: None
    """
    socketio.emit('status', 
            {
                'submissionId' : submission.job,
                'problemId' : submission.pid,
                'username' : submission.username,
                'submitTime' : submission.submit_time * 1000, # to milliseconds
                'testNum' : test_num,
                'status' : status
            },
            namespace='/judge')
    submission.result = status
    dblock.acquire()
    session.flush()
    session.commit()
    dblock.release()


def get_process_handle(submission, uploaded_file, in_file, test_num):
    """
    Returns (and starts) the process handle for the specified submission. It routes the output to /data/submits/job/out.
    The input is read from the location at which it is supposed to be found, /data/problems/pid/in(test_num).txt.

    :param submission: the newly created submission
    :param uploaded_file: the file uploaded from flask
    :param in_file: the input file that is going to be read in
    :param test_num: the test execution number
    :return: a Popen object, the new process handle
    """

    out_directory = join(directory_for_submission(submission), 'out')
    os.mkdir(out_directory)
    input_path = join(directory_for_problem(submission.pid), 'in')
    if submission.file_type == 'java':
        return Popen(['java', '-cp', directory_for_submission(submission), uploaded_file.filename.rsplit('.', 1)[0]],
                     stdin=open(join(input_path, in_file)),
                     stdout=open(join(out_directory, "out" + str(test_num) + ".txt"), 'w'))
    elif submission.file_type == 'c' or submission.file_type == 'cpp' or submission.file_type == 'c++':
        return Popen(join(directory_for_submission(submission), 'a.out'),
                     stdin=open(join(input_path, in_file)),
                     stdout=open(join(out_directory, "out" + str(test_num) + ".txt"), 'w'))
    elif submission.file_type == 'py':
        return Popen(['python', join(directory_for_submission(submission), uploaded_file.filename)],
                     stdin=open(join(input_path, in_file)),
                     stdout=open(join(out_directory, "out" + str(test_num) + ".txt"), 'w'))
    elif submission.file_type == 'go':
        return Popen(['go', 'run', join(directory_for_submission(submission), uploaded_file.filename)],
                     stdin=open(join(input_path, in_file)),
                     stdout=open(join(out_directory, "out" + str(test_num) + ".txt"), 'w'))


def execute(submission, submission_file):
    """
    Executes the submission with its corresponding language/execution type. It gets the problem that it is
    submitted for, then attempts to execute the program against each input file in the /data/problems/pid/in directory.
    If at any point the execution fails, it updates the problem's status and stops execution.

    :param submission: the newly created submission object from the database
    :param submission_file: the file that was uploaded
    :return: None
    """

    print 'beginning execution'
    directory = directory_for_problem(submission.pid)
    problem = session.query(Base.classes.problems) \
        .filter(Base.classes.problems.pid == submission.pid).first()
    input_path = join(directory, 'in')
    output_path = join(directory, 'out')
    input_files = [f for f in os.listdir(input_path) if isfile(join(input_path, f))]

    for in_file in input_files:
        print 'checking file'
        test_num = int(in_file.rsplit('.', 1)[0][2:])  # what the fuck
        update_submission_status(submission, 'running', test_num)
        process_handle = get_process_handle(submission, submission_file, in_file, test_num)

        timeout_time = time.time() + problem.time_limit
        while process_handle.poll() is None and time.time() < timeout_time:
            sleep(0.1)

        if process_handle.poll() is None:
            # timeout
            process_handle.terminate()
            update_submission_status(submission, 'timeout', test_num)
            print 'timeout'
            return

        if process_handle.returncode is not 0:
            update_submission_status(submission, 'runtime', test_num)
            print 'runtime'
            return

        with open(join(output_path, 'out' + str(test_num) + '.txt')) as test_output, \
                open(join(directory_for_submission(submission), 'out', 'out' + str(test_num) + '.txt')) as generated:
            test_lines = test_output.readlines()
            generated_lines = generated.readlines()

            if not len(generated_lines) == len(test_lines):
                print 'lengths different', len(generated_lines), len(test_lines)
                print 'wrong (lengths)'
                update_submission_status(submission, 'wrong', test_num)
                return

            for l1, l2 in zip(generated_lines, test_lines):
                if not l1.rstrip('\r\n') == l2.rstrip('\r\n'):
                    print 'wrong'
                    update_submission_status(submission, 'wrong', test_num)
                    return

    print 'correct'
    update_submission_status(submission, 'good', test_num)
