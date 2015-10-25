'''Tests for the judge.

To run these tests, execute the following command on the app level:

./test.py

Interesting fact, Python unit tests are supposed to use camelCase instead of
snake_case.
'''
import glob
import os
import sys
import unittest

from os import path

from app import app
from app.modules.submission_manager import judge
from app.modules.submission_manager import models


class JudgeTest(object):
    '''Mixin class for building tests of the judge in different langauges.
    
    This makes 6 methods mandatory for implementation.  The goal is to
    make it so that we can't add a language without adding unit tests
    to verify the behavior of submissions in that language.
    '''
    def setUp(self):
        '''Prepare the test directory to run a new submit.
            
        1. Reconfigure app to go to the directory app/test_data
        2. Empty the submits directory
        ''' 
        app.config["DATA_FOLDER"] = os.getcwd() + '/judge_tests'
        directory = path.join(app.config["DATA_FOLDER"], "submits", "*")
        self._purgeDirectory(directory)
        # TODO(djshuckerow): more things to clean the directory up.
        self.submit, self.submit_file = None, None
        
    def _purgeDirectory(self, directory):
        for f in glob.glob(directory):
            if path.isfile(f):
                os.remove(f)
            if path.isdir(f):
                self._purgeDirectory(path.join(f, "*"))
                os.rmdir(f)

    def createMockSubmission(self, problem, filename):
        '''Create a MockSubmission to use for a test run.
        
        Creates two local fields, submit and submit_file containing the 
        submission and code file to run for this submission.
        
        :param problem: the problem this submission is for.
        :param filename: the name of the code file located in 
            app/test_data/<problem>/solutions/ to run. 
        :return: None
        '''
        data_folder = app.config["DATA_FOLDER"]
        name, ext = filename.rsplit(".", 1)
        submit = models.MockSubmission(
            username="Test User",
            pid=problem,
            submit_time=0,
            auto_id=0,
            file_type=ext,
            result="start"
        )
        file_path = path.join(
            data_folder, "problems", problem, "solutions", filename)
        # Keep track of submit_file so that we can close it.
        self.submit = submit
        self.submit_file = MockUploadFile(file_path)
    
    def tearDown(self):
        if self.submit_file:
            self.submit_file.close()
        directory = path.join(app.config["DATA_FOLDER"], "submits", "*")
        self._purgeDirectory(directory)
        
    def testNoCompile(self):
        raise NotImplementedError("Subclasses must implement this test!")

    def testCompile(self):
        raise NotImplementedError("Subclasses must implement this test!")
        
    def testRuntimeError(self):
        raise NotImplementedError("Subclasses must implement this test!")

    def testTimelimitError(self):
        raise NotImplementedError("Subclasses must implement this test!")
        
    def testWrongAnswer(self):
        raise NotImplementedError("Subclasses must implement this test!")

    def testRightAnswer(self):
        raise NotImplementedError("Subclasses must implement this test!")


class JavaTest(JudgeTest, unittest.TestCase):
    '''Tests for .java submissions.'''

    def testNoCompile(self):
        self.createMockSubmission("addnumbers", "CompileError.java")
        self.assertEqual(
            judge.COMPILATION_ERROR,
            judge.evaluate(self.submit, self.submit_file))
        
    def testCompile(self):
        self.createMockSubmission("addnumbers", "CompileSuccess.java")
        directory = judge.directory_for_submission(self.submit)
        os.mkdir(directory)
        self.submit_file.save(path.join(directory, self.submit_file.filename))
        self.assertEqual(
            judge.COMPILATION_SUCCESS,
            judge.compile_submission(self.submit, self.submit_file))

    def testRuntimeError(self):
        self.createMockSubmission("addnumbers", "RuntimeError.java")
        self.assertEqual(
            judge.RUNTIME_ERROR,
            judge.evaluate(self.submit, self.submit_file))
        
    def testTimelimitError(self):
        self.createMockSubmission("addnumbers", "TimelimitError.java")
        self.assertEqual(
            judge.TIMELIMIT_EXCEEDED,
            judge.evaluate(self.submit, self.submit_file))
    
    def testWrongAnswer(self):
        self.createMockSubmission("addnumbers", "WrongAnswer.java")
        self.assertEqual(
            judge.WRONG_ANSWER,
            judge.evaluate(self.submit, self.submit_file))
    
    def testRightAnswer(self):
        self.createMockSubmission("addnumbers", "RightAnswer.java")
        self.assertEqual(
            judge.CORRECT_ANSWER,
            judge.evaluate(self.submit, self.submit_file))


class PythonTest(JudgeTest, unittest.TestCase):
    '''Tests for .py submissions.'''
    #TODO(djshuckerow): tests for python2 and python3.

    def testNoCompile(self):
        pass

    def testCompile(self):
        pass

    def testRuntimeError(self):
        self.createMockSubmission("addnumbers", "runtime.py")
        self.assertEqual(
            judge.RUNTIME_ERROR,
            judge.evaluate(self.submit, self.submit_file))
        
    def testTimelimitError(self):
        self.createMockSubmission("addnumbers", "timelimit.py")
        self.assertEqual(
            judge.TIMELIMIT_EXCEEDED,
            judge.evaluate(self.submit, self.submit_file))
    
    def testWrongAnswer(self):
        self.createMockSubmission("addnumbers", "wrong.py")
        self.assertEqual(
            judge.WRONG_ANSWER,
            judge.evaluate(self.submit, self.submit_file))
    
    def testRightAnswer(self):
        self.createMockSubmission("addnumbers", "right.py")
        self.assertEqual(
            judge.CORRECT_ANSWER,
            judge.evaluate(self.submit, self.submit_file))


