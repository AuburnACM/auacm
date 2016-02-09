from flask import Flask, Response
import time

# Create the God-objects to be used everywhere
app = Flask(__name__)
app.config.from_pyfile('config.py')
test_app = app.test_client()

# Initialize handler functions.
from app import util
from app import views
from app.modules.submission_manager import views
from app.modules.competition_manager import views
from app.modules.blog_manager import views
from app.modules.problem_manager import views
