# pylint: disable=I0011,C0103,W0603
"""Creates the database."""
from sqlalchemy import create_engine
from sqlalchemy import event
from sqlalchemy import exc
from sqlalchemy import select
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.automap import automap_base
from app.config import DATABASE_USERNAME, DATABASE_PASSWORD

# Create global database variables
database_base = automap_base()
database_engine = create_engine(
    'mysql+pymysql://'+ DATABASE_USERNAME
    + '@localhost/acm?charset=utf8&password='
    + DATABASE_PASSWORD, pool_recycle=7200)

@event.listens_for(database_engine, 'engine_connect')
def ping_connection(connection, branch):
    """
    Tests the database connection before attempting to execute a
    statement.
    """
    if branch:
        return

    save_should_close_with_result = connection.should_close_with_result
    connection.should_close_with_result = False

    try:
        connection.scalar(select([1]))
    except exc.DBAPIError as err:
        if err.connection_invalidated:
            connection.scalar(select([1]))
        else:
            raise
    finally:
        connection.should_close_with_result = save_should_close_with_result

# Configure the session maker
session_maker = sessionmaker()
session_maker.configure(bind=database_engine)

_database_session = None

def commit_to_session(base):
    """Add an object to the session and refresh the session."""
    session = get_session()
    session.add(base)
    session.flush()
    session.commit()
    session.refresh(base)

def get_session():
    """Checks to see if a valid session exists and returns it. If
    a session does not exist, one is created."""
    global _database_session
    if _database_session is None:
        _database_session = session_maker()
    else:
        try:
            # Test the session first
            _database_session.execute("SELECT 1")
        except exc.DBAPIError:
            _database_session.close()
            _database_session = session_maker()
    return _database_session
