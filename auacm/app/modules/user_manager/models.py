'''Reflection and utilities for the users database table.'''

from app.database import Base, session
from app.util import login_manager

class User(Base.classes.users):
    '''Model object for entries in the users database table.'''

    def __init__(self, user):
        self.user = user
    
    def is_authenticated(self):
        return True
        
    def is_active(self):
        return is_authenticated()
        
    def is_anonymous(self):
        return False
    
    def get_id(self):
        return self.user.username


@login_manager.user_loader
def load_user(user_id):
    '''Log a user into the app.'''
    result = session.query(Base.classes.users).filter(Base.classes.users.username==user_id).first()
    if result:
        return User(result)
    else:
        return None
