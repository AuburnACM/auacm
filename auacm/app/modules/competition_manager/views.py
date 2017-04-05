"""
This is the controller for the competition manager.
"""
from json import loads
from time import time

from flask import request
from flask.ext.login import current_user, login_required
from sqlalchemy import asc
from ...modules import APP
from ...util import serve_response, serve_error, admin_required
from ..flasknado.flasknado import Flasknado
from ..submission_manager.models import Submission
from ..problem_manager.models import Problem
from ..user_manager.models import User
from ...database import DATABASE_SESSION, commit_to_session
from .models import Competition, CompProblem, CompUser


@APP.route('/api/competitions')
def get_competitions():
    '''Returns a list of competitions.'''
    ongoing = list()
    past = list()
    upcoming = list()
    current_time = int(time())
    registered = set()

    if not current_user.is_anonymous:
        registered_rows = DATABASE_SESSION.query(CompUser).filter(
            CompUser.username == current_user.username).all()
        for row in registered_rows:
            registered.add(row.cid)

    for competition in DATABASE_SESSION.query(Competition).all():
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


@APP.route('/api/competitions', methods=['POST'])
@admin_required
def create_competition():
    '''Creates a new competition.'''

    data = request.form
    try:
        competition = Competition(
            name=data['name'],
            start=int(data['start_time']),
            stop=(int(data['start_time']) + int(data['length'])),
            closed=1 if bool(data['closed']) else 0
        )
        commit_to_session(competition)

        comp_problems = loads(data['problems'])
    except KeyError as err:
        return serve_error('You must specify name, startTime, length, and'
                           ' problem attributes. ' + err.args[0] + ' not found.',
                           response_code=400)
    except ValueError:
        return serve_error('JSON data for \'problems\' not properly formatted',
                           response_code=400)

    for problem in comp_problems:
        DATABASE_SESSION.add(
            CompProblem(
                label=problem['label'][:2],
                cid=competition.cid,
                pid=problem['pid']
            )
        )
    DATABASE_SESSION.flush()
    DATABASE_SESSION.commit()

    return serve_response(competition.to_dict())


@APP.route('/api/competitions/<int:cid>', methods=['PUT'])
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
        competition = DATABASE_SESSION.query(Competition).filter(
            Competition.cid == cid).first()

        competition.name = data['name']
        competition.start = int(data['start_time'])
        competition.stop = (int(data['start_time']) + int(data['length']))
        competition.closed = 0 if bool(data['closed']) else 0
        commit_to_session(competition)

        # If the client sends a PUT request, we need to delete all of the old
        # problems associated with this competition
        DATABASE_SESSION.query(CompProblem).filter(
            CompProblem.cid == cid).delete()

        comp_problems = loads(data['problems'])
    except KeyError as err:
        return serve_error('You must specify name, startTime, length, and'
                           ' and problem attributes. ' + err.args[0] + ' not found.',
                           response_code=400)
    except ValueError:
        return serve_error('JSON data for \'problems\' not properly formatted',
                           response_code=400)

    for problem in comp_problems:
        DATABASE_SESSION.add(CompProblem(
            label=problem['label'],
            cid=competition.cid,
            pid=problem['pid']
        ))

    DATABASE_SESSION.flush()
    DATABASE_SESSION.commit()
    return serve_response(competition.to_dict())


