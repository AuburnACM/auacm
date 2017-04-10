'''
This model file contains the model for competitions.
'''
from app.database import database_base

class Competition(database_base):
    '''The competition model.'''
    __tablename__ = 'comp_names'

    def to_dict(self, user_registered=False):
        '''Returns the competition as a dictionary'''
        return {
            'cid': self.cid,
            'name': self.name,
            'startTime': self.start,
            'length': self.stop - self.start,
            'closed': self.closed == 1,
            'registered': user_registered
        }

class CompProblem(database_base):
    '''The competition problem model.'''
    __tablename__ = 'comp_problems'
    cid = None


class CompUser(database_base):
    '''The competition user model.'''
    __tablename__ = 'comp_users'
    cid = None
    team = None
    username = ''
