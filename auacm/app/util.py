"""Utility functions and objects for the auacm server."""

from flask import send_from_directory, jsonify
from flask.ext.login import LoginManager, current_user
from flask.ext.bcrypt import Bcrypt
from app import app
from app.database import session
from app.modules.user_manager.models import User
from os.path import join
from functools import wraps


# bcrypt setup
bcrypt = Bcrypt(app)

# login session setup
login_manager = LoginManager()
login_manager.init_app(app)

@login_manager.user_loader
def load_user(user_id):
    '''Log a user into the app.'''
    return session.query(User).filter(User.username==user_id).first()

# Functions for serving responses
def serve_html(filename):
    '''Serve static HTML pages.'''
    return send_from_directory(app.static_folder+"/html/", filename)


def serve_info_pdf(pid):
    '''Serve static PDFs.'''
    return send_from_directory(join(app.config['DATA_FOLDER'], 'problems', pid), 'info.pdf')


def serve_response(response, response_code=200):
    '''Serve json containing a response to a request.'''
    return jsonify({'status': response_code, 'data': response}), response_code


def serve_error(error, response_code):
    '''Serve an error in response to a request.'''
    return jsonify({'status': response_code, 'error': error}), response_code

def admin_required(function):
    @wraps(function)
    def wrap(*args, **kwargs):
        if current_user.is_anonymous or current_user.admin == 0:
            return serve_error('You need to be an admin to do that.',
                    response_code=401)
        else:
            return function(*args, **kwargs)
    return wrap
