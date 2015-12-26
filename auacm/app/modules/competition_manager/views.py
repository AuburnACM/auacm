from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import session
from app.util import serve_response, serve_error
from .models import Competition, CompProblem, CompUser
from app.modules.submission_manager.models import Submission
from app.modules.problem_manager.models import Problem
from sqlalchemy import asc
from time import time


@app.route('/api/competitions')
@login_required
def get_competitions():
    ongoing = list()
    past = list()
    upcoming = list()
    current_time = int(time())
    for competition in session.query(Competition).all():
        if competition.stop < current_time:
            past.append(competition.to_dict())
        elif competition.start < current_time:
            ongoing.append(competition.to_dict())
        else:
            upcoming.append(competition.to_dict())
    return serve_response({
        'ongoing': ongoing,
        'past': past,
        'upcoming': upcoming
    })


@app.route('/api/competitions', methods=['POST'])
@login_required
def create_competition():
    data = request.form
    if current_user.admin == 0:
        return serve_error('Only admins can create competitions', 401)
    if not data['name'] or not data['startTime'] or not data['length']:
        return serve_error('You must specify name, startTime,' +
                ' and length attributes')
    competition = Competition(
        name=data['name'],
        start=int(data['startTime']),
        stop=(int(data['startTime']) + int(data['length']))
    )
    competition.commit_to_session()
    return serve_response(competition.to_dict())


@app.route('/api/competitions/<int:cid>')
def get_competition_data(cid):
    competition = session.query(Competition).filter(Competition.cid == cid).\
            first()
    if competition is None:
        return serve_error('competition not found', response_code=404)
    comp_users = session.query(CompUser).filter(CompUser.cid == cid).all()

    comp_problems = dict()
    for prob in session.query(CompProblem, Problem).join(Problem).\
            filter(CompProblem.cid == cid).all():
        comp_problems[prob.CompProblem.label] = {
            'pid': prob.Problem.pid,
            'name': prob.Problem.name,
            'shortname': prob.Problem.shortname
        }

    submissions = session.query(Submission)\
            .filter(Submission.submit_time > competition.start,\
                    Submission.submit_time < competition.stop)\
            .order_by(asc(Submission.submit_time))\
            .all()

    scoreboard = list()

    team_users = dict()
    for user in comp_users:
        if not user.team in team_users:
            team_users[user.team] = list()
        team_users[user.team].append(user.username)

    for team in team_users:
        team_problems = dict()
        for name in comp_problems:
            problem = comp_problems[name]
            correct = 0
            incorrect = 0
            pointless = 0
            for s in submissions:
                if not s.pid == problem['pid'] or s.username not in team_users[team]:
                    continue
                elif correct > 0:
                    pointless += 1
                elif s.result == 'good':
                    correct = s.submit_time - competition.start
                else:
                    incorrect += 1
            problem_time = incorrect * 20 + correct / 60
            submit_count = 0
            if correct > 0:
                submit_count = 1
            submit_count += incorrect + pointless
            team_problems[comp_problems[name]['pid']] = {
                'label': name,
                'problemTime': problem_time,
                'submitCount': submit_count,
                'status': 'correct' if correct > 0 else 'unattempted' if submit_count == 0 else 'incorrect'
            }
        team_row = dict()
        team_row['name'] = team
        team_row['users'] = team_users[team]
        team_row['problemData'] = team_problems
        scoreboard.append(team_row)

    return serve_response({
        'competition': competition.to_dict(),
        'compProblems': comp_problems,
        'teams': scoreboard
    })


@app.route('/api/competitions/<int:cid>', methods=['POST', 'PUT'])
@login_required
def update_competition_data(cid):
    """ Adds problems to a competition

    Doing a POST request adds that problem to the competition whereas
    a PUT request will remove all problems that were previously associated
    with that competition and add all of the ones in the form body.

    TODO: form validation to make sure that no duplicates are added.
    """
    if current_user.admin == 0:
        # admins only
        return serve_error('Only admins can modify competitions', 401)

    if request.method == 'PUT':
        # If the client sends a PUT request, we need to delete all of the old
        # problems associated with this competition
        session.query(CompProblem).filter(CompProblem.cid==cid).delete()

    for problem in request.json['problemIds']:
        session.add(CompProblem(cid=cid, pid=problem))

    session.flush()
    session.commit()
    return serve_response({
        'competition': competition.to_dict(),
        'compProblems': comp_problems,
        'teams': scoreboard
    })
