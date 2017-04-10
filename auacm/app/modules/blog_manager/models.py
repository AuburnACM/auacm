'''
Contains the model of a blog post.
'''
from time import time
from app.database import database_base

class BlogPost(database_base):
    '''Reflects a blog post in the database'''

    __tablename__ = 'blog_posts'

    def __init__(self, **kwargs):
        '''Initialize a new blog post'''
        defaults = {
            'title': kwargs.get('title'),
            'subtitle': kwargs.get('subtitle'),
            'body': kwargs.get('body'),
            'post_time': kwargs.get('post_time', time()),
            'username': kwargs.get('username')
        }
        database_base.__init__(self, **defaults)
