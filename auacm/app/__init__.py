from flask import Flask
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

app = Flask(__name__)
app.config.from_pyfile('config.py') # not sure if this works

Base = automap_base()

engine = create_engine('mysql://acm@localhost/acm')
connection = engine.connect()
Base.prepare(engine, reflect=True)

session = Session(engine)

from app import views
