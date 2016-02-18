from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import session
from app.util import serve_response, serve_error, admin_required
from app.modules.flasknado.flasknado import Flasknado
from .models import Competition, CompProblem, CompUser
from app.modules.submission_manager.models import Submission
from app.modules.problem_manager.models import Problem
from app.modules.user_manager.models import User
from sqlalchemy import asc
from time import time
from json import loads


@app.route('/api/competitions')
def get_competitions():
    ongoing = list()
    past = list()
    upcoming = list()
    current_time = int(time())
    registered = set()

    if not current_user.is_anonymous:
        registered_rows = session.query(CompUser).filter(
                CompUser.username == current_user.username).all()
        for row in registered_rows:
            registered.add(row.cid)

    for competition in session.query(Competition).all():
        if competition.stop < current_time:
            past.append(competition.to_dict(
                user_registered=competition.cid in registered))
        elif competition.start < current_time:
            ongoing.append(competition.to_dict(
                user_registered=competition.cid in registered))
        else:
            upcoming.append(competition.to_dict(
                user_registered=competition.cid in registered))
    return serve_response({
        'ongoing': ongoing,
        'past': past,
        'upcoming': upcoming
    })


@app.route('/api/competitions', methods=['POST'])
@admin_required
def create_competition():
    data = request.form
    try:
        competition = Competition(
            name=data['name'],
            start=int(data['start_time']),
            stop=(int(data['start_time']) + int(data['length'])),
            closed=1 if bool(data['closed']) else 0
        )
        competition.commit_to_session()

        comp_problems = loads(data['problems'])
    except KeyError as err:
        return serve_error('You must specify name, startTime, length, and'
                ' problem attributes. ' + err[0] + ' not found.',
                response_code=400)
    except ValueError:
        return serve_error('JSON data for \'problems\' not properly formatted',
                response_code=400)

    for problem in comp_problems:
        session.add(CompProblem(
            label=problem['label'][:2],
            cid=competition.cid,
            pid=problem['pid']
        ))
    session.flush()
    session.commit()

    return serve_response(competition.to_dict())


@app.route('/api/competitions/<int:cid>', methods=['PUT'])
@admin_required
def update_competition_data(cid):
    """ Adds problems to a competition

    Doing a POST request adds that problem to the competition whereas
    a PUT request will remove all problems that were previously associated
    with that competition and add all of the ones in the form body.

    TODO: form validation to make sure that no duplicates are added.
    """

    data = request.form

    try:
        competition = session.query(Competition).filter(Competition.cid == cid)\
                .first()

        competition.name = data['name']
        competition.start=int(data['start_time'])
        competition.stop=(int(data['start_time']) + int(data['length']))
        competition.closed = 0 if bool(data['closed']) else 0
        competition.commit_to_session()

        # If the client sends a PUT request, we need to delete all of the old
        # problems associated with this competition
        session.query(CompProblem).filter(CompProblem.cid == cid).delete()

        comp_problems = loads(data['problems'])
    except KeyError as err:
        return serve_error('You must specify name, startTime, length, and'
                ' and problem attributes. ' + err[0] + ' not found.',
                response_code=400)
    except ValueError:
        return serve_error('JSON data for \'problems\' not properly formatted',
                response_code=400)

    for problem in comp_problems:
        session.add(CompProblem(
            label=problem['label'],
            cid=competition.cid,
            pid=problem['pid']
        ))

    session.flush()
    session.commit()
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
            problem_time = incorrect * 20 + correct // 60
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

    # Send the system time to the client
    Flasknado.emit('system_time', {'milliseconds': int(time() * 1000)})

    return serve_response({
        'competition': competition.to_dict(),
        'compProblems': comp_problems,
        'teams': scoreboard
    })


