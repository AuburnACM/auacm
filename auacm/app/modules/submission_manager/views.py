import time
from threading import Thread

from flask import request
from flask.ext.login import current_user, login_required
from app import app, socketio
from app.util import serve_response, serve_error
from app.modules.submission_manager import models
from app.modules.submission_manager import judge


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
    if not judge.allowed_filetype(uploaded_file.filename):
        return serve_error('filename not allowed', response_code=403)
    if not request.form['pid']:
        return serve_error('the field \'pid\' must be specified', response_code=400)

    attempt = models.Submission(
        username=current_user.username,
        pid=request.form['pid'].lower(),
        submit_time=int(time.time()),
        auto_id=0,
        file_type=uploaded_file.filename.split('.')[-1].lower(),
        result='start')
    thread = Thread(
        target=judge.evaluate, args=(attempt, uploaded_file))
    thread.daemon = False
    thread.start()
    return serve_response({
        'submissionId' : attempt.job
    })
