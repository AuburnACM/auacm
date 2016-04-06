'''Database handlers.'''
import os
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

DB_HOST = os.environ.get('DOCKER_HOST', 'localhost')
ENGINE = 'mysql+pymysql://root@{}/acm?charset=utf8'.format(DB_HOST)

# Create global database variables
Base = automap_base()
engine = create_engine(ENGINE)
connection = engine.connect()
session = Session(engine)

# Import all the ORM classes
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission, MockSubmission
from app.modules.competition_manager.models import Competition, CompUser, CompProblem
from app.modules.problem_manager.models import Problem, ProblemData, SampleCase
from app.modules.blog_manager.models import BlogPost
Base.prepare(engine, reflect=True)
