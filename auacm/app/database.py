'''Database handlers.'''
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

from app import app

# Use the test DB if specified, otherwise the default
Base = automap_base()
engine = create_engine('mysql+pymysql://acm@localhost/acm?charset=utf8')
connection = engine.connect()
session = Session(engine)

# Set up the test database connection
test_engine = create_engine(
    'mysql+pymysql://root@localhost/acm_test?charset=utf8')
test_connection = test_engine.connect()
test_session = Session(test_engine)

# Import all the ORM classes
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission, MockSubmission
from app.modules.scoreboard_manager.models import Competition, CompUser, CompProblem
from app.modules.problem_manager.models import Problem, ProblemData, SampleCase
Base.prepare(engine, reflect=True)
