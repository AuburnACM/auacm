from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.util import serve_response, serve_error, admin_required
from .models import BlogPost
from app.modules.user_manager.models import User
from sqlalchemy import desc
from time import time
import app.database as database

def create_blog_object(post):
    user = database.session.query(User).filter(User.username==post.username).first()
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
    posts = database.session.query(BlogPost).order_by(desc(BlogPost.post_time)).all()
    postList = list()
    for p in posts:
        postList.append(create_blog_object(p))
    return serve_response(postList)


@app.route('/api/blog/<int:bid>')
def get_one_blog_post(bid):
    """Retrieve a single blog post by its blog id (bid)"""
    post = database.session.query(BlogPost).filter(BlogPost.id == bid).first()
    if not post:
        return serve_error('No blog post id: ' + str(bid), 404)

    return serve_response(create_blog_object(post))


@app.route('/api/blog/<int:bid>', methods=['PUT'])
def update_blog_post(bid):
    """Modify a blog post"""
    post = database.session.query(BlogPost).filter(BlogPost.id == bid).first()
    if not post:
        return serve_error('No blog post id: ' + str(bid), 404)

    post.title = request.form['title']
    post.subtitle = request.form['subtitle']
    post.body = request.form['body']
    post.username = current_user.username
    post.commit_to_session()

    return serve_response(create_blog_object(post))


@app.route('/api/blog', methods=["POST"])
@admin_required
def create_blog_post():
    if not request.form['title'] or not request.form['subtitle'] or not request.form['body']:
        return serve_error('Must include title, subtitle, and body with request',
                           response_code=400)
    post = BlogPost(title=request.form['title'],
                    subtitle=request.form['subtitle'],
                    post_time=int(time()),
                    body=request.form['body'],
                    username=current_user.username)
    post.commit_to_session(database.session)
    return serve_response(create_blog_object(post))
