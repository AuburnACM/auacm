import time
import os

from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.util import serve_response, serve_error
from app.modules.submission_manager import models
from app.modules.submission_manager import judge
from app.modules.problem_manager.models import ProblemData
from app.database import session
from sqlalchemy.orm import load_only


def directory_for_submission(submission):
    return os.path.join(
        app.config['DATA_FOLDER'], 'submits', str(submission.job))


@app.route("/api/submit", methods=["POST"])
@login_required
def submit():
    """
    Retrieves the submission information from the request, creates a submission,
    then begins the submissions execution. The response is simply a submission
    identifier of the new submission.

    :return: serves a 200 request code and an empty object if it is a good
            request, 403 if the filetype is unsupproted, 400 if the required
            fields are missing.
    """

    uploaded_file = request.files['file']
    if not uploaded_file:
        return serve_error('file must be uploaded', response_code=400)
    if not judge.allowed_filetype(uploaded_file.filename):
        return serve_error('filename not allowed', response_code=403)
    if not request.form['pid']:
        return serve_error('the field \'pid\' must be specified',
            response_code=400)

    # Obtain the time limit for the problem
    time_limit = session.query(ProblemData).\
            options(load_only("pid", "time_limit")).\
            filter(ProblemData.pid==request.form['pid']).\
            first().time_limit


    ext = uploaded_file.filename.split('.')[-1].lower()
    if 'python' in request.form:
        ext = request.form['python']

    attempt = models.Submission(
        username=current_user.username,
        pid=request.form['pid'],
        submit_time=int(time.time()),
        auto_id=0,
        file_type=ext,
        result='start')

    attempt.commit_to_session()

    directory = directory_for_submission(attempt)
    os.mkdir(directory)
    uploaded_file.save(os.path.join(directory, uploaded_file.filename))

    judge.Judge(attempt, uploaded_file, time_limit).run_threaded()

    return serve_response({
        'submissionId': attempt.job
    })


@app.route('/api/submit/job/<int:job_id>')
def get_submit(job_id):
    """Get the current status of a submission based on its id"""
    submit = (session.query(models.Submission)
              .filter(models.Submission.job == job_id).first())

    if not submit:
        return serve_error('Submission with job id ' + str(job_id) +
                           ' not found', 401)

    return serve_response(submit.to_dict())


@app.route('/api/submit/user/<username>')
@app.route('/api/submit/user/<username>/<int:limit>')
def get_submit_for_user(username, limit=100):
    """
    Return a number of submit for a user, chronologically (most recent first)

    :param username: The user to collect submits for ('all' will return from
                     all users).
    :param limit:    The number of submits to pull, max 100
    """
    limit = min(limit, 100)

    submits = (session.query(models.Submission)
               .order_by(models.Submission.submit_time.desc()))

    # Filter by user if provided
    if username != 'all':
        submits = submits.filter(models.Submission.username == username)

    result = submits.limit(limit).all()

    if not result:
        return serve_error('No submissions found for username ' +
                           str(username), 401)

    return serve_response([s.to_dict() for s in result])