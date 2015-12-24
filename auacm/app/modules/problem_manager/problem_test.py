"""Tests for problem CRUD-ing

To run these tests, execute the following at the app level:

<code>
./test.py
</code>

Interesting fact, Benjamin Franklin was the oldest signer of the Declaration
of Independence at 70 years old.
"""

import unittest
import json

from app import app

# TODO: Test get_problem (detailed info)
# TODO: Test get_problems (all problems)
# TODO: Test create_problem (and all of its subtests)
# TODO: Test delete_problem
# TODO: Test updating a problem

class CommonProblemTest(object):
    def setUp(self):
        # Test app object
        self.app = app.test_client()

        # Sign in as a test user
        data = json.loads(self.app.post('/api/login', data=dict(username='brandonm',
                                              password='password')).data)
        # TODO: Validate the problems somehow
        print(data)

class ProblemGetTests(CommonProblemTest, unittest.TestCase):
    def testSomething(self):
        # with self.app.session_transaction() as sess:
        #     sess['user_id'] = 'test'
        #     sess['is_authenticated'] = True
        # input('Press enter to continue')
        # print(json.loads(self.app.get('/api/problems').data)['data'])
        print(self.app.get('/api/problems').data)

if __name__ == '__main__':
    unittest.main()
