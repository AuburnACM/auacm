from app import Base

class User(Base.classes.users):
    
    def __init__(self, user):
        self.user = user
    
    def is_authenticated(self):
        return True
        
    def is_active(self):
        return is_authenticated()
        
    def is_anonymous(self):
        return False
    
    def get_id(self):
        return self.user.username
