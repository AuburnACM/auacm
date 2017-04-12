# pylint: disable=I0011,C0103
"""Creates the database."""
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

# Create global database variables
database_base = automap_base()
database_engine = create_engine('mysql+pymysql://acm@localhost/acm?charset=utf8')
database_connection = database_engine.connect()
database_session = Session(database_engine)

def commit_to_session(base):
    """Add an object to the session and refresh the session."""
    database_session.add(base)
    database_session.flush()
    database_session.commit()
    database_session.refresh(base)
