from time import time
from app.database import Base, session

class BlogPost(Base):
    """Reflects a blog post in the database"""

    __tablename__ = 'blog_posts'

    def __init__(self, **kwargs):
        """Initialize a new blog post"""
        defaults = {
            'title': kwargs.get('title'),
            'subtitle': kwargs.get('subtitle'),
            'body': kwargs.get('body'),
            'post_time': kwargs.get('post_time', time()),
            'username': kwargs.get('username')
        }
        Base.__init__(self, **defaults)

    def commit_to_session(self, session=session):
        """Commit this blog post to the database"""
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        return self.id
