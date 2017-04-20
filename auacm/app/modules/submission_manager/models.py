"""Provide a model object for handling submits."""
import threading

from app.database import database_base, get_session
from app.modules.problem_manager.models import Problem

DATABASE_LOCK = threading.Lock()


class Submission(database_base):
    """Submission class that we build by reflecting the mysql database.

    This class also has some methods for adjusting its status.
    """

    session = get_session()

    __tablename__ = "submits"

    def __init__(self, **kwargs):
        database_base.__init__(self, **kwargs)

    def commit_to_session(self):
        """Commit this Submission to the database.

        This is useful for adding a newly-created Submission to the database.
        """
        session = get_session()
        DATABASE_LOCK.acquire()
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        DATABASE_LOCK.release()
        self._problem = None

    def update_status(self, status):
        """Updates status in the database.

        :param status: the status of the submission
        :return: None
        """
        session = get_session()
        self.result = status
        DATABASE_LOCK.acquire()

        # Add to problem_solved if solved for first time
        if status == 'good' and not (
                session.query(ProblemSolved).filter(
                    ProblemSolved.pid == self.pid).filter(
                        ProblemSolved.username == self.username).all()):
            session.add(ProblemSolved(
                username=self.username, pid=self.pid,
                submit_time=self.submit_time))

        session.flush()
        session.commit()
        DATABASE_LOCK.release()

    def get_problem(self):
        """Find the problem that this submit is associated with."""
        session = get_session()
        if self._problem is None:
            self._problem = (
                session.query(Problem)
                .filter(Problem.pid == self.pid)
                .first())
        return self._problem

    def to_dict(self):
        """Return this object as a dictionary"""
        return {
            'job_id': self.job,
            'pid': self.pid,
            'username': self.username,
            'submit_time': self.submit_time,
            'file_type': self.file_type,
            'status': self.result
        }

class ProblemSolved(database_base):
    """Reflects problem_solved table of the database"""

    def __init__(self, *_args, **kwargs):
        database_base.__init__(self, **kwargs)

    __tablename__ = 'problem_solved'


MOCK_PROBLEM_TIMEOUT = 1


class MockSubmission(database_base):
    """Mock submissions class to use in tests.

    This class contains all the data that Submission does, however it doesn't
    modify the database or emit any status.
    """

    __tablename__ = "submits"

    def __init__(self, **kwargs):
        database_base.__init__(self, **kwargs)
        # The MockSubmission will also mock relevant data about the problem.
        self.time_limit = MOCK_PROBLEM_TIMEOUT
        if not self.job:
            self.job = "mocksubmit"

    def commit_to_session(self):
        """Commits the MockSubmission to the database."""
        pass

    def update_status(self, status_new):
        """Updates this MockSubmission's status"""
        self.result = status_new

    def get_problem(self):
        """MockSubmission contains everything relevant to its problem."""
        return self
