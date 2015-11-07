'''Database handlers.'''
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

# database setup
Base = automap_base()
engine = create_engine('mysql+pymysql://acm@localhost/acm?charset=utf8')
connection = engine.connect()
session = Session(engine)
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission, MockSubmission
from app.modules.scoreboard_manager.models import Competition, CompUser, CompProblem
from app.modules.problem_manager.models import Problem, Problem_Data, Sample_Case
Base.prepare(engine, reflect=True)

