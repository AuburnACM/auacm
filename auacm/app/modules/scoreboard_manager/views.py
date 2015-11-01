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
def getCompetitions():
    ongoing = list()
    past = list()
    upcoming = list()
    current_time = int(time())
    for competition in session.query(Competition).all():
        if competition.stop < current_time:
            past.append(create_competition_object(competition))
        elif competition.start < current_time:
            ongoing.append(create_competition_object(competition))
        else:
            upcoming.append(create_competition_object(competition))
    return serve_response({
        'ongoing' : ongoing,
        'past' : past,
        'upcoming' : upcoming
    })
    

def create_competition_object(competition):
    return {
        'cid' : competition.cid,
        'name' : competition.name,
        'startTime' : competition.start,
        'length' : competition.stop - competition.start
    }


@app.route('/api/competitions/<int:cid>')
def getCompetitionData(cid):
    competition = session.query(Competition).filter(Competition.cid==cid).first()
    if competition is None:
        return serve_error('competition not found', response_code=404)
    comp_users = session.query(CompUser).filter(CompUser.cid==cid).all()
    comp_problems = [p.pid for p in session.query(CompProblem).filter(CompProblem.cid==cid).all()]
    comp_problems.sort()
    
    submissions = session.query(Submission)\
            .filter(Submission.submit_time>competition.start,\
                    Submission.submit_time<competition.stop)\
            .order_by(asc(Submission.submit_time))\
            .all()
    
    scoreboard = list()
    
    team_users = dict()
    for user in comp_users:
        if not user.team in team_users:
            team_users[user.team] = list()
        team_users[user.team].append(user.username)
    
    for team in team_users.keys():
        team_problems = dict()
        for problem in comp_problems:
            correct = 0
            incorrect = 0
            pointless = 0
            for s in submissions:
                if not s.pid == problem or not s.username in team_users[team]:
                    continue
                elif correct > 0:
                    pointless += 1
                elif s.result == 'good':
                    correct = s.submit_time - competition.start
                else:
                    incorrect += 1
            problem_time = incorrect*20+correct/60
            submit_count = 0
            if (correct > 0):
                submit_count = 1
            submit_count += incorrect+pointless
            team_problems[problem] = {
                'problemTime' : problem_time,
                'submitCount' : submit_count,
                'status' : 'correct' if correct > 0 else 'unattempted' if submit_count == 0 else 'incorrect'
            }
        team_row = dict()
        team_row['name'] = team
        team_row['users'] = team_users[team]
        team_row['problemData'] = team_problems
        scoreboard.append(team_row)
        
    return serve_response({
        'competition' : create_competition_object(competition),
        'compProblems' : comp_problems,
        'teams' : scoreboard
    })
    