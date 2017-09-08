"""
This file serves the static index.html file.

Since routing is done client side by Angular 2, if a user refreshed the page,
the server would display a 404 error. I added the tags so it would return the
proper page.
"""
from flask import render_template, redirect
from app.modules import app

@app.route('/')
@app.route('/404')
@app.route('/blogs/<path:_path>')
@app.route('/blog/<path:_path>')
@app.route('/competitions')
@app.route('/competitions/<path:_path>')
@app.route('/competition/<path:_path>')
@app.route('/index')
@app.route('/judge')
@app.route('/problems')
@app.route('/problems/<path:_path>')
@app.route('/problem/<path:_path>')
@app.route('/profile')
@app.route('/profile/<path:_path>')
@app.route('/rankings')
@app.route('/settings/<path:_path>')
@app.route('/users/<path:_path>')
def get_home(_path=''):
    """Render the home page for the logged in user, if any"""
    return render_template('index.html')

@app.route('/api')
@app.route('/api/<path:_path>')
def api_404(_path=''):
    """Serve a 404 for the API"""
    return '404: Not Found', 404

@app.errorhandler(404)
def general_404(_error):
    """A catch all 404 for non-api requests"""
    return redirect("//127.0.0.1/404", code=302)
