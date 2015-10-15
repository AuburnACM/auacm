'''Reflection and utilities for the users database table.'''

from app.database import Base

test = 'this is a test'

class User(Base):
    '''Model object for entries in the users database table.'''
    
    __tablename__ = 'users'

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self.username
