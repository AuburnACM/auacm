from app.database import Base, session


class Competition(Base):
    __tablename__ = 'comp_names'
    
    
    def commit_to_session(self):
        """Commit this Competition to the database.
        
        This is useful for adding a newly-created Competition to the database.
        """
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        self._problem = None


class CompProblem(Base):
    __tablename__ = 'comp_problems'


class CompUser(Base):
    __tablename__ = 'comp_users'

