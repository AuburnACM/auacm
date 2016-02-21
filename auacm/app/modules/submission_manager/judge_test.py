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

from app import app
from app.modules.submission_manager import judge
from app.modules.submission_manager import models


class JudgeTest(object):
    """Mixin class for building tests of the judge in different langauges.

    This makes 6 methods mandatory for implementation.  The goal is to
    make it so that we can't add a language without adding unit tests
    to verify the behavior of submissions in that language.
    """

    def setUp(self):
        """Prepare the test directory to run a new submit.

        1. Reconfigure app to go to the directory app/test_data
        2. Empty the submits directory
        """
        app.config['DATA_FOLDER'] = os.getcwd() + '/judge_tests'
        directory = os.path.join(app.config['DATA_FOLDER'], 'submits', '*')
        self._purgeDirectory(directory)
        self.submit, self.submit_file = None, None

    def _purgeDirectory(self, directory):
        """Recursively empty out the contents of a directory. USE WITH CAUTION.

        This is currently used to empty out the judge_tests/submits directory
        before and after tests are run.
        """
        for f in glob.glob(directory):
            if os.path.isfile(f):
                os.remove(f)
            if os.path.isdir(f):
                self._purgeDirectory(os.path.join(f, '*'))
                os.rmdir(f)

    def createMockSubmission(self, problem, filename):
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
        submit_directory = os.path.join(app.config['DATA_FOLDER'], 'submits', str(submit.job))
        if not os.path.exists(submit_directory):
            os.mkdir(submit_directory)
        # Keep track of submit_file so that we can close it.
        self.submit = submit
        self.submit_file = MockUploadFile(file_path)
        self.judge = judge.Judge(self.submit, self.submit_file, 1)

    def assertEvaluation(self, expected_result):
        """Assert the behavior of a submission.

        A MockSubmission must first be created with createMockSubmission.

        :param expected_result: the expected result for this submission.
            Normally one of the constants exposed by the judge module.
        :return: None
        """
        directory = self.judge.directory_for_submission
        self.submit_file.save(
                os.path.join(directory, self.submit_file.filename))
        self.assertEqual(
            expected_result,
            self.judge.run())

    def assertCompilation(self, expected_result):
        """Assert the behavior of a compilation.

        A MockSubmission must first be created with createMockSubmission.

        :param expected_result: the expected result for this submission.
            Normally one of the constants exposed by the judge module.
        :return: None
        """
        directory = self.judge.directory_for_submission
        self.submit_file.save(
            os.path.join(directory, self.submit_file.filename))
        self.assertEqual(
            expected_result,
            self.judge.compile_submission())

    def tearDown(self):
        """Post-test cleanup method.

        Close our submit_file so that we don't waste resources waiting on
        the garbage collector.

        Purge the judge_tests/submits directory post-test.
        """
        if self.submit_file:
            self.submit_file.close()
        directory = os.path.join(app.config['DATA_FOLDER'], 'submits', '*')
        self._purgeDirectory(directory)

    def testNoCompile(self):
        raise NotImplementedError('Subclasses must implement this test!')

    def testCompile(self):
        raise NotImplementedError('Subclasses must implement this test!')

    def testRuntimeError(self):
        raise NotImplementedError('Subclasses must implement this test!')

    def testTimelimitError(self):
        raise NotImplementedError('Subclasses must implement this test!')

    def testWrongAnswer(self):
        raise NotImplementedError('Subclasses must implement this test!')

    def testRightAnswer(self):
        raise NotImplementedError('Subclasses must implement this test!')


class JavaTest(JudgeTest, unittest.TestCase):
    """Tests for .java submissions."""

    __test_ext__ = "java"

    def testNoCompile(self):
        self.createMockSubmission('addnumbers', 'CompileError.java')
        self.assertEvaluation(judge.COMPILATION_ERROR)

    def testCompile(self):
        self.createMockSubmission('addnumbers', 'CompileSuccess.java')
        self.assertCompilation(judge.COMPILATION_SUCCESS)

    def testRuntimeError(self):
        self.createMockSubmission('addnumbers', 'RuntimeError.java')
        self.assertEvaluation(judge.RUNTIME_ERROR)

    def testTimelimitError(self):
        self.createMockSubmission('addnumbers', 'TimelimitError.java')
        self.assertEvaluation(judge.TIMELIMIT_EXCEEDED)

    def testWrongAnswer(self):
        self.createMockSubmission('addnumbers', 'WrongAnswer.java')
        self.assertEvaluation(judge.WRONG_ANSWER)

    def testRightAnswer(self):
        self.createMockSubmission('addnumbers', 'RightAnswer.java')
        self.assertEvaluation(judge.CORRECT_ANSWER)


