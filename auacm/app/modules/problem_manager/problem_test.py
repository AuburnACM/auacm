"""Tests for problem CRUD-ing

To run these tests, execute the following at the app level:

<code>
./test.py problem
</code>

These tests assume that the login functionality works, and depends on having
access to the test database.

Interesting fact, Benjamin Franklin was the oldest signer of the Declaration
of Independence at 70 years old.
"""

import unittest
import json
import copy
from time import time

from ...modules import TEST_APP
from ..problem_manager.models import Problem, ProblemData, SampleCase
from ..competition_manager.models import Competition
from ...util import AUACMTest
from ...database import DATABASE_SESSION

# TODO: Test invalid requests to problem(s)
# TODO: Problem subtests (different arguments, etc)

# Test data to work with
TEST_PROBLEM = {
    'pid': 9999,
    'name': 'This is a test',
    'shortname': 'thisisatest',
    'appeared': 'Not a real Competition',
    'difficulty': 100,
    'added': 1,
    'comp_release': 1
}
TEST_PROBLEM_DATA = {
    'pid': 9999,
    'description': 'This is a test problem. There is no need for alarm.',
    'input_desc': 'Some values and stuff',
    'output_desc': 'Other things. Like the right answer.',
    'time_limit': 90
}
TEST_CASES = [{
    'pid': 9999,
    'case_num': 1,
    'input': '1 1',
    'output': '2'
}, {
    'pid': 9999,
    'case_num': 2,
    'input': '2 2',
    'output': '4'
}]


def _reinsert_test_problem(test_prob, test_prob_data):
    # Find the problem
    cases = DATABASE_SESSION.query(SampleCase).\
        filter(SampleCase.pid == TEST_PROBLEM['pid']).all()
    data = DATABASE_SESSION.query(ProblemData).\
        filter(ProblemData.pid == TEST_PROBLEM['pid']).first()
    problem = DATABASE_SESSION.query(Problem).\
        filter(Problem.pid == TEST_PROBLEM['pid']).first()

    # Remove it (if it was actually in there)
    for case in cases:
        DATABASE_SESSION.delete(case)
    if data:
        DATABASE_SESSION.delete(data)
    if problem:
        DATABASE_SESSION.delete(problem)
    DATABASE_SESSION.commit()

    # Put in the fresh test problem
    DATABASE_SESSION.add(test_prob)
    DATABASE_SESSION.add(test_prob_data)
    for test in TEST_CASES:
        DATABASE_SESSION.add(test)
    DATABASE_SESSION.commit()

class ProblemGetTests(AUACMTest):
    """Tests functionality for GET-ing problems from the API"""

    def setUp(self):
        """Manually add a problem to the test database"""
        self.problem = Problem(**TEST_PROBLEM)
        self.problem_data = ProblemData(**TEST_PROBLEM_DATA)
        self.cases = list()
        for case in TEST_CASES:
            self.cases.append(SampleCase(**case))

        # Ship it off to the db
        try:
            DATABASE_SESSION.add(self.problem)
            DATABASE_SESSION.add(self.problem_data)
            for case in self.cases:
                DATABASE_SESSION.add(case)
            DATABASE_SESSION.commit()
        except:
            DATABASE_SESSION.rollback()
            _reinsert_test_problem(self.problem, self.problem_data)

        # Log in
        self.login()

    def tearDown(self):
        """Manually remove test problem from the test database"""
        for case in self.cases:
            DATABASE_SESSION.delete(case)
        DATABASE_SESSION.delete(self.problem_data)
        DATABASE_SESSION.delete(self.problem)
        DATABASE_SESSION.commit()

        # Log out of this DATABASE_SESSION too
        self.logout()

    def test_get_all(self):
        """Should get basic info about all the problems"""
        # Check that the request went through
        resp = TEST_APP.get('/api/problems')
        self.assertEqual(200, resp.status_code)
        response_data = json.loads(resp.data.decode())
        self.assertFalse('Please log in' in str(response_data))

        # Find the test problem in the list returned
        found = None
        for prob in response_data['data']:
            if prob['pid'] == TEST_PROBLEM['pid']:
                found = prob

        self.assertTrue(len(response_data['data']) > 0)
        self.assertFalse(found is None)

        # All the original values should be maintained
        for k in TEST_PROBLEM:
            self.assertEqual(str(TEST_PROBLEM[k]), str(found[k]))

    def test_get_one(self):
        """Should get detailed info about one specific problem"""
        resp = TEST_APP.get('/api/problems/' + str(TEST_PROBLEM['pid']))
        self.assertEqual(200, resp.status_code)

        response_data = json.loads(resp.data.decode())
        self.assertFalse('Please log in' in str(response_data))

        # Time limit doesn't get returned from the API, so take it out of our
        # validation
        data_validate = copy.deepcopy(TEST_PROBLEM_DATA)
        data_validate.pop('time_limit')

        prob = response_data['data']
        for key in TEST_PROBLEM:
            self.assertEqual(str(TEST_PROBLEM[key]), str(prob[key]))
        for key in data_validate:
            self.assertEqual(str(TEST_PROBLEM_DATA[key]), str(prob[key]))

    def test_hide_unreleased_problem(self):
        """Test that GET-ting an unreleased problem returns a 404"""
        unreleased_cid = self._setUpUnreleasedComp()

        resp = TEST_APP.get('/api/problems/{}'.format(self.problem.pid))
        self.assertEqual(404, resp.status_code)

        self._tearDownComp(unreleased_cid)

    def test_hide_unreleased_problems(self):
        """
        Test that GET-ting all problems doesn't return unreleased problems
        """
        unreleased_cid = self._setUpUnreleasedComp()

        resp = TEST_APP.get('/api/problems')
        self.assertEqual(200, resp.status_code)
        response_data = json.loads(resp.data.decode())['data']

        # Test problem should be hidden
        for prob in response_data:
            self.assertNotEqual(self.problem.pid, prob['pid'])

        self._tearDownComp(unreleased_cid)

    def _set_up_unreleased_comp(self):
        """
        Creates an unreleased competition and associates the test's problem with it

        :return: the cid of the new competition
        """
        unreleased_cid = Competition(
            name='Test Competition',
            start=int(time() + 10000),
            stop=int(time() + 20000),
            closed=0
        ).commit_to_session(DATABASE_SESSION)

        self.problem.comp_release = unreleased_cid
        self.problem.commit_to_session(DATABASE_SESSION)

        return unreleased_cid

    def _tear_down_comp(self, cid):
        """Removes a competition from the database by its cid"""
        DATABASE_SESSION.delete(
            DATABASE_SESSION.query(Competition)
            .filter_by(cid=cid)
            .first()
        )
        DATABASE_SESSION.commit()


