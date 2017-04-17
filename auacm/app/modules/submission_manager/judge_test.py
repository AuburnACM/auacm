# pylint: disable=I0011,W0212
"""Tests for the judge.

To run these tests, execute the following command on the app level:

<code>
./test.py
</code>

Interesting fact, Python unit tests are supposed to use camelCase instead of
snake_case.
"""
import glob
import os
import os.path
import unittest

from app.modules.submission_manager import models, judge
from app.modules import app


class JudgeTest(unittest.TestCase, object):
    """Mixin class for building tests of the judge in different langauges.

    This makes 6 methods mandatory for implementation.  The goal is to
    make it so that we can't add a language without adding unit tests
    to verify the behavior of submissions in that language.
    """
    submit = None
    submit_file = None
    judge = None
    def __init__(self):
        super().__init__()
    def set_up(self):
        """Prepare the test directory to run a new submit.

        1. Reconfigure app to go to the directory app/test_data
        2. Empty the submits directory
        """
        app.config['DATA_FOLDER'] = os.getcwd() + '/judge_tests'
        directory = os.path.join(app.config['DATA_FOLDER'], 'submits', '*')
        self._purge_directory(directory)
        self.submit, self.submit_file = None, None

    def _purge_directory(self, directory):
        """Recursively empty out the contents of a directory. USE WITH CAUTION.

        This is currently used to empty out the judge_tests/submits directory
        before and after tests are run.
        """
        for file in glob.glob(directory):
            if os.path.isfile(file):
                os.remove(file)
            if os.path.isdir(file):
                self._purge_directory(os.path.join(file, '*'))
                os.rmdir(file)

    def create_mock_submission(self, problem, filename):
        """Create a MockSubmission to use for a test run.

        Creates two local fields, submit and submit_file containing the
        submission and code file to run for this submission.

        :param problem: the problem this submission is for.
        :param filename: the name of the code file located in
            app/test_data/<problem>/solutions/ to run.
        :return: None
        """
        data_folder = app.config['DATA_FOLDER']
        _, ext = filename.rsplit('.', 1)
        submit = models.MockSubmission(
            username='Test User',
            pid=problem,
            submit_time=0,
            auto_id=0,
            file_type=ext,
            result='start'
        )
        file_path = os.path.join(
            data_folder, 'problems', problem, 'solutions', filename)
        submit_directory = os.path.join(app.config['DATA_FOLDER'], 'submits',
                                        str(submit.job))
        if not os.path.exists(submit_directory):
            os.mkdir(submit_directory)
        # Keep track of submit_file so that we can close it.
        self.submit = submit
        self.submit_file = MockUploadFile(file_path)
        self.judge = judge.Judge(self.submit.pid, submit_directory,
                                 self.submit_file, 1)

    def assert_evaluation(self, expected_result):
        """Assert the behavior of a submission.

        A MockSubmission must first be created with create_mock_submission.

        :param expected_result: the expected result for this submission.
            Normally one of the constants exposed by the judge module.
        :return: None
        """
        self.submit_file.save(
            os.path.join(self.judge.submission_path,
                         self.submit_file.filename))
        result, _ = self.judge.run()
        self.assertEqual(
            expected_result,
            result)

    def assert_compilation(self, expected_result):
        """Assert the behavior of a compilation.

        A MockSubmission must first be created with create_mock_submission.

        :param expected_result: the expected result for this submission.
            Normally one of the constants exposed by the judge module.
        :return: None
        """
        self.submit_file.save(
            os.path.join(self.judge.submission_path, self.submit_file.filename))
        self.assertEqual(
            expected_result,
            self.judge._compile_submission())

    def tearDown(self):
        """Post-test cleanup method.

        Close our submit_file so that we don't waste resources waiting on
        the garbage collector.

        Purge the judge_tests/submits directory post-test.
        """
        if self.submit_file:
            self.submit_file.close()
        directory = os.path.join(app.config['DATA_FOLDER'], 'submits', '*')
        self._purge_directory(directory)

    def test_no_compile(self):
        """Raises an error."""
        raise NotImplementedError('Subclasses must implement this test!')

    def test_compile(self):
        """Raises an error."""
        raise NotImplementedError('Subclasses must implement this test!')

    def test_runtime_error(self):
        """Raises an error."""
        raise NotImplementedError('Subclasses must implement this test!')

    def test_timelimit_error(self):
        """Raises an error."""
        raise NotImplementedError('Subclasses must implement this test!')

    def test_wrong_answer(self):
        """Raises an error."""
        raise NotImplementedError('Subclasses must implement this test!')

    def test_right_answer(self):
        """Raises an error."""
        raise NotImplementedError('Subclasses must implement this test!')


class JavaTest(JudgeTest):
    """Tests for .java submissions."""

    __test_ext__ = "java"

    def test_no_compile(self):
        self.create_mock_submission('addnumbers', 'CompileError.java')
        self.assert_evaluation(judge.COMPILATION_ERROR)

    def test_compile(self):
        self.create_mock_submission('addnumbers', 'CompileSuccess.java')
        self.assert_compilation(judge.COMPILATION_SUCCESS)

    def test_runtime_error(self):
        self.create_mock_submission('addnumbers', 'RuntimeError.java')
        self.assert_evaluation(judge.RUNTIME_ERROR)

    def test_timelimit_error(self):
        self.create_mock_submission('addnumbers', 'TimelimitError.java')
        self.assert_evaluation(judge.TIMELIMIT_EXCEEDED)

    def test_wrong_answer(self):
        self.create_mock_submission('addnumbers', 'WrongAnswer.java')
        self.assert_evaluation(judge.WRONG_ANSWER)

    def test_right_answer(self):
        self.create_mock_submission('addnumbers', 'RightAnswer.java')
        self.assert_evaluation(judge.CORRECT_ANSWER)