class PythonTest(JudgeTest, unittest.TestCase):
    """Tests for .py submissions."""
    # TODO(djshuckerow): tests for python2 and python3.

    __test_ext__ = "py"

    def testNoCompile(self):
        pass

    def testCompile(self):
        pass

    def testRuntimeError(self):
        self.createMockSubmission('addnumbers', 'runtime.py')
        self.assertEvaluation(judge.RUNTIME_ERROR)

    def testTimelimitError(self):
        self.createMockSubmission('addnumbers', 'timelimit.py')
        self.assertEvaluation(judge.TIMELIMIT_EXCEEDED)

    def testWrongAnswer(self):
        self.createMockSubmission('addnumbers', 'wrong.py')
        self.assertEvaluation(judge.WRONG_ANSWER)

    def testRightAnswer(self):
        self.createMockSubmission('addnumbers', 'right.py')
        self.assertEvaluation(judge.CORRECT_ANSWER)


class CTest(JudgeTest, unittest.TestCase):
    """Tests for .c submissions."""

    __test_ext__ = "c"

    def testNoCompile(self):
        self.createMockSubmission('addnumbers', 'compileerror.c')
        self.assertEvaluation(judge.COMPILATION_ERROR)

    def testCompile(self):
        self.createMockSubmission('addnumbers', 'compilesuccess.c')
        self.assertCompilation(judge.COMPILATION_SUCCESS)

    def testRuntimeError(self):
        self.createMockSubmission('addnumbers', 'runtimeerror.c')
        self.assertEvaluation(judge.RUNTIME_ERROR)

    def testTimelimitError(self):
        self.createMockSubmission('addnumbers', 'timelimiterror.c')
        self.assertEvaluation(judge.TIMELIMIT_EXCEEDED)

    def testWrongAnswer(self):
        self.createMockSubmission('addnumbers', 'wronganswer.c')
        self.assertEvaluation(judge.WRONG_ANSWER)

    def testRightAnswer(self):
        self.createMockSubmission('addnumbers', 'rightanswer.c')
        self.assertEvaluation(judge.CORRECT_ANSWER)


class CppTest(JudgeTest, unittest.TestCase):
    """Tests for .cpp submissions."""

    __test_ext__ = "cpp"

    def testNoCompile(self):
        self.createMockSubmission('addnumbers', 'compileerror.cpp')
        self.assertEqual(
            judge.COMPILATION_ERROR,
            self.judge.run())

    def testCompile(self):
        self.createMockSubmission('addnumbers', 'compilesuccess.cpp')
        self.assertCompilation(judge.COMPILATION_SUCCESS)

    def testRuntimeError(self):
        self.createMockSubmission('addnumbers', 'runtimeerror.cpp')
        self.assertEvaluation(judge.RUNTIME_ERROR)

    def testTimelimitError(self):
        self.createMockSubmission('addnumbers', 'timelimiterror.cpp')
        self.assertEvaluation(judge.TIMELIMIT_EXCEEDED)

    def testWrongAnswer(self):
        self.createMockSubmission('addnumbers', 'wronganswer.cpp')
        self.assertEvaluation(judge.WRONG_ANSWER)

    def testRightAnswer(self):
        self.createMockSubmission('addnumbers', 'rightanswer.cpp')
        self.assertEvaluation(judge.CORRECT_ANSWER)


class GoTest(JudgeTest, unittest.TestCase):
    """Tests for .go submissions."""

    __test_ext__ = "go"

    def testNoCompile(self):
        self.createMockSubmission('addnumbers', 'compileerror.go')
        self.assertEvaluation(judge.COMPILATION_ERROR)

    def testCompile(self):
        self.createMockSubmission('addnumbers', 'compilesuccess.go')
        self.assertCompilation(judge.COMPILATION_SUCCESS)

    def testRuntimeError(self):
        self.createMockSubmission('addnumbers', 'runtimeerror.go')
        self.assertEvaluation(judge.RUNTIME_ERROR)

    def testTimelimitError(self):
        self.createMockSubmission('addnumbers', 'timelimiterror.go')
        self.assertEvaluation(judge.TIMELIMIT_EXCEEDED)

    def testWrongAnswer(self):
        self.createMockSubmission('addnumbers', 'wronganswer.go')
        self.assertEvaluation(judge.WRONG_ANSWER)

    def testRightAnswer(self):
        self.createMockSubmission('addnumbers', 'rightanswer.go')
        self.assertEvaluation(judge.CORRECT_ANSWER)


class MockUploadFile(object):
    """Mock upload file that has a save method.

    The judge uses the object this mocks out to save the file it receives from
    the judge api call.  The file is then saved to the submission directory so
    that it can be run.
    """

    def __init__(self, location):
        self.f = open(location)
        self.filename = os.path.split(location)[1]

    def save(self, location):
        with open(location, 'w') as to_save:
            to_save.write(self.f.read())

    def close(self):
        self.f.close()


class LanguageTest(unittest.TestCase):
    """Make sure that we have tests for all the languages we support.

    This test uses a reflection pattern to get all our tests and verify that
    a test for each language we support is implemented.
    """

    def testLanguagesAreAllTested(self):
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
