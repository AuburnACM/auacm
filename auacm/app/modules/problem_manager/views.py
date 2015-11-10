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
from json import loads


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


@app.route('/api/problems/create', methods=['POST'])
@login_required
def create_problem():
    # Admin check
    if not current_user.admin == 1:
        return serve_error('You must be an admin to create problems', 
            response_code=401)

    # Ensure all parts of the form are complete (note js will send 'undefined')
    if not 'title' in request.form:
        return serve_error('You must give a problem title', response_code=400)
    if not 'description' in request.form:
        return serve_error('You must give a problem description',
            response_code=400)
    if not 'input_description' in request.form:
        return serve_error('You must give an input description',
            response_code=400)
    if not 'output_description' in request.form:
        return serve_error('You must give an output description',
            response_code=400)
    if not 'cases' in request.form:
        return serve_error('You must provide at least one sample case',
            response_code=400)
    # TODO(brandonlmorris): Check and handle zip files

    # TODO(brandonlmorris): Insert new problem into database
    title = request.form['title'][:32]
    shortname = title.lower().replace(' ', '')

    problem = Problem(
        name=title,
        shortname=shortname
    )
    pid = problem.commit_to_session()

    # Convert the JSON array string to python array of dictionaries
    cases = request.form['cases']
    cases = loads(str(cases))

    return serve_response({
        'success': True,
        'title': request.form['title'],
        'description': request.form['description'],
        'input_description': request.form['input_description'],
        'output_description': request.form['output_description'],
        'sample_cases': cases,
        'pid': pid
    })

@app.route('/api/problems/delete', methods=['POST'])
@login_required
def delete_problem():
    # Admin check
    if not current_user.admin == 1:
        return serve_error('You must be an admin to delete a problem',
            response_code=401)

    if not request.form['pid']:
        return serve_error('You must specify a problem to delete',
            response_code=400)

    # TODO(brandonlmorris): Actually delete the thing
    problem = session.query(Problem).\
        filter(Problem.pid == request.form['pid'])
    if not problem.first():
        return serve_error('Could not find problem with pid ' + 
            request.form['pid'], response_code=401)
    problem.delete()
    session.flush()
    session.commit()

    return serve_response({
        'success': True,
        'deleted_pid': request.form['pid']
    })