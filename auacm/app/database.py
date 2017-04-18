# pylint: disable=I0011,C0103
"""Creates the database."""
from sqlalchemy import create_engine
from sqlalchemy import event
from sqlalchemy import exc
from sqlalchemy import select
from sqlalchemy.orm import Session
from sqlalchemy.ext.automap import automap_base

# Create global database variables
database_base = automap_base()
database_engine = create_engine(
    'mysql+pymysql://acm@localhost/acm?charset=utf8', pool_recycle=7200)

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

database_session = Session(database_engine)

def commit_to_session(base):
    """Add an object to the session and refresh the session."""
    database_session.add(base)
    database_session.flush()
    database_session.commit()
    database_session.refresh(base)
