#!flask/bin/python
import glob
import math
import os

from app import app, database
from app.modules.submission_manager import judge
from app.modules.problem_manager.models import ProblemData
from app.modules.submission_manager.models import MockSubmission
from app.modules.submission_manager.judge_test import MockUploadFile


submit_file = None
MOCK_SUBMIT_DIR = os.path.join(
        app.config['DATA_FOLDER'], 'submits', 'mocksubmit')
PROBLEMS_DIR = os.path.join(app.config['DATA_FOLDER'], 'problems')


def setUp():
    """Prepare the test directory to run a new submit.

    1. Reconfigure app to go to the directory app/test_data
    2. Empty the submits directory
    """
    try:
        os.makedirs(
                os.path.join(app.config['DATA_FOLDER'], 'submits',
                'mocksubmit'))
    except OSError:
        pass


def _purgeDirectory(directory):
    """Recursively empty out the contents of a directory. USE WITH CAUTION.

    This is currently used to empty out the judge_tests/submits directory
    before and after tests are run.
    """
    for f in glob.glob(directory):
        if os.path.isfile(f):
            os.remove(f)
        if os.path.isdir(f):
            _purgeDirectory(os.path.join(f, '*'))
            os.rmdir(f)


def tearDown():
    """Post-test cleanup method.

    Close our submit_file so that we don't waste resources waiting on
    the garbage collector.

    Purge the judge_tests/submits directory post-test.
    """
    if submit_file:
        submit_file.close()
    try:
        _purgeDirectory(os.path.join(MOCK_SUBMIT_DIR, '*'))
        os.rmdir(MOCK_SUBMIT_DIR)
    except OSError:
        pass


def time_problems():
    problems = database.session.query(ProblemData).all()
    for problem in problems:
        path = os.path.join(PROBLEMS_DIR, str(problem.pid), 'test')
        files = os.listdir(path)
        if len(files) == 0:
            print('No sample solutions found for pid:', problem.pid)
        for fname in files:
            fyle = os.path.join(path, fname)
            if os.path.isfile(fyle):
                if not judge.allowed_filetype(fname):
                    # os.remove(fyle)
                    print('unsupported file: ', fyle)
                    os.remove(fyle)
                    continue
                # if fname.rsplit('.')[1] == 'cpp':
                #     print('skipping c++')
                #     continue

                file_type = fname.rsplit('.', 1)[1].lower()
                sub = MockSubmission(
                    username='Test User',
                    pid=problem.pid,
                    submit_time=0,
                    auto_id=0,
                    file_type=file_type,
                    result='start'
                )
                upload = MockUploadFile(fyle)
                upload.save(os.path.join(MOCK_SUBMIT_DIR, fname))
                judgement = judge.Judge(sub.pid, MOCK_SUBMIT_DIR, upload,
                        120 if file_type != 'cpp' else 10)
                status, time = judgement.run()

                if status == judge.CORRECT_ANSWER:
                    time_limit = time
                    problem.time_limit = time_limit
                    problem.commit_to_session()
                    print(problem.pid, time_limit)
                else:
                    print(problem.pid, 'judge solution incorrect')

if __name__ == '__main__':
    setUp()
    time_problems()
    tearDown()
