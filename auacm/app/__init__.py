"""
This is the main module of the python project.
"""
import time
from app import database
from app import util
import app.views
import app.modules.user_manager.views
import app.modules.submission_manager.views
import app.modules.competition_manager.views
import app.modules.blog_manager.views
import app.modules.problem_manager.views
import app.modules.profile_manager.views

from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission, MockSubmission
from app.modules.competition_manager.models import (
	   Competition, CompUser, CompProblem)
from app.modules.problem_manager.models import Problem, ProblemData, SampleCase
from app.modules.blog_manager.models import BlogPost
from app.database import database_base, database_engine
database_base.prepare(database_engine, reflect=True)
