from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import Base, session
from app.util import serve_response, serve_error, serve_info_pdf, login_manager
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission
from app.modules.problem_manager.models import Problem, Problem_Data, Sample_Case
from os.path import join
from sqlalchemy.orm import load_only
from json import loads


def url_for_problem(problem):
    # app.logger.info(join('problems', str()))
    return join('problems', str(problem.shortname), 'info.pdf')


# @app.route('/problems/<shortname>')
@app.route('/problems/<shortname>/info.pdf')
@login_required
def get_problem_info(shortname):
    pid = session.query(Problem).\
            options(load_only('pid', 'shortname')).\
            filter(Problem.shortname==shortname).\
            first().pid
    return serve_info_pdf(str(pid))

# Get a JSON representation of a problem
@app.route('/api/problems/<shortname>', methods=['GET'])
@login_required
def get_problem(shortname):
    problem = session.query(Problem, Problem_Data).\
            join(Problem_Data).\
            filter(Problem.shortname == shortname).\
            first()

    cases = list()
    for case in session.query(Sample_Case).\
                    filter(Sample_Case.pid == problem.Problem.pid).all():
        cases.append({
            'case_num': case.case_num,
            'input': case.input,
            'output': case.output
        })
    return serve_response({
        'pid': problem.Problem.pid,
        'name': problem.Problem.name,
        'shortname': problem.Problem.shortname,
        'appeared': problem.Problem.appeared,
        'difficulty': problem.Problem.difficulty,
        'added': problem.Problem.added,
        'comp_release': problem.Problem.comp_release,
        'description': problem.Problem_Data.description,
        'input_desc': problem.Problem_Data.input_desc,
        'output_desc': problem.Problem_Data.output_desc,
        'sample_cases': cases
    })

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
    
    for problem in session.query(Problem, Problem_Data).\
            join(Problem_Data).all():
        problems.append({
            'pid': problem.Problem.pid,
            'name': problem.Problem.name,
            'shortname': problem.Problem.shortname,
            'appeared': problem.Problem.appeared,
            'difficulty': problem.Problem.difficulty,
            'compRelease': problem.Problem.comp_release,
            'added': problem.Problem.added,
            'solved': problem.Problem.pid in solved_set,
            'description': problem.Problem_Data.description,
            'input_description': problem.Problem_Data.input_desc,
            'output_description': problem.Problem_Data.output_desc,
            'url': url_for_problem(problem.Problem)
        })
    return serve_response(problems)


@app.route('/api/problems/create', methods=['POST'])
@login_required
def create_problem():
    # Admin check
    if not current_user.admin == 1:
        return serve_error('You must be an admin to create problems', 
            response_code=401)

    # Ensure all required arguments are defined
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

    # Convert the JSON array string to python array of dictionaries
    cases = request.form['cases']
    cases = loads(str(cases))
    for case in cases:
        if not 'input' in case or not 'output' in case:
            return serve_error('Sample case(s) were not formed correctly',
                response_code=400)

    # TODO(brandonlmorris): Check and handle zip files

    # Create the problem and add it to the database
    title = request.form['title'][:32]
    shortname = title.lower().replace(' ', '')
    problem = Problem(
        name=title,
        shortname=shortname
    )
    if 'difficulty' in request.form:
        problem.diffiulty = request.form['diffiulty']
    if 'appeared_in' in request.form:
        problem.appeared = request.form['appeared_in']
    pid = problem.commit_to_session()

    # Create the problem data and add it to the database
    problem_data = Problem_Data(
        pid=pid,
        description=request.form['description'],
        input_desc=request.form['input_description'],
        output_desc=request.form['output_description']
    )
    if 'time_limit' in request.form:
        problem_data.time_limit = request.form['time_limit']
    problem_data.commit_to_session()

    # Add each sample case to the db
    case_num = 1
    for case in cases:
        sample = Sample_Case(
            pid=pid,
            case_num=case_num,
            input=case['input'],
            output=case['output']
        )
        sample.commit_to_session()
        case_num += 1

    return serve_response({
        'success': True,
        'name': problem.name,
        'shortname': problem.shortname,
        'description': problem_data.description,
        'input_description': problem_data.input_desc,
        'output_description': problem_data.output_desc,
        'sample_cases': cases,
        'pid': problem.pid,
        'difficulty': problem.difficulty
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

    # Delete from problem_data table first to satisfy foreign key constraint
    problem_data = session.query(Problem_Data).\
        filter(Problem_Data.pid == request.form['pid'])
    if not problem_data.first():
        return serve_error('Could not find problem data with pid ' + 
            request.form['pid'], response_code=401)
    problem_data.delete()

    # TODO(brandonlmorris):Delete any and all sample cases associated w/ problem

    # Delete from problem table
    problem = session.query(Problem).\
        filter(Problem.pid == request.form['pid'])
    if not problem.first():
        return serve_error('Could not find problem with pid ' + 
            request.form['pid'], response_code=401)
    problem.delete()

    # Commit changes
    session.flush()
    session.commit()

    return serve_response({
        'success': True,
        'deleted_pid': request.form['pid']
    })