"""
Utility functions and objects for the auacm server.
"""

from functools import wraps
import json
from os.path import join
import unittest

from flask import send_from_directory, jsonify
from flask.ext.login import LoginManager, current_user
from flask.ext.bcrypt import Bcrypt
from app.modules import app, test_app
from app.database import DATABASE_SESSION
from app.modules.user_manager.models import User

# bcrypt setup
BCRYPT_CONST = Bcrypt(app)

# login session setup
LOGIN_MANAGER = LoginManager()
LOGIN_MANAGER.init_app(app)

@LOGIN_MANAGER.user_loader
def load_user(user_id):
    '''Log a user into the app.'''
    return DATABASE_SESSION.query(User)\
        .filter(User.username == user_id).first()

# Functions for serving responses
def serve_html(filename):
    '''Serve static HTML pages.'''
    return send_from_directory(app.static_folder+"/html/", filename)


def serve_info_pdf(pid):
    '''Serve static PDFs.'''
    return send_from_directory(join(app.config['DATA_FOLDER'],
                                    'problems', pid), 'info.pdf')


def serve_response(response, response_code=200):
    '''Serve json containing a response to a request.'''
    return jsonify({'status': response_code, 'data': response}), response_code


def serve_error(error, response_code):
    '''Serve an error in response to a request.'''
    return jsonify({'status': response_code, 'error': error}), response_code

def admin_required(function):
    '''Checks to see if a user has to be an admin to access a certain part of the api.'''
    @wraps(function)
    def wrap(*args, **kwargs):
        '''Wraps the function passed to admin_required and returns
           if the current user is an admin.'''
        if current_user.is_anonymous or current_user.admin == 0:
            return serve_error('You need to be an admin to do that.',
                               response_code=401)
        else:
            return function(*args, **kwargs)
    return wrap


class AUACMTest(unittest.TestCase):
    """A base test class for AUACM tests"""

    @classmethod
    def setUpClass(cls):
        """One time setup for the class of tests"""
        cls.username = app.config['TEST_USERNAME']
        cls.password = app.config['TEST_PASSWORD']

    def login(self):
        """Log the test user into the app"""
        response = json.loads(test_app.post(
            '/api/login',
            data=dict(username=self.username, password=self.password)
        ).data.decode())
        assert response['status'] == 200

    def logout(self):
        """Log the test user out of the app"""
        response = json.loads(test_app.get('/api/logout').data.decode())
        assert response['status'] == 200

    def insert_into_db(self, session_test, model, args_list):
        """
        Insert a number of ORM objects into the session for testing. The number
        of objects inserted is equal to the length of the args_list parameter.

        :param session_test: the database session to insert into
        :param model: the model class of the objects to be added
        :param args: a list of the arguments to pass to the model constructor
        :param num: the number of ORM objects to create and insert
        :returns: the list of new ORM objects
        """
        results = list()
        for args in args_list:
            model_object = model(**args)
            session_test.add(model_object)
            session_test.flush()
            session_test.commit(self)
            session_test.refresh(model_object)
            results.append(model_object)

        return results
