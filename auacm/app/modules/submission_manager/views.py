from flask import request
from flask.ext.login import login_user, logout_user, current_user, login_required
from app import app
from app.database import Base, session
from app.util import login_manager, serve_html, serve_response, serve_error
from .models import Submission
import os, time, subprocess
from os.path import isfile, join
from subprocess import Popen
from time import sleep

ALLOWED_EXTENSIONS = ['java', 'c', 'cpp', 'c++', 'py', 'go']

def allowed_filetype(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS
           
def directory_for_submission(submission):
    return join(app.config['DATA_FOLDER'], 'submits', str(submission.job))
    
def directory_for_problem(pid):
    return join(app.config['DATA_FOLDER'], 'problems', pid)

@app.route("/api/submit", methods=["POST"])
@login_required
def submit():
    file = request.files['file']
    if not allowed_filetype(file.filename):
        return serve_error('filename not allowed', response_code=403)
    attempt = Submission(username=current_user.username,\
            pid=request.form['pid'],\
            submit_time=int(time.time()),\
            auto_id=0,\
            file_type=file.filename.rsplit('.', 1)[1].lower(),\
            result='compile')
    session.add(attempt)
    session.flush()
    session.refresh(attempt)
    directory = directory_for_submission(attempt)
    os.mkdir(directory)
    file.save(join(directory, file.filename))
    start_execution(attempt, file)
    return serve_response('{}')

def start_execution(submission, file):
    if submission.file_type=='java':
        if compile_java(submission, file):
            return
        execute_java(submission, file)
    elif submission.file_type=='c' or submission.file_type=='cpp' or submission.file_type=='c++':
        result = compile_c(submission, file)
        if not result:
            return
        result = execute_c(submission, file)

def compile_java(submission, file):
    location = join(directory_for_submission(submission), file.filename)
    if subprocess.call(['javac', location]):
        return True
    else:
        submission.result = 'compile'
        session.flush()
        return False
        
def compile_c(submission, file):
    location = join(directory_for_submission(submission), file.filename)
    if subprocess.call(['g++', location]):
        return True
    else:
        submission.result = 'compile'
        session.flush()
        return False
        
def get_process_handle(submission, file):
    if submission.file_type=='java':
        return Popen(['java', '-cp',\
                directory_for_submission(submission),\
                file.filename.rsplit('.', 1)[0]],\
                stdin=open(join(input_path, in_file)),\
                stdout=open(join(sub_out_directory, "out" + str(test_num) + ".txt"), 'w'))
    elif submission.file_type=='c' or submission.file_type=='cpp' or submission.file_type=='c++':
        return Popen(join(directory_for_submission(submission), 'a.out')]\
                stdin=open(join(input_path, in_file)),\
                stdout=open(join(sub_out_directory, "out" + str(test_num) + ".txt"), 'w'))
    elif submission.file_type=='py':
        return Popen(['python', join(directory_for_submission(submission), file.filename)]\
                stdin=open(join(input_path, in_file)),\
                stdout=open(join(sub_out_directory, "out" + str(test_num) + ".txt"), 'w'))
    elif submission.file_type=='go':
        return Popen('go', 'run', join(directory_for_submission(submission), file.filename)\
                stdin=open(join(input_path, in_file)),\
                stdout=open(join(sub_out_directory, "out" + str(test_num) + ".txt"), 'w'))
    
def execute_java(submission, file):
    print 'beginning execution'
    directory = directory_for_problem(submission.pid)
    problem = session.query(Base.classes.problems)\
            .filter(Base.classes.problems.pid==submission.pid).first()
    input_path = join(directory, 'in')
    output_path = join(directory, 'out')
    input_files = [ f for f in os.listdir(input_path) if isfile(join(input_path, f)) ]
    sub_out_directory = join(directory_for_submission(submission), 'out')
    os.mkdir(sub_out_directory)

    for in_file in input_files:
        print 'checking file'
        test_num = int(in_file.rsplit('.', 1)[0][2:]) # what the fuck
        process_handle = get_process_handle(submission, file)
        
        timeout_time = time.time() + problem.time_limit
        while process_handle.poll() is None and time.time() < timeout_time:
            sleep(0.1)
            
        if process_handle.poll() is None:
            # timeout
            process_handle.terminate()
        
        with open(join(output_path, 'out' + str(test_num) + '.txt')) as test_output,\
                open(join(directory_for_submission(submission), 'out', 'out' + str(test_num) + '.txt')) as generated:
            test_lines = test_output.readlines()
            generated_lines = generated.readlines()
            
            if not len(generated_lines) == len(test_lines):
                print 'lengths different', len(generated_lines), len(test_lines)
                submission.result = 'wrong'
                session.flush()
                return
            
            for i in range(0, len(test_lines)):
                if not test_lines[i] == generated_lines[i]:
                    submission.result = 'wrong'
                    session.flush()
                    return

    submission.result = 'correct'
    session.flush()
    
