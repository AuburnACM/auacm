from flask import render_template, jsonify
from app import app, session, Base, serve_html
# from app.modules.problem_manager.models import Problem

@app.route('/')
@app.route('/index')
def index():
    return render_template('login.html')

@app.route('/problems')
def getProblemsPage():
    return serve_html('problems.html')

# ideally, this would be broken out into a different module, but we can
# fix that later. For now, this works, and that's all that matters.
@app.route('/api/problems')
def getProblems():
    problems = list()
    for problem in session.query(Base.classes.problems).all():
        problems.append({
            'pid': problem.pid,
            'name': problem.name,
            'appeared': problem.appeared,
            'difficulty': problem.difficulty,
            'compRelease': problem.comp_release,
            'added': problem.added,
            'timeLimit': problem.time_limit
        })
    return jsonify({
        'status': 200,
        'data': problems
        }), 200
