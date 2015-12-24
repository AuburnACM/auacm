'''Database handlers.'''
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

from app import app

# Use the test DB if specified, otherwise the default
Base = automap_base()
if not app.config['TEST']:
    app.logger.info('NOT using test database')
    engine = create_engine('mysql+pymysql://acm@localhost/acm?charset=utf8')
else:
    app.logger.info('Using test database')
    engine = create_engine('mysql+pymysql://root@localhost/acm_test?charset=utf8')
connection = engine.connect()
session = Session(engine)

# Import all the ORM classes
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission, MockSubmission
from app.modules.scoreboard_manager.models import Competition, CompUser, CompProblem
from app.modules.problem_manager.models import Problem, ProblemData, SampleCase
Base.prepare(engine, reflect=True)

# Switch to test database for unit testing
def use_test_db():
    app.logger.info('Swtiching to test database')
    Base = automap_base()
    engine = create_engine('mysql+pymysql://root@localhost/acm_test?charset=utf8')
    connection = engine.connect()
    session = Session(engine)
    Base.prepare(engine, reflect=True) # Not sure if necessary
