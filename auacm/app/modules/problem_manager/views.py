import os
import zipfile

from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import Base, session
from app.util import serve_response, serve_error, serve_info_pdf, login_manager
from app.modules.user_manager.models import User
from app.modules.submission_manager.models import Submission
from app.modules.problem_manager.models import Problem, Problem_Data, Sample_Case
from sqlalchemy.orm import load_only
from json import loads
from shutil import copy, rmtree


def url_for_problem(problem):
    return os.path.join('problems', str(problem.shortname), 'info.pdf')


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
@app.route('/api/problems/<id>', methods=['GET'])
@login_required
def get_problem(id):
    problem = session.query(Problem, Problem_Data).join(Problem_Data)
    try:
        id = int(id)     # see if `id` is the pid
        problem = problem.filter(Problem.pid == id).first()
    except ValueError:
        problem = problem.filter(Problem.shortname == id).first()

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


# GET basic information about all the problems in the database
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


# Create a new problem
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
    if not 'in_file' in request.files:
        return serve_error('You must provide a input file',
            reponse_code=400)
    if not 'out_file' in request.files:
        return serve_error('You must provide an output file',
            response_code=400)
    if not 'sol_file' in request.files:
        return serve_error('You must provide a solution file',
            response_code=400)

    # Convert the JSON array string to python array of dictionaries
    cases = request.form['cases']
    cases = loads(str(cases))
    for case in cases:
        if not 'input' in case or not 'output' in case:
            return serve_error('Sample case(s) were not formed correctly',
                response_code=400)

    # Create the problem and add it to the database
    title = request.form['title'][:32]
    shortname = title.lower().replace(' ', '')
    problem = Problem(
        name=title,
        shortname=shortname
    )
    if 'difficulty' in request.form:
        problem.difficulty = request.form['difficulty']
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

    # Store the judge data
    directory = os.path.join(app.config['DATA_FOLDER'], 'problems', str(problem.pid))
    zipfile.ZipFile(request.files['in_file']).extractall(directory)
    zipfile.ZipFile(request.files['out_file']).extractall(directory)
    os.mkdir(os.path.join(directory, 'test'))
    request.files['sol_file'].save(os.path.join(directory, 'test', request.files['sol_file'].filename))

    return serve_response({
        'success': True,
        'name': problem.name,
        'shortname': problem.shortname,
        'description': problem_data.description,
        'input_desc': problem_data.input_desc,
        'output_desc': problem_data.output_desc,
        'sample_cases': cases,
        'pid': problem.pid,
        'difficulty': problem.difficulty
    })

# Delete a problem from the database
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

    # Delete any and all sample cases associated w/ problem
    for case in session.query(Sample_Case).\
            filter(Sample_Case.pid == request.form['pid']).all():
        case.delete()

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

# Update a problem in the database
@app.route('/api/problems/edit', methods=['POST'])
@login_required
def update_problem():
    if not current_user.admin == 1:
        return serve_error('You must be an admin to update a problem',
            resonse_code=401)

    if not request.form['pid']:
        return serve_error('You must specify a problem to update',
            response_code=400)

    pid = request.form['pid']

    problem = session.query(Problem).filter(Problem.pid==pid).first()
    data = session.query(Problem_Data).filter(Problem_Data.pid==pid).first()
    if 'name' in request.form:
        problem.name = request.form['name'][:32]
        problem.shortname = request.form['name'][:32].replace(' ','').lower()
    if 'description' in request.form:
        data.description = request.form['description']
    if 'input_desc' in request.form:
        data.input_desc = request.form['input_desc']
    if 'output_desc' in request.form:
        data.output_desc = request.form['output_desc']
    if 'appeared_in' in request.form:
        problem.appeared = request.form['appeared_in']
    if 'difficulty' in request.form:
        data.difficulty = request.form['difficulty']

    # Save the changes
    problem.commit_to_session()
    data.commit_to_session()

    # If sample cases were uploaded, delete cases and go with the new ones
    if 'cases' in request.form:
        for old_case in session.query(Sample_Case).\
                filter(Sample_Case.pid==pid).all():
            old_case.delete()
        case_num = 1
        cases = loads(str(request.form['cases']))
        for case in cases:
            Sample_Case(
                pid=pid,
                case_num=case_num,
                input=case['input'],
                output=case['output']
            ).commit_to_session()
            case_num += 1

    directory = os.path.join(app.config['DATA_FOLDER'], 'problems', pid)
    if not os.path.exists(directory):
        os.mkdir(directory)

    # Add judge data if supplied
    if 'in_file' in request.files:
        in_file = zipfile.ZipFile(request.files['in_file'])
        in_file.extractall(directory)

    if 'out_file' in request.files:
        out_file = zipfile.ZipFile(request.files['out_file'])
        out_file.extractall(directory)

    if 'sol_file' in request.files:
        if os.path.exists(directory + '/test'):
            rmtree(directory + '/test')
        os.mkdir(os.path.join(directory, 'test'))
        request.files['sol_file'].save(os.path.join(directory, 'test', request.files['sol_file'].filename))


    return serve_response({
        'success': True,
        'pid': problem.pid,
        'name': problem.name,
        'shotrname': problem.shortname,
        'description': data.description,
        'input_desc': data.input_desc,
        'output_desc': data.output_desc,
        'difficulty' : problem.difficulty
    })
