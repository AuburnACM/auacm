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

    def testCreateComp(self):
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

        comp_problems = session.query(CompProblem).filter_by(cid=cid).all()
        for comp_problem in comp_problems:
            session.delete(comp_problem)
        session.delete(comp)
        session.commit()

    def testGetOneComp(self):
        """Test retrieving a single competition by id"""
        competition = self._insert_comp_into_db()[0]

        response = test_app.get('/api/competitions/{}'.format(competition.cid))
        self.assertIsNotNone(response)
        self.assertEqual(200, response.status_code)
        response_body = json.loads(response.data.decode())['data']

        print(json.dumps(response_body, indent=4))
        self.assertEqual(competition.cid, response_body['competiton']['cid'])

        session.delete(competition)
        session.commit()

    def testGetAllComp(self):
        """Test retrieving all competitions"""
        pass

    def testEditComp(self):
        """Test modifying a competition"""
        pass

    def testDeleteComp(self):
        """Test deleting a competition"""
        pass

    def testGetCompTeams(self):
        """Test retreiving the teams for a competition"""
        pass

    def testEditCompTeams(self):
        """Test editing the teams for a competition"""
        pass

    def _insert_comp_into_db(self, num=1):
        """
        Inserts a number of competitions into the database. The problems of
        each competition are inserted as well, but those objects are not
        returned.

        :param num: the number of competitions to inser
        :returns: a list of the new competition ORM objects
        """
        competitions = list()
        for i in range(num):
            competition = Competition(
                name='Test Competition {}'.format(i),
                start=int(time.time()),
                stop=int(time.time()) + 10,
                closed=0
            )
            cid = competition.commit_to_session(session)
            session.add(
                CompProblem(
                    label='A',
                    cid=cid,
                    pid=1
                )
            )
            competitions.append(competition)
        session.commit()

        return competitions