class CTest(JudgeTest, unittest.TestCase):
    '''Tests for .c submissions.'''

    def testNoCompile(self):
        self.createMockSubmission("addnumbers", "compileerror.c")
        self.assertEqual(
            judge.COMPILATION_ERROR,
            judge.evaluate(self.submit, self.submit_file))

    def testCompile(self):
        self.createMockSubmission("addnumbers", "compilesuccess.c")
        directory = judge.directory_for_submission(self.submit)
        os.mkdir(directory)
        self.submit_file.save(path.join(directory, self.submit_file.filename))
        self.assertEqual(
            judge.COMPILATION_SUCCESS,
            judge.compile_submission(self.submit, self.submit_file))

    def testRuntimeError(self):
        self.createMockSubmission("addnumbers", "runtimeerror.c")
        self.assertEqual(
            judge.RUNTIME_ERROR,
            judge.evaluate(self.submit, self.submit_file))

    def testTimelimitError(self):
        self.createMockSubmission("addnumbers", "timelimiterror.c")
        self.assertEqual(
            judge.TIMELIMIT_EXCEEDED,
            judge.evaluate(self.submit, self.submit_file))

    def testWrongAnswer(self):
        self.createMockSubmission("addnumbers", "wronganswer.c")
        self.assertEqual(
            judge.WRONG_ANSWER,
            judge.evaluate(self.submit, self.submit_file))

    def testRightAnswer(self):
        self.createMockSubmission("addnumbers", "rightanswer.c")
        self.assertEqual(
            judge.CORRECT_ANSWER,
            judge.evaluate(self.submit, self.submit_file))
            

class CppTest(JudgeTest, unittest.TestCase):
    '''Tests for .cpp submissions.'''

    def testNoCompile(self):
        self.createMockSubmission("addnumbers", "compileerror.cpp")
        self.assertEqual(
            judge.COMPILATION_ERROR,
            judge.evaluate(self.submit, self.submit_file))

    def testCompile(self):
        self.createMockSubmission("addnumbers", "compilesuccess.cpp")
        directory = judge.directory_for_submission(self.submit)
        os.mkdir(directory)
        self.submit_file.save(path.join(directory, self.submit_file.filename))
        self.assertEqual(
            judge.COMPILATION_SUCCESS,
            judge.compile_submission(self.submit, self.submit_file))

    def testRuntimeError(self):
        self.createMockSubmission("addnumbers", "runtimeerror.cpp")
        self.assertEqual(
            judge.RUNTIME_ERROR,
            judge.evaluate(self.submit, self.submit_file))

    def testTimelimitError(self):
        self.createMockSubmission("addnumbers", "timelimiterror.cpp")
        self.assertEqual(
            judge.TIMELIMIT_EXCEEDED,
            judge.evaluate(self.submit, self.submit_file))

    def testWrongAnswer(self):
        self.createMockSubmission("addnumbers", "wronganswer.cpp")
        self.assertEqual(
            judge.WRONG_ANSWER,
            judge.evaluate(self.submit, self.submit_file))

    def testRightAnswer(self):
        self.createMockSubmission("addnumbers", "rightanswer.cpp")
        self.assertEqual(
            judge.CORRECT_ANSWER,
            judge.evaluate(self.submit, self.submit_file))


class GoTest(JudgeTest, unittest.TestCase):
    '''Tests for .go submissions.'''

    def testNoCompile(self):
        self.createMockSubmission("addnumbers", "compileerror.go")
        self.assertEqual(
            judge.COMPILATION_ERROR,
            judge.evaluate(self.submit, self.submit_file))

    def testCompile(self):
        self.createMockSubmission("addnumbers", "compilesuccess.go")
        directory = judge.directory_for_submission(self.submit)
        os.mkdir(directory)
        self.submit_file.save(path.join(directory, self.submit_file.filename))
        self.assertEqual(
            judge.COMPILATION_SUCCESS,
            judge.compile_submission(self.submit, self.submit_file))

    def testRuntimeError(self):
        self.createMockSubmission("addnumbers", "runtimeerror.go")
        self.assertEqual(
            judge.RUNTIME_ERROR,
            judge.evaluate(self.submit, self.submit_file))

    def testTimelimitError(self):
        self.createMockSubmission("addnumbers", "timelimiterror.go")
        self.assertEqual(
            judge.TIMELIMIT_EXCEEDED,
            judge.evaluate(self.submit, self.submit_file))

    def testWrongAnswer(self):
        self.createMockSubmission("addnumbers", "wronganswer.go")
        self.assertEqual(
            judge.WRONG_ANSWER,
            judge.evaluate(self.submit, self.submit_file))

    def testRightAnswer(self):
        self.createMockSubmission("addnumbers", "rightanswer.go")
        self.assertEqual(
            judge.CORRECT_ANSWER,
            judge.evaluate(self.submit, self.submit_file))


class MockUploadFile(object):
    '''Mock upload file that has a save method.'''
    def __init__(self, location):
        self.f = open(location)
        self.filename = path.split(location)[1]

    def save(self, location):
        with open(location, "w") as to_save:
            to_save.write(self.f.read())
    
    def close(self):
        self.f.close()

#TODO(djshuckerow): make sure it's impossible to add a language without tests.


if __name__ == "__main__":
    unittest.main()
