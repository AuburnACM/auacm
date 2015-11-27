"""Manages problems within the app, including their creation, deletion,
updating, and retreival.

Functions:
url_for_problem -- returns the route for the pdf description of a problem
                   given an argument that has a 'pid' field.
get_problem     -- returns a JSON representation of an individual problem,
                   with complete information about its description, input,
                   output, etc.
get_problems    -- returns basic information about all problems in the database.
create_problem  -- adds a new problem to the database and the data folder.
delete_problem  -- removes a problem from the database and data folder
update_problem  -- modifies the data/files of a specific problem
"""
import os
import zipfile

from flask import request
from flask.ext.login import current_user, login_required # pylint: disable=no-name-in-module
from app import app
from app.database import session
from app.util import serve_response, serve_error, serve_info_pdf
from app.modules.submission_manager.models import Submission
from app.modules.problem_manager.models import Problem, ProblemData, SampleCase
from sqlalchemy.orm import load_only
from json import loads
from shutil import rmtree


def url_for_problem(problem):
    """Return the path of the pdf description of a problem"""
    return os.path.join('problems', str(problem.shortname),
                        'info.pdf')


def is_pid(identifier):
    try:
        int(identifier)
        return True
    except ValueError:
        return False


@app.route('/problems/<shortname>/info.pdf', methods=['GET'])
@login_required
def get_problem_info(shortname):
    """Serve the PDF description of a problem"""
    pid = session.query(Problem).\
            options(load_only('pid', 'shortname')).\
            filter(Problem.shortname == shortname).\
            first().pid
    return serve_info_pdf(str(pid))


@app.route('/api/problems/<identifier>', methods=['GET'])
@login_required
def get_problem(identifier):
    """Returns the JSON representation of a specific problem"""
    problem = session.query(Problem, ProblemData).join(ProblemData)
    if is_pid(identifier):
        problem = problem.filter(Problem.pid == identifier).first()
    else:
        problem = problem.\
                  filter(Problem.shortname == identifier).first()

    cases = list()
    for case in session.query(SampleCase).\
                    filter(SampleCase.pid == problem.Problem.pid).\
                    all():
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
        'description': problem.ProblemData.description,
        'input_desc': problem.ProblemData.input_desc,
        'output_desc': problem.ProblemData.output_desc,
        'sample_cases': cases
    })


@app.route('/api/problems')
@login_required
def get_problems():
    """Obtain basic information of all the problems in the database"""
    problems = list()
    solved = session.query(Submission).\
            filter(Submission.username == current_user.username).\
            filter(Submission.result == "good").\
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


@app.route('/api/problems/', methods=['POST'])
@login_required
def create_problem():
    """Add a new problem to the database and data folder"""
    # Admin check
    if not current_user.admin == 1:
        return serve_error('You must be an admin to create problems',
                           response_code=401)

    try:
        # Convert the JSON to python array of dictionaries
        cases = request.form['cases']
        cases = loads(str(cases))
        for case in cases:
            if not 'input' in case or not 'output' in case:
                return serve_error(
                    'Sample case(s) were not formed correctly',
                    response_code=400)

        # Create the problem
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

        # Create the problem data and add it to the database
        problem_data = ProblemData(
            description=request.form['description'],
            input_desc=request.form['input_description'],
            output_desc=request.form['output_description']
        )
        if 'time_limit' in request.form:
            problem_data.time_limit = request.form['time_limit']

        # Create list of sample cases
        case_num = 1
        sample_cases = list()
        for case in cases:
            sample = SampleCase(
                case_num=case_num,
                input=case['input'],
                output=case['output']
            )
            case_num += 1
            sample_cases.append(sample)

        in_file = zipfile.ZipFile(request.files['in_file'])
        out_file = zipfile.ZipFile(request.files['out_file'])
        sol_file = request.files['sol_file']

    # If any required values were missing, serve an error
    except KeyError as err:
        return serve_error('Form field not found: ' + err[0],
                           response_code=400)

    # Commit everything to the database
    pid = problem.commit_to_session()
    problem_data.pid = pid
    problem_data.commit_to_session()
    for case in sample_cases:
        case.pid = pid
        case.commit_to_session()

    # Store the judge data
    directory = os.path.join(app.config['DATA_FOLDER'],
                             'problems', str(problem.pid))
    in_file.extractall(directory)
    out_file.extractall(directory)
    os.mkdir(os.path.join(directory, 'test'))
    sol_file.save(os.path.join(directory, 'test', sol_file.filename))

    return serve_response({
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
@app.route('/api/problems/<identifier>', methods=['DELETE'])
@login_required
def delete_problem(identifier):
    """Delete a specified problem in the database and data folder"""
    # Admin check
    if not current_user.admin == 1:
        return serve_error('You must be an admin to delete a problem',
                           response_code=401)

    pid, problem = None, session.query(Problem)
    if is_pid(identifier):
        pid = identifier
        problem = problem.filter(Problem.pid == pid).first()
    else:
        problem = problem.filter(Problem.shortname == identifier).first()
        pid = problem.pid

    # Delete from problem_data table first to satisfy foreign key constraint
    problem_data = session.query(ProblemData).\
        filter(ProblemData.pid == pid)
    if not problem_data.first():
        return serve_error('Could not find problem data with pid ' +
                           pid, response_code=401)
    problem_data.delete()

    # Delete any and all sample cases associated w/ problem
    for case in session.query(SampleCase).\
            filter(SampleCase.pid == pid).all():
        session.delete(case)

    # Delete from problem table
    problem.delete()

    # Commit changes
    session.flush()
    session.commit()

    # Delete judge data
    directory = os.path.join(app.config['DATA_FOLDER'], 'problems', pid)
    rmtree(directory)

    return serve_response({
        'deleted_pid': pid
    })

# Update a problem in the database
@app.route('/api/problems/<identifier>', methods=['PUT'])
@login_required
def update_problem(identifier):    # pylint: disable=too-many-branches
    """Modify a problem in the database and data folder"""
    # Admin check
    if not current_user.admin == 1:
        return serve_error('You must be an admin to update a problem',
                           response_code=401)

    pid, problem = None, session.query(Problem)
    if is_pid(identifier):
        pid = identifier
        problem = problem.filter(Problem.pid == pid).first()
    else:
        problem = problem.filter(Problem.shortname == identifier).first()
        pid = problem.pid

    data = session.query(ProblemData).filter(ProblemData.pid == pid).first()
    if 'name' in request.form:
        problem.name = request.form['name'][:32]
        problem.shortname = request.form['name'][:32].replace(' ', '').lower()
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
        for old_case in session.query(SampleCase).\
                filter(SampleCase.pid == pid).all():
            session.delete(old_case)
            session.flush()
            session.commit()
        case_num = 1
        cases = loads(str(request.form['cases']))
        case_lst = list()
        for case in cases:
            SampleCase(
                pid=pid,
                case_num=case_num,
                input=case['input'],
                output=case['output']
            ).commit_to_session()
            case_lst.append({
                'case_num': case_num,
                'input': case['input'],
                'output': case['output']
            })
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
        request.files['sol_file'].save(
            os.path.join(directory, 'test', request.files['sol_file'].filename))


    return serve_response({
        'pid': problem.pid,
        'name': problem.name,
        'shotrname': problem.shortname,
        'description': data.description,
        'input_desc': data.input_desc,
        'output_desc': data.output_desc,
        'difficulty' : problem.difficulty,
        'cases': case_lst
    })
