"""
Manages problems within the app, including their creation, deletion,
updating, and retreival.
"""
from json import loads
import os
from shutil import rmtree
from time import time
import zipfile

from flask import request
from flask.ext.login import current_user
from sqlalchemy.orm import load_only
from app.database import database_session
from app.modules import app
from app.modules.competition_manager.models import Competition
from app.modules.problem_manager.models import Problem, ProblemData, SampleCase
from app.modules.submission_manager.models import Submission
from app.util import serve_response, serve_error, serve_info_pdf, admin_required


@app.route('/problems/<shortname>/info.pdf', methods=['GET'])
def get_problem_info(shortname):
    """Serve the PDF description of a problem"""
    pid = (database_session.query(Problem)
           .options(load_only('pid', 'shortname'))
           .filter(Problem.shortname == shortname)
           .first().pid)
    return serve_info_pdf(str(pid))


@app.route('/api/problems/<identifier>', methods=['GET'])
def get_problem(identifier):
    """
    Returns the JSON representation of a specific problem

    If the problem is meant to be released with a competition that has not
    started yet, a 404 error is returned.
    """
    problem = database_session.query(Problem, ProblemData).join(ProblemData)

    if is_pid(identifier):
        problem = problem.filter(Problem.pid == identifier).first()
    else:
        problem = problem.filter(Problem.shortname == identifier).first()

    # Hide unreleased problems to non-admins
    if problem is None or ((current_user.is_anonymous or
                            current_user.admin != 1) and
                           comp_not_released(problem.Problem.comp_release)):
        return serve_error('404: Problem Not Found', 404)

    cases = list()
    for case in (database_session.query(SampleCase)
                 .filter(SampleCase.pid == problem.Problem.pid)
                 .all()):
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
def get_problems():
    """Obtain basic information of all the problems in the database"""
    problems = list()
    solved_set = set()
    competitions = dict()
    is_admin = not current_user.is_anonymous and current_user.admin == 1
    for comp in database_session.query(Competition).all():
        competitions[comp.cid] = comp.start

    if not current_user.is_anonymous:
        solved = (database_session.query(Submission)
                  .filter(Submission.username == current_user.username)
                  .filter(Submission.result == 'good')
                  .all())
        for solve in solved:
            solved_set.add(solve.pid)

    now = time()
    for problem in database_session.query(Problem).all():
        if is_admin or (problem.comp_release and
                        competitions[problem.comp_release] < now):
            problems.append({
                'pid': problem.pid,
                'name': problem.name,
                'shortname': problem.shortname,
                'appeared': problem.appeared,
                'difficulty': problem.difficulty,
                'comp_release': problem.comp_release,
                'added': problem.added,
                'solved': problem.pid in solved_set,
                'url': url_for_problem(problem)
            })

    problems.sort(key=lambda x: x['name'])
    return serve_response(problems)


@app.route('/api/problems/', methods=['POST'])
@admin_required
def create_problem():
    """Add a new problem to the database and data folder"""
    try:
        # Convert the JSON to python array of dictionaries
        cases = request.form['cases']
        cases = loads(cases)
        for case in cases:
            if 'input' not in case or 'output' not in case:
                return serve_error(
                    'Sample case(s) were not formed correctly',
                    response_code=400)

        # Create the problem
        name = request.form['name'][:32]
        shortname = name.lower().replace(' ', '')
        problem = Problem(
            name=name,
            shortname=shortname
        )
        if 'difficulty' in request.form:
            problem.difficulty = request.form['difficulty']
        if 'appeared_in' in request.form:
            problem.appeared = request.form['appeared_in']
        if 'comp_release' not in request.form or int(
                request.form['comp_release']) < 1:
            problem.comp_release = None
        else:
            problem.comp_release = request.form['comp_release']

        # Create the problem data and add it to the database
        problem_data = ProblemData(
            description=request.form['description'],
            input_desc=request.form['input_desc'],
            output_desc=request.form['output_desc']
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
        return serve_error('Form field not found: ' + err.args[0],
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
@admin_required
def delete_problem(identifier):
    """Delete a specified problem in the database and data folder"""
    pid, problem = None, database_session.query(Problem)
    if is_pid(identifier):
        pid = identifier
        problem = problem.filter(Problem.pid == pid).first()
    else:
        problem = problem.filter(Problem.shortname == identifier).first()
        pid = problem.pid

    # Delete from problem_data table first to satisfy foreign key constraint
    problem_data = (database_session.query(ProblemData)
                    .filter(ProblemData.pid == pid))
    if not problem_data.first():
        return serve_error('Could not find problem data with pid ' +
                           pid, response_code=401)
    database_session.delete(problem_data.first())

    # Delete any and all sample cases associated w/ problem
    for case in (database_session.query(SampleCase)
                 .filter(SampleCase.pid == pid).all()):
        database_session.delete(case)

    # Delete from problem table
    database_session.delete(problem)

    # Commit changes
    database_session.flush()
    database_session.commit()

    # Delete judge data
    directory = os.path.join(app.config['DATA_FOLDER'], 'problems', str(pid))
    rmtree(directory)

    return serve_response({
        'deleted_pid': pid
    })

# Update a problem in the database
@app.route('/api/problems/<identifier>', methods=['PUT'])
@admin_required
def update_problem(identifier):
    """Modify a problem in the database and data folder"""
    pid, problem = None, database_session.query(Problem)
    if is_pid(identifier):
        pid = identifier
        problem = problem.filter(Problem.pid == pid).first()
    else:
        problem = problem.filter(Problem.shortname == identifier).first()
        pid = problem.pid

    data = database_session.query(ProblemData).filter(
        ProblemData.pid == pid).first()
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
        problem.difficulty = request.form['difficulty']

    # Save the changes
    problem.commit_to_session(database_session)
    data.commit_to_session(database_session)

    # If sample cases were uploaded, delete cases and go with the new ones
    case_lst = list()
    if 'cases' in request.form:
        for old_case in (database_session.query(SampleCase)
                         .filter(SampleCase.pid == pid).all()):
            database_session.delete(old_case)
            database_session.flush()
            database_session.commit()
        case_num = 1
        cases = loads(request.form['cases'])
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

    create_problem_directory(request.files, problem.pid)

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

def create_problem_directory(files, pid):
    """Creates the problem directory."""
    directory = os.path.join(app.config['DATA_FOLDER'], 'problems', str(pid))
    if not os.path.exists(directory):
        os.mkdir(directory)

    # Add judge data if supplied
    if 'in_file' in files:
        in_file = zipfile.ZipFile(files['in_file'])
        in_file.extractall(directory)

    if 'out_file' in files:
        out_file = zipfile.ZipFile(files['out_file'])
        out_file.extractall(directory)

    if 'sol_file' in files:
        if os.path.exists(directory + '/test'):
            rmtree(directory + '/test')
        os.mkdir(os.path.join(directory, 'test'))
        files['sol_file'].save(
            os.path.join(directory, 'test', files['sol_file'].filename))


def url_for_problem(problem):
    """Return the path of the pdf description of a problem"""
    return os.path.join('problems', str(problem.shortname),
                        'info.pdf')


def is_pid(identifier):
    """
    Returns true if identifier is an integer (representing the problem id)
    """
    try:
        int(identifier)
        return True
    except ValueError:
        return False


def comp_not_released(cid):
    """Returns true if a competition has not yet begun"""
    if cid is None:
        return False
    comp = database_session.query(Competition).filter_by(cid=cid).first()
    return comp.start > time()
