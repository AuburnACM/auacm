from app.database import Base, session

class BlogPost(Base):
    """Reflects a blog post in the database"""

    __tablename__ = 'blog_posts'

    def __init__(self, **kwargs):
        """Initialize a new blog post"""
        Base.__init__(self, **defaults)

    def commit_to_session(self, session=session):
        """Commit this blog post to the database"""
        session.add(self)
        session.flush()
        session.commit()
        session.refresh(self)
        return self.id
