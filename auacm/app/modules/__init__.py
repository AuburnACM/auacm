# pylint: disable=I0011,C0103
"""Contains our backend global variables"""
from flask import Flask

# Create the God-objects to be used everywhere
app = Flask(__name__, template_folder='../templates',
				  	     static_folder='../static')
app.config.from_pyfile('../config.py')
test_app = app.test_client()
