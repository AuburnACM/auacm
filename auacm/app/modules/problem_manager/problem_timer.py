import math
import os
import shutil

from app import app
from app.modules.submission_manager import judge
from app.modules.submission_manager.models import MockSubmission
from app.modules.submission_manager.judge_test import MockUploadFile


PROBLEMS_DIR = os.path.join(app.config['DATA_FOLDER'], 'problems')


class Timer:

    def __init__(self, problem):
        self.problem = problem
        self.path = os.path.join(PROBLEMS_DIR, str(problem.pid), 'test')
        self.submission_path = os.path.join(os.getcwd(), 'temp', 'submits')

    def _set_up(self):
        try:
            os.makedirs(os.path.join(self.submission_path, str(self.problem.pid)))
        except OSError:
            pass

    def _tear_down(self):
        try:
            shutil.rmtree(self.submission_path)
        except OSError:
            pass

    def _time(self):
        """Times all of the problems in the problem's test folder

        The problem

        """
        files = os.listdir(self.path)
        if len(files) == 0:
            raise NoJudgeSolitionsError(self.problem.pid)

        for fname in files:
            source = os.path.join(self.path, fname)
            if not os.path.isfile(source):
                continue
            if not judge.allowed_filetype(fname):
                raise UnsupportedFileTypeError(fname, self.problem.pid)

            file_type = fname.rsplit('.', 1)[1].lower()
            sub = MockSubmission(
                pid=self.problem.pid, file_type=file_type, job=self.problem.pid)
            upload = MockUploadFile(source)
            upload.save(os.path.join(self.submission_path, str(sub.job), fname))
            judgement = judge.Judge(sub, self.submission_path, upload, 120 if file_type != 'cpp' else 10)
            status, time = judgement.run()

            if status == judge.CORRECT_ANSWER:
                time_limit = int(math.ceil(
                        time * 1.5 / judge.TIMEOUT_MULTIPLIER[file_type]))
                self.problem.time_limit = time_limit
                self.problem.commit_to_session()
                return True
            else:
                return False

    def run(self):
        """

        """
        self._set_up()
        result = self._time()
        self._tear_down()
        return result


class NoJudgeSolitionsError(Exception):

    def __init__(self, value):
        Exception.__init__(self)
        self.value = value

    def __str__(self):
        return 'No judge solutions found for PID: ' + repr(self.value)


class UnsupportedFileTypeError(Exception):

    def __init__(self, file_type, pid):
        Exception.__init__(self)
        self.file_type = file_type
        self.pid = pid

    def __str__(self):
        return ('File type ' + repr(self.file_type) + ' found in test folder '
                'for PID ' + repr(self.pid))