class ProblemEditTests(AUACMTest):
    """Tests functionality for editing an existing problem"""

    def setUp(self):
        """Prepare test database for tests"""
        self.problem = Problem(**TEST_PROBLEM)
        self.problem_data = ProblemData(**TEST_PROBLEM_DATA)
        self.cases = list()
        for case in TEST_CASES:
            self.cases.append(SampleCase(**case))

        # Ship it off to the db
        try:
            DATABASE_SESSION.add(self.problem)
            DATABASE_SESSION.add(self.problem_data)
            for case in self.cases:
                DATABASE_SESSION.add(case)
            DATABASE_SESSION.commit()
        except:
            DATABASE_SESSION.rollback()
            _reinsert_test_problem(self.problem, self.problem_data)

        # Log in
        self.login()

    def test_problem_edit(self):
        """Tests to ensure problem information can be updated."""
        new_name = 'A Different Test Problem'
        resp = TEST_APP.put(
            '/api/problems/' + str(TEST_PROBLEM['pid']),
            data={'name':new_name})
        self.assertEqual(200, resp.status_code)

        response_data = json.loads(resp.data.decode())
        prob = response_data['data']
        self.assertEqual(new_name, prob['name'])

    def tearDown(self):
        """Tie up loose ends from test"""
        for case in self.cases:
            DATABASE_SESSION.delete(case)
        DATABASE_SESSION.delete(self.problem_data)
        DATABASE_SESSION.delete(self.problem)
        DATABASE_SESSION.commit()

        # Log out of this DATABASE_SESSION too
        self.logout()


class ProblemDeleteTests(AUACMTest):
    """Tests deleting a problem via the API"""

    def setUp(self):
        """Add the problem to be deleted to the database"""
        self.problem = Problem(**TEST_PROBLEM)
        DATABASE_SESSION.add(self.problem)
        self.problem_data = ProblemData(**TEST_PROBLEM_DATA)
        DATABASE_SESSION.add(self.problem_data)
        self.cases = list()
        for case in TEST_CASES:
            self.cases.append(SampleCase(**case))
            DATABASE_SESSION.add(self.cases[len(self.cases)-1])

        try:
            DATABASE_SESSION.commit()
        except:
            DATABASE_SESSION.rollback()
            _reinsert_test_problem(self.problem, self.problem_data)

        # Log in as well
        self.login()

    def test_delete_problem(self):
        """Tests to ensure a problem can be deleted."""
        resp = TEST_APP.delete('api/problems/' + str(TEST_PROBLEM['pid']))
        self.assertEqual(200, resp.status_code)
        response_data = json.loads(resp.data.decode())
        self.assertEqual(str(TEST_PROBLEM['pid']), (response_data['data']['deleted_pid']))

        # Ensure problem was removed from the database
        prob = DATABASE_SESSION.query(Problem).\
            filter(Problem.pid == TEST_PROBLEM['pid']).first()
        self.assertIsNone(prob)

    def tearDown(self):
        """Delete the test problem only if unsuccessful"""
        prob = DATABASE_SESSION.query(Problem).\
            filter(Problem.pid == TEST_PROBLEM['pid']).first()
        if prob is not None:
            prob_data = DATABASE_SESSION.query(ProblemData).\
                filter(ProblemData.pid == TEST_PROBLEM['pid']).first()
            cases = DATABASE_SESSION.query(SampleCase).\
                filter(SampleCase.pid == TEST_PROBLEM['pid']).all()
            DATABASE_SESSION.delete(prob_data)
            for case in cases:
                DATABASE_SESSION.delete(case)
            DATABASE_SESSION.delete(prob)
            DATABASE_SESSION.commit()

        # Log out as well
        self.logout()

if __name__ == '__main__':
    unittest.main()
