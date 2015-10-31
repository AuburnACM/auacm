from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import Base, session
from app.util import serve_response, serve_error, serve_info_pdf
from app.modules.user_manager.models import User
from os.path import join
# from sqlalchemy import desc

def url_for_problem(problem):
    return join('problems', str(problem.pid))

@app.route('/problems/<pid>')
@app.route('/problems/<pid>/info.pdf')
@login_required
def get_problem_info(pid):
    return serve_info_pdf(pid)

@app.route('/api/problems')
@login_required
def get_problems():
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

@app.route('/api/problems', methods=['POST'])
@login_required
def create_problem() :
    if not current_user.admin == 1:
        return serve_error('You must be an admin to create problems', 
            response_code=401)
    app.logger.info('This happened');
    app.logger.info(request.form['title'])
    return serve_response({'success': True})
