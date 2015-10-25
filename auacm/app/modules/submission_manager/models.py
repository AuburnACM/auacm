'''Provide a model object for handling submits.'''
import threading

from app import socketio
from app.database import Base, session

dblock = threading.Lock()


class Submission(Base):
    '''Submission class that we build by reflecting the mysql database.

    This class also has some methods for adjusting its status.
    '''

    __tablename__ = "submits"

    def __init__(self, **kwargs):
        Base.__init__(self, **kwargs)

    def commit_to_session(self):
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
        session.flush()
        session.commit()
        dblock.release()

    def emit_status(self, status, test_num):
        '''Shares status with the clientvia a web socket.

        :param self: the newly created submission
        :param status: the status of the submission
        :return: None
        '''
        socketio.emit(
            'status',
            {
                'submissionId': self.job,
                'problemId': self.pid,
                'username': self.username,
                'submitTime': self.submit_time * 1000,  # to milliseconds
                'testNum': test_num,
                'status': status
            },
            namespace='/judge')

    def get_problem(self):
        '''Find the problem that this submit is associated with.'''
        if self._problem is None:
            self._problem = (
                session.query(Base.classes.problems)
                    .filter(Base.classes.problems.pid == self.pid)
                    first())
        return self._problem


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

    def update_status(self, status):
        self.result = status

    def emit_status(self, status, test_num):
        pass

    def get_problem(self):
        '''MockSubmission contains everything relevant to its problem.'''
        return self