@APP.route('/api/competitions/<int:cid>')
def get_competition_data(cid):
    '''Returns information about the competition using the cid.'''
    competition = DATABASE_SESSION.query(Competition).filter(
        Competition.cid == cid).first()
    if competition is None:
        return serve_error('competition not found', response_code=404)
    comp_users = DATABASE_SESSION.query(CompUser).filter(
        CompUser.cid == cid).all()

    comp_problems = dict()
    for prob in (DATABASE_SESSION.query(CompProblem, Problem)
                 .join(Problem).filter(CompProblem.cid == cid)
                 .all()):
        comp_problems[prob.CompProblem.label] = {
            'pid': prob.Problem.pid,
            'name': prob.Problem.name,
            'shortname': prob.Problem.shortname
        }

    submissions = (DATABASE_SESSION.query(Submission)
                   .filter(Submission.submit_time > competition.start,
                           Submission.submit_time < competition.stop)
                   .order_by(asc(Submission.submit_time))\
                   .all())

    team_users = dict()
    team_display_names = dict()

    all_users = DATABASE_SESSION.query(User).all()
    name_to_display = dict()
    for user in all_users:
        name_to_display[user.username] = user.display

    for user in comp_users:
        if not user.team in team_users:
            team_users[user.team] = list()
            team_display_names[user.team] = list()
        team_users[user.team].append(user.username)
        team_display_names[user.team].append(name_to_display[user.username])

    return serve_response({
        'competition': competition.to_dict(),
        'compProblems': comp_problems,
        'teams': get_scoreboard(team_users, comp_problems,\
            submissions, competition, team_display_names)
    })

def get_scoreboard(team_users, comp_problems, submissions, competition, team_display_names):
    '''Calculates the scoreboard data.'''
    scoreboard = list()
    for team in team_users:
        team_problems = dict()
        for name in comp_problems:
            problem = comp_problems[name]
            status = dict()
            status['correct'] = 0
            status['incorrect'] = 0
            for submission in submissions:
                if not submission.pid == problem['pid'] or\
                        submission.username not in team_users[team]:
                    continue
                elif status['correct'] > 0:
                    break
                elif submission.result == 'good':
                    status['correct'] = submission.submit_time - competition.start
                else:
                    status['incorrect'] += 1
            submit_time = status['correct'] // 60
            submit_count = 0
            if status['correct'] > 0:
                submit_count = 1
            submit_count += status['incorrect']
            team_problems[comp_problems[name]['pid']] = {
                'label': name,
                'submitTime': submit_time,
                'submitCount': submit_count,
                'status': 'correct' if status['correct'] > 0 else\
                    'unattempted' if submit_count == 0 else 'incorrect'
            }
        team_row = dict()
        team_row['name'] = team
        team_row['users'] = team_users[team]
        team_row['display_names'] = team_display_names[team]
        team_row['problemData'] = team_problems
        scoreboard.append(team_row)
    return scoreboard

@APP.route('/api/competitions/<int:cid>', methods=['DELETE'])
def delete_competition(cid):
    """Delete a competition and all the data associated with it"""
    competition = (DATABASE_SESSION.query(Competition)
                   .filter_by(cid=cid).first())

    if not competition:
        return serve_error('404: Competition not found', 404)

    # Delete the problems from a competition
    competition_problems = (DATABASE_SESSION.query(CompProblem)
                            .filter_by(cid=cid).all())
    for competition_problem in competition_problems:
        DATABASE_SESSION.delete(competition_problem)

    # Delete the users/tams from a competition
    competition_users = (DATABASE_SESSION.query(CompUser)
                         .filter_by(cid=cid).all())
    for user in competition_users:
        DATABASE_SESSION.delete(user)

    # Delete the competition itself
    DATABASE_SESSION.delete(competition)
    DATABASE_SESSION.commit()

    return serve_response('Successfully deleted', 204)


