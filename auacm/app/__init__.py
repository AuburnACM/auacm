from flask import Flask, Response
from flask.ext.login import LoginManager
from flask.ext.bcrypt import Bcrypt
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base
import os.path


# flask setup
app = Flask(__name__)
app.config.from_pyfile('config.py') # not sure if this works


# Initialize handler functions.
from app import views
