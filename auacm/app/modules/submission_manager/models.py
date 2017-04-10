'''Provide a model object for handling submits.'''
import threading

from app.database import DATABASE_BASE, DATABASE_SESSION
from app.modules.problem_manager.models import Problem

DATABASE_LOCK = threading.Lock()


class Submission(DATABASE_BASE):
    '''Submission class that we build by reflecting the mysql database.

    This class also has some methods for adjusting its status.
    '''

    __tablename__ = "submits"

    def __init__(self, **kwargs):
        DATABASE_BASE.__init__(self, **kwargs)

    def commit_to_session(self):
        '''Commit this Submission to the database.

        This is useful for adding a newly-created Submission to the database.
        '''
        DATABASE_LOCK.acquire()
        DATABASE_SESSION.add(self)
        DATABASE_SESSION.flush()
        DATABASE_SESSION.commit()
        DATABASE_SESSION.refresh(self)
        DATABASE_LOCK.release()
        self._problem = None

    def update_status(self, status):
        '''Updates status in the database.

        :param status: the status of the submission
        :return: None
        '''
        self.result = status
        DATABASE_LOCK.acquire()

        # Add to problem_solved if solved for first time
        if status == 'good' and not (DATABASE_SESSION.query(ProblemSolved)
                                     .filter(ProblemSolved.pid == self.pid)
                                     .filter(ProblemSolved.username == self.username).all()):
            DATABASE_SESSION.add(ProblemSolved(username=self.username, pid=self.pid,
                                               submit_time=self.submit_time))

        DATABASE_SESSION.flush()
        DATABASE_SESSION.commit()
        DATABASE_LOCK.release()

    def get_problem(self):
        '''Find the problem that this submit is associated with.'''
        if self._problem is None:
            self._problem = (
                DATABASE_SESSION.query(Problem)
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

class ProblemSolved(DATABASE_BASE):
    """Reflects problem_solved table of the database"""

    def __init__(self, *_args, **kwargs):
        DATABASE_BASE.__init__(self, **kwargs)

    __tablename__ = 'problem_solved'


MOCK_PROBLEM_TIMEOUT = 1


class MockSubmission(DATABASE_BASE):
    '''Mock submissions class to use in tests.

    This class contains all the data that Submission does, however it doesn't
    modify the database or emit any status.
    '''

    __tablename__ = "submits"

    def __init__(self, **kwargs):
        DATABASE_BASE.__init__(self, **kwargs)
        # The MockSubmission will also mock relevant data about the problem.
        self.time_limit = MOCK_PROBLEM_TIMEOUT
        if not self.job:
            self.job = "mocksubmit"

    def commit_to_session(self):
        '''Commits the MockSubmission to the database.'''
        pass

    def update_status(self, status_new):
        '''Updates this MockSubmission's status'''
        self.result = status_new

    def get_problem(self):
        '''MockSubmission contains everything relevant to its problem.'''
        return self