@APP.route('/api/competitions/<int:cid>/register', methods=['POST'])
@login_required
def register_for_competition(cid):
    """ Called when a user wants to register for a competition.

    All the user has to do is submit a post to this url with no form data.
    From their logged-in status, we'll go ahead and add them to the competiton
    as an individual (team name is default their display name). A 400 error
    will be returned if the user is already registered for the competition.

    If the user that is submitting this is an admin, they can optionally
    supply a json array of usernames to register for the competition.
    Specifying this will not register the admin, but it will register all users
    that are listed. A 400 error will be returned if any of the users are
    already registered for the competition.
    """
    if DATABASE_SESSION.query(Competition).filter(\
         Competition.cid == cid).first() is None:
        return serve_error('Competition does not exist', response_code=404)

    if current_user.admin == 1 and 'users' in request.form:
        try:
            registrants = loads(request.form['users'])
        except ValueError:
            return serve_error(
                'JSON data for \'users\' not properly formatted',
                response_code=400)
    else:
        registrants = [current_user.username]

    for user in registrants:
        if DATABASE_SESSION.query(CompUser).filter(
                CompUser.cid == cid, CompUser.username == user
            ).first() is not None:
            return serve_error('User ' + user + ' already registered for '\
                    'competition', response_code=400)

    for username in registrants:
        user = DATABASE_SESSION.query(User).filter(
            User.username == username).first()
        DATABASE_SESSION.add(
            CompUser(
                cid=cid,
                username=user.username,
                team=user.display
            )
        )
        Flasknado.emit('new_user', {
            'cid': cid,
            'user': {
                'display': user.display,
                'username': user.username
            }
        })
    DATABASE_SESSION.flush()
    DATABASE_SESSION.commit()

    return serve_response({})


@APP.route('/api/competitions/<int:cid>/unregister', methods=['POST'])
@login_required
def unregister_for_competition(cid):
    """ Called when a user wants to unregister for a competition.

    All the user has to do is submit a post to this url with no form data.
    From their logged-in status, we'll go ahead and remove them from the
    competiton.

    Similar to the <code>/register</code> endpoint, an admin can post a list of
    users to unregister from the competition.
    """
    if DATABASE_SESSION.query(Competition).filter(
            Competition.cid == cid).first() is None:
        return serve_error('Competition does not exist', response_code=404)

    if current_user.admin == 1 and 'users' in request.form:
        try:
            registrants = loads(request.form['users'])
        except ValueError:
            return serve_error('JSON data for \'users\' not properly formatted',\
                response_code=400)
    else:
        registrants = [current_user.username]

    for user in registrants:
        (DATABASE_SESSION.query(CompUser)
         .filter(CompUser.username == user, CompUser.cid == cid)
         .delete())
    DATABASE_SESSION.flush()
    DATABASE_SESSION.commit()

    return serve_response({})


@APP.route('/api/competitions/<int:cid>/teams', methods=['GET'])
@admin_required
def get_competition_teams(cid):
    """ Get all of the teams in a competition.

    Returns all of the teams, their users, and those users' display names.
    """
    comp_users = (DATABASE_SESSION.query(CompUser, User)
                  .join(User, User.username == CompUser.username)
                  .filter(CompUser.cid == cid)
                  .all()
                 )

    teams = dict()
    for user in comp_users:
        if user.CompUser.team not in teams:
            teams[user.CompUser.team] = list()
        teams[user.CompUser.team].append({
            'username': user.User.username,
            'display': user.User.display
        })

    return serve_response(teams)


@APP.route('/api/competitions/<int:cid>/teams', methods=['PUT'])
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
    except KeyError as _err:
        return serve_error('You must include the parameter \'teams\'.',
                           response_code=400)
    except ValueError:
        return serve_error('JSON data for \'teams\' not properly formatted',
                           response_code=400)

    # Delete all of the old CompUser rows for this competition
    DATABASE_SESSION.query(CompUser).filter(CompUser.cid == cid).delete()

    for team in teams:
        for user in teams[team]:
            DATABASE_SESSION.add(
                CompUser(
                    cid=cid,
                    username=user,
                    team=team
                )
            )

    DATABASE_SESSION.flush()
    DATABASE_SESSION.commit()

    return serve_response({})

# Register a socket callback to send the server time
@Flasknado.on('system_time')
def send_system_time(connection):
    '''Sends the system time so the user's local time can be synced.'''
    Flasknado.send(connection, 'system_time',
                   {'milliseconds': int(time() * 1000)})
