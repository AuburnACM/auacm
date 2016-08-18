from app.database import Base, session


class Competition(Base):
    __tablename__ = 'comp_names'

    def to_dict(self, user_registered=False):
        return {
            'cid': self.cid,
            'name': self.name,
            'startTime': self.start,
            'length': self.stop - self.start,
            'closed': self.closed == 1,
            'registered': user_registered
        }

    def commit_to_session(self, session=session):
        """Commit this Competition to the database.

        This is useful for adding a newly-created Competition to the database.
        """
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        self._problem = None
        return self.cid


class CompProblem(Base):
    __tablename__ = 'comp_problems'


class CompUser(Base):
    __tablename__ = 'comp_users'

    def commit_to_session(self, session=session):
        """Commit this CompUser to the database.

        This is useful for adding a newly-created CompUser to the database.
        """
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        self._problem = None
