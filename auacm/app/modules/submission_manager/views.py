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
from app.modules.submission_manager import judge
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
def on_connection():
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
                         file_type=uploaded_file.filename.split('.')[-1].lower(),
                         result='start')
    session.add(attempt)
    session.flush()
    session.commit()
    session.refresh(attempt)
    problem = session.query(Base.classes.problems) \
        .filter(Base.classes.problems.pid == submission.pid).first()
    thread = Thread(
        target=judge.evaluate, args=(attempt, uploaded_file, problem))
    thread.start()
    return serve_response({
        'submissionId' : attempt.job
    })


def update_submission_status(submission, status):
    """
    Updates the submission's status in the database.

    :param submission: the newly created submission
    :param status: the status of the submission
    :return: None
    """
    submission.result = status
    dblock.acquire()
    session.flush()
    session.commit()
    dblock.release()

 
def emit_submission_status(submission, status, test_num):
    """
    Shares the status of a submission with the client via a web socket.
    
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
