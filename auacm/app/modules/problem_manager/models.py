"""Provide model objects for representing problems."""
import time

from app.database import Base, session

# Problem = Base.classes.problems

class Problem(Base):
    """Problem class that we build by reflecting the mysql database."""

    __tablename__ = "problems"

    def __init__(self, **kwargs):
        # Initialize with some default values
        Base.__init__(
            self, appeared='',
            difficulty=0,
            added=time.time(),
            **kwargs
        )

    def commit_to_session(self):
        """Commit this problem to the database as a new problem."""
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        return self.pid


class  Problem_Data(Base):
    """Problem Data class that reflects mysql database"""

    __tablename__ = 'problem_data'

    def __init__(self, **kwargs):
        Base.__init__(self, time_limit=90, **kwargs)

    def commit_to_session(self):
        """Commit this problem data object to the database."""
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)


class Sample_Case(Base):
    """Test case class that reflects the mysql database."""

    __tablename__ = 'sample_cases'

    def __init__(self, **kwargs):
        Base.__init__(self, **kwargs)

    def commit_to_session(self):
        """Commit this sample case to the database."""
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
