from flask import render_template, request
from flask.ext.login import login_user, logout_user, current_user, login_required
from app import app
# from app.modules.user_manager.models import load_user
from app.database import Base, session
from app.util import bcrypt, login_manager, serve_html, serve_response, serve_error
from app.modules.user_manager.models import User
# from app.modules.problem_manager.models import Problem


@app.route('/')
@app.route('/index')
@login_required
def index():
    return render_template('login.html')

@app.route('/problems')
@login_required
def getProblemsPage():
    return serve_html('problems.html')
    
@app.route('/login')
def getLoginPage():
    return serve_html('login.html')

# ideally, this would be broken out into a different module, but we can
# fix that later. For now, this works, and that's all that matters.
@app.route('/api/problems')
@login_required
def getProblems():
    problems = list()
    for problem in session.query(Base.classes.problems).all():
        problems.append({
            'pid': problem.pid,
            'name': problem.name,
            'appeared': problem.appeared,
            'difficulty': problem.difficulty,
            'compRelease': problem.comp_release,
            'added': problem.added,
            'timeLimit': problem.time_limit
        })
    return serve_response(problems)

@app.route('/api/login', methods=['POST'])
def login():
    username = request.form['username']
    password = request.form['password']
    user = load_user(username)
    if user:
        hashed = user.passw
        if bcrypt.check_password_hash(hashed, password):
            # everything's gucci
            login_user(user)
            return serve_response({})
    return serve_error('invalid username or password', 401)

@app.route('/api/logout')
@login_required
def logout():
    logout_user()
    return serve_response({})

@app.route('/api/me')
@login_required
def getMe():
    return serve_response({
        'username': current_user.username, 
        'displayName': current_user.display
    })
