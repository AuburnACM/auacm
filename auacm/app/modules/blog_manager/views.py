"""
This is the controller for the blog manager.
"""
from time import time

from flask import request
from flask.ext.login import current_user
from sqlalchemy import desc
from app.modules.user_manager.models import User
from app.database import database_session
from app.modules.blog_manager.models import BlogPost
from app.modules import app
from app.database import commit_to_session
from app.util import serve_error, serve_response, admin_required

def create_blog_object(post):
    """Creates a new blog post."""
    user = database_session.query(User).filter(
        User.username == post.username)
    user = user.first()
    return {
        'title' : post.title,
        'subtitle' : post.subtitle,
        'postTime' : post.post_time * 1000,
        'body' : post.body,
        'id' : post.id,
        'author' : {
            'username' : user.username,
            'display' : user.display
        }
    }

@app.route('/api/blog')
def get_blog_posts():
    """Returns all blog posts."""
    posts = database_session.query(BlogPost).order_by(
        desc(BlogPost.post_time))
    post_list = list()
    for post in posts.all():
        post_list.append(create_blog_object(post))
    return serve_response(post_list)


@app.route('/api/blog/<int:bid>')
def get_one_blog_post(bid):
    """Retrieve a single blog post by its blog id (bid)"""
    post = database_session.query(BlogPost).filter(
        BlogPost.id == bid).first()
    if not post:
        return serve_error('No blog post id: ' + str(bid), 404)

    return serve_response(create_blog_object(post))


@app.route('/api/blog/<int:bid>', methods=['PUT'])
def update_blog_post(bid):
    """Modify a blog post"""
    post = database_session.query(BlogPost).filter(
        BlogPost.id == bid).first()
    if not post:
        return serve_error('No blog post id: ' + str(bid), 404)

    post.title = request.form['title']
    post.subtitle = request.form['subtitle']
    post.body = request.form['body']
    post.username = current_user.username
    database_session.commit()

    return serve_response(create_blog_object(post))


@app.route('/api/blog', methods=["POST"])
@admin_required
def create_blog_post():
    """Creates a new blog post"""
    try:
        post = BlogPost(title=request.form['title'],
                        subtitle=request.form['subtitle'],
                        post_time=int(time()),
                        body=request.form['body'],
                        username=current_user.username)
    except KeyError:
        return serve_error('Must include title, subtitle, and body',
                           response_code=400)

    commit_to_session(post)
    return serve_response(create_blog_object(post))


@app.route('/api/blog/<int:bid>', methods=['DELETE'])
@admin_required
def delete_blog_post(bid):
    """Delete a blog post from the database"""
    post = database_session.query(BlogPost).filter_by(id=bid).first()
    if not post:
        return serve_error('No blog post id: {}'.format(bid), 404)

    post_id = post.id
    database_session.delete(post)
    database_session.commit()
    return serve_response({'deleted blog id': post_id})
