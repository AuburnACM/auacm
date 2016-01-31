'''Reflection and utilities for the users database table.'''

from app.database import Base

test = 'this is a test'

class User(Base):
    '''Model object for entries in the users database table.'''

    __tablename__ = 'users'
    is_authenticated = True
    is_active = True
    is_anonymous = False

    def get_id(self):
        return self.username
