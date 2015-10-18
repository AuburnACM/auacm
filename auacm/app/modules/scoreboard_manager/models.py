from app.database import Base


class Competition(Base):
    __tablename__ = 'comp_names'

class CompProblem(Base):
    __tablename__ = 'comp_problems'
    
class CompUser(Base):
    __tablename__ = 'comp_users'

