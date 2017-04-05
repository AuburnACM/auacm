"""
This file serves the static index.html file.

Since routing is done client side by Angular 2, if a user refreshed the page,
the server would display a 404 error. I added the tags so it would return the
proper page.
"""
from flask import render_template, redirect
from .modules import APP

@APP.route('/')
@APP.route('/404')
@APP.route('/blogs/<path:_path>')
@APP.route('/blog/<path:_path>')
@APP.route('/competitions/<path:_path>')
@APP.route('/competition/<path:_path>')
@APP.route('/index')
@APP.route('/judge')
@APP.route('/problems/<path:_path>')
@APP.route('/problem/<path:_path>')
@APP.route('/profile/<path:_path>')
@APP.route('/rankings')
@APP.route('/settings/<path:_path>')
@APP.route('/users/<path:_path>')
def get_home(_path=''):
    """Render the home page for the logged in user, if any"""
    return render_template('index.html')

@APP.route('/api')
@APP.route('/api/<path:_path>')
def api_404(_path=''):
    """Serve a 404 for the API"""
    return '404: Not Found', 404

@APP.errorhandler(404)
def general_404(_error):
    """A catch all 404 for non-api requests"""
    return redirect("//127.0.0.1/404", code=302)
