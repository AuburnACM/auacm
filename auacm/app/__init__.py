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

# bcrypt setup
bcrypt = Bcrypt(app)

# database setup
Base = automap_base()
engine = create_engine('mysql://acm@localhost/acm')
connection = engine.connect()
Base.prepare(engine, reflect=True)
session = Session(engine)

# login session setup
login_manager = LoginManager()
login_manager.init_app(app)

from app.modules.user_manager.models import User
@login_manager.user_loader
def load_user(user_id):
    result = session.query(Base.classes.users).filter(Base.classes.users.username=='will').first()
    if result:
        return User(result)
    else:
        return None

# dirty hack from StackOverflow to allow us to serve static html
def serve_html(filename):
    try:
        src = os.getcwd() + '/app/static/html/' + filename
        return Response(open(src).read(), mimetype="text/html")
    except IOError as exc:
        return str(exc)

from app import views
