'''
Reflection and utilities for the users database table.
'''

from app.modules.problem_manager.models import Problem
from app.database import DATABASE_SESSION

class AttemptSession:
    '''Object that represents a user's work on a single problem.

    For example, if a user submits on problem "Foo" 3 times in
    succession, with an incorrect, a TLE, and a correct, this
    would correspond to 1 AttemptSession with a total of 3
    submits that was eventually correct.
    '''

    def __init__(self, submission):
        ''' Constructor. Takes a submission_manager Submission.'''
        self.count = 1
        self.pid = submission.pid
        self.job_ids = list()
        self.job_ids.append(submission.job)
        self.correct = (submission.result == 'good')
        problem = DATABASE_SESSION.query(Problem).filter(
            Problem.pid == self.pid).first()
        self.shortname = problem.shortname
        self.name = problem.name

    def add_submission(self, submission):
        ''' Given a submission_manager Submission, add it to the
        list of submissions on the problem.'''
        if submission.pid != self.pid:
            raise ValueError("Different pid than before")
        self.count = self.count + 1
        self.job_ids.append(submission.job)
        self.correct = self.correct or (submission.result == 'good')

    def to_dict(self):
        ''' Return this object as a dictionary.'''
        return {
            'submissionCount': self.count,
            'pid': self.pid,
            'submissionIds': self.job_ids,
            'correct': self.correct,
            'shortname': self.shortname,
            'name': self.name
        }
