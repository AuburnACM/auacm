'''Reflection and utilities for the users database table.'''

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
            'submission_count': self.count,
            'pid': self.pid,
            'submission_ids': self.job_ids,
            'correct': self.correct
        }
