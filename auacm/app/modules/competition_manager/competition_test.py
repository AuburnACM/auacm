"""Tests for AUACM competition manager"""

import json, time

from app import app, test_app
from app.util import AUACMTest
from app.modules.competition_manager.models import Competition, CompProblem, CompUser

import app.database as database
session = database.session

TEST_COMP = {
    
}

class AUACMCompetitionTests(AUACMTest):
    """Test cases for the AUACM competition manager"""

    def testCreate(self):
        """Test creating a new competition"""
        self.login()
        post_form = {
            'name': 'Test Competition',
            'start_time': int(time.time()),
            'problems': '[{"label": "A", "pid": 1}]',
            'closed': False,
            'length': 10,
        }

        response = json.loads(test_app.post('/api/competitions',
                              data=post_form).data.decode())

        self.assertEqual(200, response['status'])

        cid = response['data']['cid']
        comp = (session.query(Competition)
            .filter_by(cid=cid).
            first()
        )
        self.assertIsNotNone(comp)
        session.delete(comp)
        session.commit()

    def testGetOne(self):
        """Test retrieving a single competition by id"""
        pass

    def testGetAll(self):
        """Test retrieving all competitions"""
        pass

    def testEdit(self):
        """Test modifying a competition"""
        pass

    def testDelete(self):
        """Test deleting a competition"""
        pass

    
