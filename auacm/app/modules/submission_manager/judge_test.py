'''Tests for the judge.

To run these tests, execute the following command on the app level:

<code>
flask/bin/python -m unittest discover -p "*_test.py" 
</code>

Interesting fact, Python unit tests are supposed to use camelCase instead of
snake_case.
'''
import glob
import os
import unittest

from os import path

from app import app
from app.modules.submission_manager import judge
from app.modules.submission_manager import models


class JudgeTest(object):
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
    '''Tests for java submissions.'''

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
        pass
    
    def testWrongAnswer(self):
        pass
    
    def testRightAnswer(self):
        pass


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


if __name__ == "__main__":
    unittest.main()
