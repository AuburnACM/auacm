from flask import render_template, request
from flask.ext.login import login_user, logout_user, current_user, login_required
from app import app, socketio
from app.database import Base, session
from app.util import bcrypt, login_manager, serve_info_pdf, serve_html, serve_response, serve_error, load_user
from app.modules.user_manager.models import User
from os.path import join

@socketio.on('connect')
def onConnect():
    print "sever connected; it's a miracle"

@app.route('/')
@app.route('/index')
@login_required
def getProblemsPage():
    print "serving index.html"
    return render_template('index.html', username=current_user.display)

    
@app.route('/login')
@login_manager.unauthorized_handler
def getLoginPage():
    return serve_html('login.html')

    
@app.route('/problems/<pid>')
@login_required
def getProblemInfo(pid):
    return serve_info_pdf(pid)


# ideally, this would be broken out into a different module, but we can
# fix that later. For now, this works, and that's all that matters.
@app.route('/api/problems')
@login_required
def getProblems():
    problems = list()
    Submits = Base.classes.submits
    solved = session.query(Submits).\
            filter(Submits.username==current_user.username).\
            filter(Submits.result=="good").\
            all()
    solved_set = set()
    for solve in solved:
        solved_set.add(solve.pid)
    
    for problem in session.query(Base.classes.problems).all():
        problems.append({
            'pid': problem.pid,
            'name': problem.name,
            'appeared': problem.appeared,
            'difficulty': problem.difficulty,
            'compRelease': problem.comp_release,
            'added': problem.added,
            'timeLimit': problem.time_limit,
            'solved': problem.pid in solved_set,
            'url': url_for_problem(problem)
        })
    return serve_response(problems)
    

def url_for_problem(problem):
    return join('problems', str(problem.pid))


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
