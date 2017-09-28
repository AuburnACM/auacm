"""Provide model objects for representing problems."""
import time

from app.database import database_base, database_session

class Problem(database_base):
    """Problem class that we build by reflecting the mysql database.

    problems obey the following structure in the database:
    pid             Integer (primary key)
    name            String
    shortname       "
    appeared        "
    difficulty      "
    added           Integer (date)
    comp_release    Integer
    """

    __tablename__ = "problems"

    def __init__(self, **kwargs):
        # Initialize some default values
        defaults = {
            'appeared': '',
            'difficulty': 0,
            'added': time.time()
        }
        # Override defaults if provided
        defaults.update(kwargs)

        database_base.__init__(self, **defaults)

    def commit_to_session(self, session=database_session):
        """Commit this problem to the database as a new problem."""
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        return self.pid

    def to_dict(self):
        """Returns the problem as a dictionary"""
        return {
            'pid': self.pid,
            'name': self.name,
            'shortname': self.shortname,
            'appeared': self.appeared,
            'difficulty': self.difficulty,
            'added': self.added,
            'comp_release': self.comp_release
        }


class ProblemData(database_base):
    """Problem Data class that reflects mysql database

    Problem Data obeys the following structure:
    pid             Integer (foreign key, references Problem table)
    description     String (text)
    input_desc      "
    output_desc     "
    time_limit      Integer
    """

    __tablename__ = 'problem_data'

    def __init__(self, **kwargs):
        # Initialize default value(s)
        defaults = {'time_limit': 90}
        # Override defaults if provided
        defaults.update(kwargs)
        database_base.__init__(self, **defaults)

    def commit_to_session(self, session=database_session):
        '''Commit this problem data object to the database.'''
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)


class SampleCase(database_base):
    """Test case class that reflects the mysql database.

    Sample Cases obey the following structure:
    pid             Integer (foreign key, references Problem table)
    case_num        Integer
    input           String
    output          String
    """

    __tablename__ = 'sample_cases'

    def __init__(self, **kwargs):
        database_base.__init__(self, **kwargs)

    def commit_to_session(self, session=database_session):
        """Commit this sample case to the database."""
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
