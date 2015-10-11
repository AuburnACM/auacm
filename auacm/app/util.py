'''Utility functions and objects for the auacm server.'''

from flask import send_from_directory, jsonify
from flask.ext.login import LoginManager
from flask.ext.bcrypt import Bcrypt
from app import app

# bcrypt setup
bcrypt = Bcrypt(app)


# login session setup
login_manager = LoginManager()
login_manager.init_app(app)


def serve_html(filename):
    '''Serve static HTML pages.'''
    return send_from_directory(app.static_folder+"/html/", filename)


def serve_response(response, response_code=200):
    '''Serve json containing a response to a request.'''
    return jsonify({'status': response_code, 'data': response}), response_code


def serve_error(error, response_code):
    '''Serve an error in response to a request.'''
    return jsonify({'status': response_code, 'error': error}), response_code
