from flask import request
from flask.ext.login import login_user, logout_user, current_user, login_required
from app import app
from app.database import Base, session
from app.util import login_manager, serve_html, serve_response, serve_error
from .models import Submission
import os, time

ALLOWED_EXTENSIONS = ['java', 'c', 'cpp']

def allowed_filetype(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

@app.route("/api/submit", methods=["POST"])
@login_required
def submit():
    file = request.files['file']
    if not allowed_filetype(file.filename):
        return serve_error('filename not allowed', response_code=403)
    attempt = Submission(username=current_user.username,\
            pid=int(request.form['pid']),\
            submit_time=int(time.time()),\
            auto_id=0,\
            file_type=file.filename.rsplit('.', 1)[1],\
            result='compile')
    session.add(attempt)
    session.flush()
    session.refresh(attempt)
    directory = os.path.join(app.config['DATA_FOLDER'], str(attempt.job))
    os.mkdir(directory)
    file.save(os.path.join(directory, file.filename))
    return serve_response('{}')
