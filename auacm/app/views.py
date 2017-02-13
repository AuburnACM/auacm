from flask import render_template, send_from_directory
from flask.ext.login import current_user
from app import app

@app.route('/')
@app.route('/index')
def get_home():
    """Render the home page for the logged in user, if any"""
    logged_in = not current_user.is_anonymous
    display_name = (current_user.display if logged_in else 'Log in')
    logged_in_string = 'true' if logged_in else 'false'
    return render_template('index_angular.html')

@app.route('/api')
@app.route('/api/<path:path>')
def api_404(path=''):
    """Serve a 404 for the API"""
    return '404: Not Found', 404

@app.errorhandler(404)
def general_404(error):
    """A catch all 404 for non-api requests"""
    return render_template("404.html")
