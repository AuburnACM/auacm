'''Provide a model object for handling submits.'''
import threading

from app import socketio
from app.database import Base, session

dblock = threading.Lock()


class Submission(Base.classes.submits):
    def __init__(self. *args, **kwargs):
        Base.classes.submits.__init__(self, *args, **kwargs)
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        problem = (
            session.query(Base.classes.problems)
                .filter(Base.classes.problems.pid == self.pid)
                .first()
        )

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
                'submitTime': self.submit_time * 1000, # to milliseconds
                'testNum': test_num,
                'status': status
            },
            namespace='/judge')
