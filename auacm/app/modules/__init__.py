'''Contains our backend global variables'''
from flask import Flask

# Create the God-objects to be used everywhere
APP = Flask(__name__, template_folder='../templates',\
	static_folder='../static')
APP.config.from_pyfile('../config.py')
TEST_APP = APP.test_client()
