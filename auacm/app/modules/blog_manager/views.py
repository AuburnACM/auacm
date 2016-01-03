from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import session
from app.util import serve_response, serve_error
from .models import BlogPost
from app.modules.user_manager.models import User
from sqlalchemy import desc
from time import time

def create_blog_object(post):
    user = session.query(User).filter(User.username==post.username).first()
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
    posts = session.query(BlogPost).order_by(desc(BlogPost.post_time)).all()
    postList = list()
    for p in posts:
        postList.append(create_blog_object(p))
    return serve_response(postList)

@app.route('/api/blog/', methods=["POST"])
@login_required
def create_blog_post():
    if not current_user.admin == 1:
        return serve_error('You must be an admin to submit blog posts', response_code=401)
    if not request.form['title'] or not request.form['subtitle'] or not request.form['body']:
        return serve_error('Must include title, subtitle, and body with request',
                           response_code=400)
    post = BlogPost(title=request.form['title'],
                    subtitle=request.form['subtitle'],
                    post_time=int(time()),
                    body=request.form['body'],
                    username=current_user.username)
    session.add(post)
    session.flush()
    session.commit()
    session.refresh(post)
    return serve_response(create_blog_object(post))
