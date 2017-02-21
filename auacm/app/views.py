from flask import render_template, send_from_directory
from flask.ext.login import current_user
from app import app

"""Since routing is done client side by Angular 2, if a user refreshed the page,
the server would display a 404 error. I added the tags so it would return the
proper page."""

@app.route('/')
@app.route('/index')
@app.route('/blogs')
@app.route('/blogs/create')
@app.route('/problems')
@app.route('/problems/create')
@app.route('/judge')
@app.route('/rankings')
@app.route('/competitions')
@app.route('/competitions/create')
@app.route('/users/create')
@app.route('/404')
@app.route('/settings')
def get_home():
    """Render the home page for the logged in user, if any"""
    logged_in = not current_user.is_anonymous
    display_name = (current_user.display if logged_in else 'Log in')
    logged_in_string = 'true' if logged_in else 'false'
    return render_template('index.html')

@app.route('/blog/<int:id>')
@app.route('/blog/<int:id>/edit')
def get_blog_page(id):
    """Render the home page for the logged in user, if any"""
    logged_in = not current_user.is_anonymous
    display_name = (current_user.display if logged_in else 'Log in')
    logged_in_string = 'true' if logged_in else 'false'
    return render_template('index.html')

@app.route('/problem/<string:id>')
@app.route('/problem/<string:id>/edit')
def get_problem_page(id):
    """Render the home page for the logged in user, if any"""
    logged_in = not current_user.is_anonymous
    display_name = (current_user.display if logged_in else 'Log in')
    logged_in_string = 'true' if logged_in else 'false'
    return render_template('index.html')

@app.route('/competition/<string:id>')
@app.route('/competition/<string:id>/edit')
@app.route('/competition/<string:id>/teams')
def get_scorboard_page(id):
    """Render the home page for the logged in user, if any"""
    logged_in = not current_user.is_anonymous
    display_name = (current_user.display if logged_in else 'Log in')
    logged_in_string = 'true' if logged_in else 'false'
    return render_template('index.html')

@app.route('/api')
@app.route('/api/<path:path>')
def api_404(path=''):
    """Serve a 404 for the API"""
    return '404: Not Found', 404

@app.errorhandler(404)
def general_404(error):
    """A catch all 404 for non-api requests"""
    return render_template("404.html")
