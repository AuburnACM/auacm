#!flask/bin/python
import sys

from app import database
from app.modules.problem_manager import models
from app.modules.problem_manager import problem_timer


def time_problems():
    problems = database.session.query(models.ProblemData).all()
    for problem in problems:
        problem_timer.Timer(problem).run()

def time_problem(pid):
    problem = (database.session.query(models.ProblemData)
               .filter(models.ProblemData.pid == pid).first())
    problem_timer.Timer(problem).run()


if __name__ == '__main__':
    if len(sys.argv) == 2:
        for pid in map(int, sys.argv[1].split(',')):
            time_problem(pid)
    else:
        time_problems()
