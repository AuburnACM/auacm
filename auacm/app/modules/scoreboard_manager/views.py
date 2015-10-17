from flask import request
from flask.ext.login import current_user, login_required
from app import app
from app.database import session
from app.util import serve_response, serve_error
from .models import Competition, CompProblem, CompUser
from app.modules.submission_manager.models import Submission
from app.modules.problem_manager.models import Problem
from sqlalchemy import asc

@app.route('/api/competitions/<int:cid>')
def getCompetitionData(cid):
    competition = session.query(Competition).filter(Competition.cid==cid).first()
    if competition is None:
        return serve_error('competition not found', response_code=404)
    comp_users = session.query(CompUser).filter(CompUser.cid==cid).all()
    comp_problems = set([p.pid for p in session.query(CompProblem).filter(CompProblem.cid==cid).all()])
    
    submissions = session.query(Submission)\
            .filter(Submission.submit_time>competition.start,\
                    Submission.submit_time<competition.stop)\
            .order_by(asc(Submission.submit_time))\
            .all()
    
    scoreboard = dict()
    
    team_users = dict()
    for user in comp_users:
        if not user.team in team_users:
            team_users[user.team] = list()
        team_users[user.team].append(user.username)
    
    for team in team_users.keys():
        team_row = dict()
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
            problem_time = incorrect*20*60+correct
            submit_count = 0
            if (correct > 0):
                submit_count = 1
            submit_count += incorrect+pointless
            team_row[problem] = {
                'problemTime' : problem_time,
                'submitCount' : submit_count,
                'status' : 'solved' if correct > 0 else 'unattempted' if submit_count == 0 else 'incorrect'
            }
        scoreboard[team] = team_row
        
    return serve_response(scoreboard)
    