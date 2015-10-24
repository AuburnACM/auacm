'''Database handlers.'''
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

# database setup
Base = automap_base()
from app.modules.user_manager.models import User
engine = create_engine('mysql+pymysql://acm@localhost/acm?charset=utf8')
from app.modules.scoreboard_manager.models import Competition, CompUser, CompProblem
connection = engine.connect()
Base.prepare(engine, reflect=True)
session = Session(engine)
