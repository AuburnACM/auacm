from flask import render_template, request
from flask.ext.login import login_user, logout_user, current_user, login_required
from app import app, socketio
from app.database import Base, session
from app.util import bcrypt, login_manager, serve_info_pdf, serve_html, serve_response, serve_error, load_user
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission
from os.path import join
from time import time

from gevent import monkey
monkey.patch_all()

@app.route('/')
@app.route('/index')
@login_required
def get_problems_page():
    return render_template('index.html', username=current_user.display)


@app.route('/login')
@login_manager.unauthorized_handler
def get_login_page():
    return serve_html('login.html')


@app.route('/api/login', methods=['POST'])
def log_in():
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
@login_required
def create_user():
    # Verify that the poster is an admin
    if current_user.admin == 0:
        return server_error('Must be admin to create users', 401)

    # Get form contents
    username = request.form['username']
    password = request.form['password']
    display = request.form['display']

    # Create the user if doesn't already exist
    user = load_user(username)
    if user is None:
        hashed = bcrypt.generate_password_hash(password)
        user = User(username=username, passw=hashed, display=display, admin=0)
        session.add(user)
        session.flush()
        session.commit()
        return serve_response({})
    return serve_error('username already exists', 401)


@app.route('/api/change_password', methods=['POST'])
@login_required
def change_password():
    oldPassword = request.form['oldPassword']
    newPassword = request.form['newPassword']
    if bcrypt.check_password_hash(current_user.passw, oldPassword):
        hashed = bcrypt.generate_password_hash(newPassword)
        current_user.passw = hashed
        session.add(current_user)
        session.flush()
        session.commit()
        return serve_response({})
    return serve_error('old password does not match', 401)


@app.route('/api/logout')
@login_required
def log_out():
    logout_user()
    return serve_response({})


@app.route('/api/me')
@login_required
def get_me():
    return serve_response({
        'username'   : current_user.username,
        'displayName': current_user.display,
        'isAdmin'    : current_user.admin
    })
