from app.database import Base


class Competition(Base):
    __tablename__ = 'comp_names'

    def to_dict(self):
        return {
            'cid': self.cid,
            'name': self.name,
            'startTime': self.start,
            'length': self.stop - self.start
        }

class CompProblem(Base):
    __tablename__ = 'comp_problems'

class CompUser(Base):
    __tablename__ = 'comp_users'
