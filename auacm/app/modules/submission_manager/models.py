'''Provide a model object for handling submits.'''
import threading

from app.database import Base, session
from app.modules.problem_manager.models import Problem

dblock = threading.Lock()


class Submission(Base):
    '''Submission class that we build by reflecting the mysql database.

    This class also has some methods for adjusting its status.
    '''

    __tablename__ = "submits"

    def __init__(self, **kwargs):
        Base.__init__(self, **kwargs)

    def commit_to_session(self):
        '''Commit this Submission to the database.

        This is useful for adding a newly-created Submission to the database.
        '''
        dblock.acquire()
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        dblock.release()
        self._problem = None

    def update_status(self, status):
        '''Updates status in the database.

        :param status: the status of the submission
        :return: None
        '''
        self.result = status
        dblock.acquire()

        # Add to problem_solved if solved for first time
        if status == 'good' and not (session.query(ProblemSolved)
                .filter(ProblemSolved.pid == self.pid)
                .filter(ProblemSolved.username == self.username)):
            session.add(ProblemSolved(username=self.username, pid=self.pid))

        session.flush()
        session.commit()
        dblock.release()

    def get_problem(self):
        '''Find the problem that this submit is associated with.'''
        if self._problem is None:
            self._problem = (
                session.query(Problem)
                    .filter(Problem.pid == self.pid)
                    .first())
        return self._problem

class ProblemSolved(Base):
    """Reflects problem_solved table of the database"""

    def __init__(self, *args, **kwargs):
        Base.__init__(self, **kwargs)

    __tablename__ = 'problem_solved'


MOCK_PROBLEM_TIMEOUT = 1


class MockSubmission(Base):
    '''Mock submissions class to use in tests.

    This class contains all the data that Submission does, however it doesn't
    modify the database or emit any status.
    '''

    __tablename__ = "submits"

    def __init__(self, **kwargs):
        Base.__init__(self, **kwargs)
        # The MockSubmission will also mock relevant data about the problem.
        self.time_limit = MOCK_PROBLEM_TIMEOUT
        self.job = "mocksubmit"

    def commit_to_session(self):
        pass

    def update_status(self, status):
        self.result = status

    def get_problem(self):
        '''MockSubmission contains everything relevant to its problem.'''
        return self
