'''Tests for the judge.

To run these tests, execute the following command on the app level:

<code>
flask/bin/python -m unittest discover -p "*_test.py" 
</code>
'''
import unittest

from app import app
from app.modules.submission_manager import judge


class JudgeTest(unittest.TestCase):
    def setUp(self):
        unittest.TestCase.setUp(self)
        app.config["DATA_FOLDER"] = "test_data"
    def test1(self):
        self.assertTrue(True, "First test!")
        

suite = unittest.TestLoader().loadTestsFromTestCase(JudgeTest)


if __name__ == "__main__":
    unittest.main()