class PythonTest(JudgeTest):
    """Tests for .py submissions."""
    # TODO (djshuckerow): tests for python2 and python3.

    __test_ext__ = "py"

    def test_no_compile(self):
        pass

    def test_compile(self):
        pass

    def test_runtime_error(self):
        self.create_mock_submission('addnumbers', 'runtime.py')
        self.assert_evaluation(judge.RUNTIME_ERROR)

    def test_timelimit_error(self):
        self.create_mock_submission('addnumbers', 'timelimit.py')
        self.assert_evaluation(judge.TIMELIMIT_EXCEEDED)

    def test_wrong_answer(self):
        self.create_mock_submission('addnumbers', 'wrong.py')
        self.assert_evaluation(judge.WRONG_ANSWER)

    def test_right_answer(self):
        self.create_mock_submission('addnumbers', 'right.py')
        self.assert_evaluation(judge.CORRECT_ANSWER)


class CTest(JudgeTest):
    """Tests for .c submissions."""

    __test_ext__ = "c"

    def test_no_compile(self):
        self.create_mock_submission('addnumbers', 'compileerror.c')
        self.assert_evaluation(judge.COMPILATION_ERROR)

    def test_compile(self):
        self.create_mock_submission('addnumbers', 'compilesuccess.c')
        self.assert_compilation(judge.COMPILATION_SUCCESS)

    def test_runtime_error(self):
        self.create_mock_submission('addnumbers', 'runtimeerror.c')
        self.assert_evaluation(judge.RUNTIME_ERROR)

    def test_timelimit_error(self):
        self.create_mock_submission('addnumbers', 'timelimiterror.c')
        self.assert_evaluation(judge.TIMELIMIT_EXCEEDED)

    def test_wrong_answer(self):
        self.create_mock_submission('addnumbers', 'wronganswer.c')
        self.assert_evaluation(judge.WRONG_ANSWER)

    def test_right_answer(self):
        self.create_mock_submission('addnumbers', 'rightanswer.c')
        self.assert_evaluation(judge.CORRECT_ANSWER)


class CppTest(JudgeTest):
    """Tests for .cpp submissions."""

    __test_ext__ = "cpp"

    def test_no_compile(self):
        self.create_mock_submission('addnumbers', 'compileerror.cpp')
        result, _ = self.judge.run()
        self.assertEqual(
            judge.COMPILATION_ERROR,
            result)

    def test_compile(self):
        self.create_mock_submission('addnumbers', 'compilesuccess.cpp')
        self.assert_compilation(judge.COMPILATION_SUCCESS)

    def test_runtime_error(self):
        self.create_mock_submission('addnumbers', 'runtimeerror.cpp')
        self.assert_evaluation(judge.RUNTIME_ERROR)

    def test_timelimit_error(self):
        self.create_mock_submission('addnumbers', 'timelimiterror.cpp')
        self.assert_evaluation(judge.TIMELIMIT_EXCEEDED)

    def test_wrong_answer(self):
        self.create_mock_submission('addnumbers', 'wronganswer.cpp')
        self.assert_evaluation(judge.WRONG_ANSWER)

    def test_right_answer(self):
        self.create_mock_submission('addnumbers', 'rightanswer.cpp')
        self.assert_evaluation(judge.CORRECT_ANSWER)


class GoTest(JudgeTest):
    """Tests for .go submissions."""

    __test_ext__ = "go"

    def test_no_compile(self):
        self.create_mock_submission('addnumbers', 'compileerror.go')
        self.assert_evaluation(judge.COMPILATION_ERROR)

    def test_compile(self):
        self.create_mock_submission('addnumbers', 'compilesuccess.go')
        self.assert_compilation(judge.COMPILATION_SUCCESS)

    def test_runtime_error(self):
        self.create_mock_submission('addnumbers', 'runtimeerror.go')
        self.assert_evaluation(judge.RUNTIME_ERROR)

    def test_timelimit_error(self):
        self.create_mock_submission('addnumbers', 'timelimiterror.go')
        self.assert_evaluation(judge.TIMELIMIT_EXCEEDED)

    def test_wrong_answer(self):
        self.create_mock_submission('addnumbers', 'wronganswer.go')
        self.assert_evaluation(judge.WRONG_ANSWER)

    def test_right_answer(self):
        self.create_mock_submission('addnumbers', 'rightanswer.go')
        self.assert_evaluation(judge.CORRECT_ANSWER)


class MockUploadFile(object):
    """Mock upload file that has a save method.

    The judge uses the object this mocks out to save the file it receives from
    the judge api call.  The file is then saved to the submission directory so
    that it can be run.
    """

    def __init__(self, location):
        self.file = open(location)
        self.filename = os.path.split(location)[1]

    def save(self, location):
        """Saves a file."""
        with open(location, 'w') as to_save:
            to_save.write(self.file.read())

    def close(self):
        """Closes a file."""
        self.file.close()


class LanguageTest(unittest.TestCase):
    """Make sure that we have tests for all the languages we support.

    This test uses a reflection pattern to get all our tests and verify that
    a test for each language we support is implemented.
    """

    def test_languages_are_all_tested(self):
        """Checks to make sure all languages have been tested."""
        found_languages = set()
        for cls in JudgeTest.__subclasses__():
            # Inspect all the entities that are not a JudgeTest.
            if cls is not JudgeTest:
                if hasattr(cls, "__test_ext__"):
                    found_languages.add(cls.__test_ext__)
                else:
                    self.fail("Class {} must have a field __test_ext__ that "
                              "corresponds to the file extension under test."
                              .format(cls.__name__))
        self.assertEqual(set(judge.ALLOWED_EXTENSIONS), found_languages)


if __name__ == '__main__':
    unittest.main()
