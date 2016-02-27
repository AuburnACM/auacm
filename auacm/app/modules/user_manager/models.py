'''Reflection and utilities for the users database table.'''

from app.database import Base, session

class User(Base):
    '''Model object for entries in the users database table.'''

    __tablename__ = 'users'
    is_authenticated = True
    is_active = True
    is_anonymous = False

    def get_id(self):
        return self.username

    def commit_to_session(self):
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
