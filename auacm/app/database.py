'''Database handlers.'''
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

# database setup
Base = automap_base()
from app.modules.user_manager.models import User
engine = create_engine('mysql://acm@localhost/acm')
connection = engine.connect()
Base.prepare(engine, reflect=True)
session = Session(engine)
