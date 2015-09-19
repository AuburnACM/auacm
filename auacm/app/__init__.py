from flask import Flask, Response
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base
import os.path

app = Flask(__name__)
app.config.from_pyfile('config.py') # not sure if this works

Base = automap_base()

engine = create_engine('mysql://acm@localhost/acm')
connection = engine.connect()
Base.prepare(engine, reflect=True)

session = Session(engine)

# dirty hack from StackOverflow to allow us to serve static html
def serve_html(filename):
    try:
        src = os.getcwd() + '/app/static/html/' + filename
        return Response(open(src).read(), mimetype="text/html")
    except IOError as exc:
        return str(exc)

from app import views
