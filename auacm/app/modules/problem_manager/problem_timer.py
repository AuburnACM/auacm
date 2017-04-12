"""
Calculates the time length for each problem.
"""
import math
import os
import shutil
import sys

from app import config
from app.database import database_session
from app.modules.problem_manager import models
from app.modules.submission_manager import judge
from app.modules.submission_manager.models import MockSubmission
from app.modules.submission_manager.judge_test import MockUploadFile

TIME_LIMIT = 20

PROBLEMS_DIR = os.path.join(config.DATA_FOLDER, 'problems')


class Timer:
    """Timer times all of the judge solutions for a problem."""

    def __init__(self, problem):
        self.problem = problem
        self.path = os.path.join(PROBLEMS_DIR, str(problem.pid), 'test')
        self.submission_path = os.path.join(config.TEMP_DIRECTORY, 'submits')

    def _create_temp_directory(self):
        """Attempts to create a temp directory.

        In the event that we can't create the temp directory, an OSError will
        be raised. In that case, you probably want to specify a different
        directory to create the temp files in in config.py.
        """
        os.makedirs(os.path.join(self.submission_path, str(self.problem.pid)))

    def _remove_temp_files(self):
        """Removes the temporary files.

        In the event that we can't remove the file tree that was created, an
        OSError will be raised. In that case, you should probably choose a new
        temp directory as well as delete the old temp directory yourself.
        """
        shutil.rmtree(self.submission_path)

    def _run(self):
        """Times all of the problems in the problem's test folder.

        This will iterate through all of the files in the problem's test
        folder. For each file, it creates a mock submission (so that it does
        not have to store a submission in the database), and a "Mock" upload
        file. The "Mock" upload file still depends on the computer's
        filesystem, because the Judge is dependent upon the computer's
        filesystem.

        Each file is run in serial, and the maximum time of all of the judge
        submissions, times 1.5 (just to give people some wiggle-room). Then,
        it is "standardized" by dividing by the judge's TIMEOUT_MULTIPLIER for
        the given file type.

        :raises NoJudgeSolutionError: in the event that no judge solution is
            found for the given problem.
        """
        files = os.listdir(self.path)

        judged_count = 0
        found_max = 0
        problem_details = (database_session.query(models.Problem)
                           .filter(models.Problem.pid == self.problem.pid).first())
        print('Judging', problem_details.name, '...', file=sys.stderr)
        for fname in files:
            source = os.path.join(self.path, fname)
            if not os.path.isfile(source):
                continue
            if not judge.allowed_filetype(fname):
                raise UnsupportedFileTypeError(fname, self.problem.pid)

            print('    Trying', fname, '...', file=sys.stderr)

            file_type = fname.rsplit('.', 1)[1].lower()
            sub = MockSubmission(
                pid=self.problem.pid,
                file_type=file_type,
                job=self.problem.pid
            )
            submission_path = os.path.join(self.submission_path, str(sub.job))
            upload = MockUploadFile(source)
            upload.save(os.path.join(submission_path, fname))

            judgement = judge.Judge(sub.pid, submission_path, upload, TIME_LIMIT)
            status, time = judgement.run()

            if status == judge.CORRECT_ANSWER:
                judged_count += 1
                time_limit = int(math.ceil(
                    time * 1.5 / judge.TIMEOUT_MULTIPLIER[file_type]))
                found_max = max(found_max, time_limit)
                self.problem.time_limit = found_max
                self.problem.commit_to_session()

        if judged_count == 0:
            raise NoJudgeSolutionsError(self.problem.pid, problem_details.name)
        print('Judged', problem_details.name, 'with time', found_max)

    def run(self):
        """Executes the problem timer."""
        self._create_temp_directory()
        try:
            self._run()
        except NoJudgeSolutionsError as error:
            print(str(error), file=sys.stderr)
        finally:
            self._remove_temp_files()


class NoJudgeSolutionsError(Exception):
    """An error that is thrown when a judge solution does not exist for a problem."""
    def __init__(self, value, name):
        Exception.__init__(self)
        self.value = value
        self.name = name

    def __str__(self):
        return ('No judge solutions found for PID: ' + repr(self.value) +
                ' (' + self.name + ')')


class UnsupportedFileTypeError(Exception):
    """An error that is thrown when a problem file type is not supported."""
    def __init__(self, file_type, pid):
        Exception.__init__(self)
        self.file_type = file_type
        self.pid = pid

    def __str__(self):
        return ('File type ' + repr(self.file_type) + ' found in test folder '
                'for PID ' + repr(self.pid))
