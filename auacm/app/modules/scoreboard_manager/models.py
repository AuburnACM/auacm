from app.database import Base

print dir(Base.classes)


class Competition(Base):
    __tablename__ = 'comp_names'

class CompProblem(Base):
    __tablename__ = 'comp_problems'
    
class CompUser(Base):
    __tablename__ = 'comp_users'
    

# CompUser = Base.classes.comp_users
# Competition = Base.classes.comp_names
# CompProblem = Base.classes.comp_problems
