'''Runs the problems and calculates the run time for them.'''
#!flask/bin/python
import sys

from app.database import DATABASE_SESSION
from app.modules.problem_manager.models import ProblemData
from app.modules.problem_manager import problem_timer


def time_problems():
    '''Runs the problems and calculates the run time for them.'''
    problems = DATABASE_SESSION.query(ProblemData).all()
    for problem in problems:
        problem_timer.Timer(problem).run()

def time_problem(problem_pid):
    '''Runs the problems and calculates the run time for them.'''
    problem = (DATABASE_SESSION.query(ProblemData)
               .filter(ProblemData.pid == problem_pid).first())
    problem_timer.Timer(problem).run()


if __name__ == '__main__':
    if len(sys.argv) == 2:
        for pid in map(int, sys.argv[1].split(',')):
            time_problem(pid)
    else:
        time_problems()
