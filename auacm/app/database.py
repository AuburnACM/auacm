'''Creates the database.'''
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

# Create global database variables
DATABASE_BASE = automap_base()
DATABASE_ENGINE = create_engine('mysql+pymysql://acm@localhost/acm?charset=utf8')
DATABASE_CONNECTION = DATABASE_ENGINE.connect()
DATABASE_SESSION = Session(DATABASE_ENGINE)

def commit_to_session(base):
    '''Add an object to the session and refresh the session.'''
    DATABASE_SESSION.add(base)
    DATABASE_SESSION.flush()
    DATABASE_SESSION.commit()
    DATABASE_SESSION.refresh(base)
