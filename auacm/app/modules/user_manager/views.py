"""User-related routes and methods"""

from flask import request
from flask.ext.login import login_user, logout_user, current_user, login_required
from app import app
from app.util import bcrypt, login_manager, serve_response, serve_error, load_user, admin_required
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission, ProblemSolved
import app.database as database
from time import time


@app.route('/api/login', methods=['POST'])
def log_in():
    """Log in a user as the current user"""
    username = request.form['username']
    password = request.form['password']
    user = load_user(username)
    if user:
        hashed = user.passw
        if bcrypt.check_password_hash(hashed, password):
            # everything's gucci
            login_user(user)
            return serve_response({})
    return serve_error('invalid username or password', 401)


@app.route('/api/create_user', methods=['POST'])
@admin_required
def create_user():
    """Create a new user"""
    # Get form contents
    username = request.form['username']
    password = request.form['password']
    display = request.form['display']

    # Create the user if doesn't already exist
    user = load_user(username)
    if user is None:
        hashed = bcrypt.generate_password_hash(password)
        user = User(username=username, passw=hashed, display=display, admin=0)
        user.commit_to_session()
        return serve_response({})
    return serve_error('username already exists', 401)


@app.route('/api/change_password', methods=['POST'])
@login_required
def change_password():
    """Change the password of an existing user"""
    oldPassword = request.form['oldPassword']
    newPassword = request.form['newPassword']
    if bcrypt.check_password_hash(current_user.passw, oldPassword):
        hashed = bcrypt.generate_password_hash(newPassword)
        current_user.passw = hashed
        current_user.commit_to_session()
        return serve_response({})
    return serve_error('old password does not match', 401)


@app.route('/api/logout')
@login_required
def log_out():
    """Logout the current user"""
    logout_user()
    return serve_response({})


@app.route('/api/me')
@login_required
def get_me():
    """Obtain data about the current user"""
    return serve_response({
        'username': current_user.username,
        'displayName': current_user.display,
        'isAdmin': current_user.admin,
        'avatar': current_user.gravatar()
    })


@app.route('/api/ranking', methods=['GET'])
@app.route('/api/ranking/<timeframe>', methods=['GET'])
def get_ranking(timeframe='all'):
    """
    Return the users in order of how many problems are solved in the time frame
    """
    now = int(time())
    frame_lengths = {
        'all': lambda x: True,
        'day': lambda x: now - x < 60 * 60 * 24,                 # 24 hours
        'week': lambda x: now - x < (60 * 60 * 24) * 7,          # 7 days
        'month': lambda x: now - x < (60 * 60 * 24) * 7 * 4,     # 4 weeks
        'year': lambda x: now  - x < (60 * 60 * 24) * 7 * 52,    # 52 weeks
    }
    if timeframe not in frame_lengths.keys():
        timeframe = 'all'


    ranks = list()
    for username, display in [(u.username, u.display) for u in
                              database.session.query(User).all()]:
        solves = (database.session.query(ProblemSolved)
                  .filter(ProblemSolved.username == username).all())

        # Only count solves if they meet the time criteria
        num_solved = len([s for s in solves
                          if frame_lengths[timeframe](s.submit_time)])

        # Don't add users that haven't solved at least one problem
        if num_solved > 0:
            ranks.append({
                'username': username,
                'displayName': display,
                'solved': num_solved
            })

    # Sort the ranks by problems solved
    rank = 1
    ranks = sorted(ranks, key=lambda k: k['solved'])[::-1]
    for r in ranks:
        r['rank'] = rank
        rank += 1

    return serve_response(ranks)
