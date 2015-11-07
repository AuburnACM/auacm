from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import Base, session
from app.util import serve_response, serve_error, serve_info_pdf, login_manager
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission
from app.modules.problem_manager.models import Problem
from os.path import join
from sqlalchemy.orm import load_only


def url_for_problem(problem):
    return join('problems', str(problem.shortname))

@app.route('/problems/<shortname>')
@app.route('/problems/<shortname>/info.pdf')
@login_required
def get_problem_info(shortname):
    pid = session.query(Problem).\
            options(load_only("pid", "shortname")).\
            filter(Problem.shortname==shortname).\
            first().pid
    return serve_info_pdf(str(pid))

@app.route('/api/problems')
@login_required
def get_problems():
    problems = list()
    solved = session.query(Submission).\
            filter(Submission.username==current_user.username).\
            filter(Submission.result=="good").\
            all()
    solved_set = set()
    for solve in solved:
        solved_set.add(solve.pid)
    
    for problem in session.query(Problem).all():
        problems.append({
            'pid': problem.pid,
            'name': problem.name,
            'shortname': problem.shortname,
            'appeared': problem.appeared,
            'difficulty': problem.difficulty,
            'compRelease': problem.comp_release,
            'added': problem.added,
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