@app.route('/api/competitions/<int:cid>/register', methods=['POST'])
@login_required
def register_for_competition(cid):
    """ Called when a user wants to register for a competition.

    All the user has to do is submit a post to this url with no form data.
    From their logged-in status, we'll go ahead and add them to the competiton
    as an individual (team name is default their display name). A 400 error will
    be returned if the user is already registered for the competition.

    If the user that is submitting this is an admin, they can optionally
    supply a json array of usernames to register for the competition. Specifying
    this will not register the admin, but it will register all users that are
    listed. A 400 error will be returned if any of the users are already
    registered for the competition.
    """
    if session.query(Competition).filter(Competition.cid == cid).first() \
            is None:
        return serve_error('Competition does not exist', response_code=404)

    if current_user.admin == 1 and 'users' in request.form:
        try:
            registrants = loads(request.form['users'])
        except ValueError:
            return serve_error('JSON data for \'users\' not properly formatted',
                    response_code=400)
    else:
        registrants = [current_user.username]

    for user in registrants:
        if session.query(CompUser).filter(CompUser.cid == cid,
            CompUser.username == user).first() is not None:
            return serve_error('User ' + user + ' already registered for '
                    'competition', response_code=400)

    for username in registrants:
        user = session.query(User).filter(User.username == user).first()
        session.add(CompUser(
            cid=cid,
            username=user.username,
            team=user.display
        ))
        Flasknado.emit('new_user', {
            'cid': cid,
            'user': {
                'display': user.display,
                'username': user.username
            }
        })
    session.flush()
    session.commit()

    return serve_response({})


@app.route('/api/competitions/<int:cid>/unregister', methods=['POST'])
@login_required
def unregister_for_competition(cid):
    """ Called when a user wants to unregister for a competition.

    All the user has to do is submit a post to this url with no form data.
    From their logged-in status, we'll go ahead and remove them from the
    competiton.

    Similar to the <code>/register</code> endpoint, an admin can post a list of
    users to unregister from the competition.
    """
    if session.query(Competition).filter(Competition.cid == cid).first() \
            is None:
        return serve_error('Competition does not exist', response_code=404)

    if current_user.admin == 1 and 'users' in request.form:
        try:
            registrants = loads(request.form['users'])
        except ValueError:
            return serve_error('JSON data for \'users\' not properly formatted',
                    response_code=400)
    else:
        registrants = [current_user.username]

    for user in registrants:
        (session.query(CompUser)
                .filter(CompUser.username == user, CompUser.cid == cid)
                .delete())
    session.flush()
    session.commit()

    return serve_response({})


@app.route('/api/competitions/<int:cid>/teams', methods=['GET'])
@admin_required
def get_competition_teams(cid):
    """ Get all of the teams in a competition.

    Returns all of the teams, their users, and those users' display names.
    """
    comp_users = session.query(CompUser, User).join(User,
            User.username == CompUser.username).filter(CompUser.cid == cid)\
            .all()

    teams = dict()
    for user in comp_users:
        if user.CompUser.team not in teams:
            teams[user.CompUser.team] = list()
        teams[user.CompUser.team].append({
            'username': user.User.username,
            'display': user.User.display
        })

    return serve_response(teams)


@app.route('/api/competitions/<int:cid>/teams', methods=['PUT'])
@admin_required
def put_competition_teams(cid):
    """ Update the teams for a competition

    If a user is an admin, they can update the competition's users, doing a PUT.
    This will take the JSON data in the 'teams' part of the request form and
    store it to the database. Any teams or users not included in the JSON data
    will not be a part of the competition and will have to re-register; however
    it should not be used for the solely purpose of de-registering participants.
    """
    try:
        teams = loads(request.form['teams'])
    except KeyError as err:
        return serve_error('You must include the parameter \'teams\'.',
                response_code=400)
    except ValueError:
        return serve_error('JSON data for \'teams\' not properly formatted',
                response_code=400)

    # Delete all of the old CompUser rows for this competition
    session.query(CompUser).filter(CompUser.cid == cid).delete()

    for team in teams:
        for user in teams[team]:
            session.add(CompUser(
                cid=cid,
                username=user,
                team=team
            ))

    session.flush()
    session.commit()

    return serve_response({})
