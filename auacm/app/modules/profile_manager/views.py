"""
Profile-related routes and methods.
"""

import os
from os.path import join
import mimetypes
import base64

from sqlalchemy import and_
from flask import send_file, request

from app.database import get_session
from app.modules import app
from app.modules.blog_manager.models import BlogPost
from app.modules.competition_manager.models import CompUser, Competition
from app.modules.profile_manager.models import AttemptSession
from app.modules.submission_manager.models import Submission, ProblemSolved
from app.modules.user_manager.models import User
from app.util import serve_response, serve_error

MAX_RECENT_ATTEMPTS = 5
MAX_RECENT_COMPETITIONS = 5
MAX_RECENT_BLOG_POSTS = 3

IMAGE_DIR = join(app.config['DATA_FOLDER'], 'profile')

@app.route('/api/profile/image/<username>', methods=['GET'])
def get_profile_image(username='tester'):
    """
    Return a user's profile picture.
    """

    imagefile = [filename for filename in os.listdir(
        join(app.config['DATA_FOLDER'], 'profile')) if
                 filename.startswith(username + '.')]

    if len(imagefile) == 0:
        return send_file(join(IMAGE_DIR, 'default', 'profile.png'),
                         mimetype='image/png')
    else:
        file_ext = '.' + imagefile[0].split('.', 1)[1]
        mime_type = mimetypes.types_map[file_ext]
        return send_file(join(IMAGE_DIR, imagefile[0]), mimetype=mime_type)

@app.route('/api/profile/image/<username>', methods=['PUT'])
def set_profile_image(username='tester'):
    """
    Set a user's profile picture.
    """
    session = get_session()
    user = session.query(User).filter(
        User.username == username).first()
    if user is None:
        return serve_error('user does not exist', 404)

    request_json = request.get_json()
    mimetype = request_json['mimetype']
    filedata = request_json['data'].encode('utf-8')

    file_ext = mimetypes.guess_extension(mimetype)
    if file_ext is None:
        return serve_error('invalid mime type', 400)

    imagefiles = [filename for filename in os.listdir(IMAGE_DIR)
                  if filename.startswith(username + '.')]

    for filename in imagefiles:
        os.remove(join(IMAGE_DIR, filename))

    filename = join(IMAGE_DIR, username + file_ext)

    with open(filename, "wb") as writefile:
        writefile.write(base64.decodebytes(filedata))

    return serve_response({
        'message': 'Image saved'
    })

@app.route('/api/profile/userprofile/<username>', methods=['GET'])
def get_profile(username='tester'):
    """
    Return a user's profile.
    """
    session = get_session()
    user = session.query(User).filter(
        User.username == username).first()
    if user is None:
        return serve_error('user does not exist', 404)

    problems_solved = len(session.query(ProblemSolved).filter(
        ProblemSolved.username == username).all())

    return serve_response({
        'displayName': user.display,
        'recentAttempts': get_recent_attempts(username),
        'recentCompetitions': get_recent_competitions(username),
        'recentBlogPosts': get_recent_blog_posts(username),
        'problemsSolved': problems_solved
    })

def get_recent_blog_posts(username):
    """
    Returns a list of data pertaining to the most recent blog posts that
    the given user has made. The maximum number of competitions returned is
    defined by MAX_RECENT_BLOG_POSTS.
    """
    session = get_session()
    recent_blog_posts = list()

    for blog_post in session.query(BlogPost).filter(
            BlogPost.username == username).order_by(BlogPost.post_time.desc()):
        recent_blog_posts.append({
            'title': blog_post.title,
            'subtitle': blog_post.subtitle,
            'postTime': blog_post.post_time * 1000,
            'id': blog_post.id
        })
        if len(recent_blog_posts) >= MAX_RECENT_BLOG_POSTS:
            break

    return recent_blog_posts

def get_recent_competitions(username):
    """
    Returns the data for the competitions most recently participated
    in by the user. The maximum number of competitions returned is
    defined by MAX_RECENT_COMPETITIONS.
    """
    session = get_session()
    recent_competitions = list()

    for comp_user in session.query(CompUser).filter(
            CompUser.username == username).order_by(CompUser.cid.desc()):

        cid = comp_user.cid
        comp = session.query(Competition).filter(
            Competition.cid == cid).first()

        team_size = len(session.query(CompUser).filter(and_(
            CompUser.cid == cid, CompUser.team == comp_user.team)).all())
        recent_competitions.append({
            'teamName': comp_user.team,
            'cid': cid,
            'compName': comp.name,
            'teamSize': team_size
        })
        if len(recent_competitions) >= MAX_RECENT_COMPETITIONS:
            break
    return recent_competitions

def get_recent_attempts(username):
    """
    Returns the data for the problems most recently attempted by the user.
    The maximum number of problems returned is defined by MAX_RECENT_ATTEMPTS.
    """
    session = get_session()
    last_submit = None
    attempt_session = None
    recent_attempts = list()

    # For each of the user's most recent submissions:
    for recent_submit in session.query(Submission).filter(
            Submission.username == username).order_by(Submission.job.desc()):

        # If this is the most recent or different from the previous:
        if last_submit is None or last_submit.pid != recent_submit.pid:
            # Add the previous batch of submissions to the list of most
            # recent attempts (if applicable)
            if attempt_session is not None:
                recent_attempts.append(attempt_session.to_dict())
                # If we hit the limit, break.
                if len(recent_attempts) >= MAX_RECENT_ATTEMPTS:
                    break
            # Create a new attempt session from this submit.
            attempt_session = AttemptSession(recent_submit)

        # Otherwise, if this is another submission on the same problem:
        else:
            # Batch it up in the same attempt session.
            attempt_session.add_submission(recent_submit)
        last_submit = recent_submit

    # If we haven't hit the cap, but we have an unused attempt session, add it.
    if (len(recent_attempts) < MAX_RECENT_ATTEMPTS
            and attempt_session is not None):
        recent_attempts.append(attempt_session.to_dict())

    return recent_attempts
