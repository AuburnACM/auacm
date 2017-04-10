'''Tests for AUACM competition manager'''

import json
import time

from app.modules import test_app
from app.util import AUACMTest
from app.modules.competition_manager.models import Competition, CompProblem, CompUser

from ...database import DATABASE_SESSION

class AUACMCompetitionTests(AUACMTest):
    '''Test cases for the AUACM competition manager'''

    def test_create_comp(self):
        '''Test creating a new competition'''
        self.login()
        post_form = {
            'name': 'Test Competition',
            'start_time': int(time.time()),
            'problems': '[{"label": "A", "pid": 1}]',
            'closed': False,
            'length': 10,
        }

        response = json.loads(test_app.post('/api/competitions',\
                              data=post_form).data.decode())

        self.assertEqual(200, response['status'])

        cid = response['data']['cid']
        comp = (DATABASE_SESSION.query(Competition)
                .filter_by(cid=cid).
                first()
               )
        self.assertIsNotNone(comp)

        comp_problems = DATABASE_SESSION.query(CompProblem).filter_by(cid=cid).all()
        for comp_problem in comp_problems:
            DATABASE_SESSION.delete(comp_problem)
        DATABASE_SESSION.delete(comp)
        DATABASE_SESSION.commit()

    def test_get_one_comp(self):
        '''Test retrieving a single competition by id'''
        competition = self._insert_comp_into_db()[0]

        response = test_app.get('/api/competitions/{}'.format(competition.cid))
        self.assertIsNotNone(response)
        self.assertEqual(200, response.status_code)
        response_body = json.loads(response.data.decode())['data']

        self.assertEqual(competition.cid, response_body['competition']['cid'])
        self.assertIn('compProblems', response_body)
        self.assertIn('competition', response_body)
        self.assertIn('teams', response_body)
        self.assertEqual(competition.to_dict(), response_body['competition'])

        DATABASE_SESSION.delete(competition)
        DATABASE_SESSION.commit()

    def test_get_all_comp(self):
        '''Test retrieving all competitions'''
        competitions = self._insert_comp_into_db(3)

        response = test_app.get('/api/competitions')
        response_body = json.loads(response.data.decode())['data']

        self.assertEqual(200, response.status_code)
        all_comps = (response_body['ongoing'] + response_body['upcoming'] +
                     response_body['past'])
        for competition in competitions:
            self.assertIn(
                competition.to_dict(),
                all_comps
            )

        for competition in competitions:
            DATABASE_SESSION.delete(competition)
        DATABASE_SESSION.commit()

    def test_edit_comp(self):
        '''Test modifying a competition'''
        self.login()
        competition = self._insert_comp_into_db()[0]
        competition_data = {
            'name':  'Different Name',
            'start_time': int(time.time()),
            'length': 100,
            'closed': False,
            'problems': '[{"label": "A", "pid": 1}]',
        }

        response = test_app.put('/api/competitions/{}'.format(competition.cid),
                                data=competition_data)

        self.assertEqual(200, response.status_code)
        response_data = json.loads(response.data.decode())['data']
        self.assertEqual(competition_data['name'], response_data['name'])
        self.assertEqual(competition_data['start_time'],
                         response_data['startTime'])
        self.assertEqual(competition_data['length'], response_data['length'])

        DATABASE_SESSION.delete(competition)
        DATABASE_SESSION.commit()

    def test_delete_comp(self):
        '''Test deleting a competition'''
        self.login()
        competition = self._insert_comp_into_db()[0]
        cid = competition.cid
        DATABASE_SESSION.expunge(competition)

        response = test_app.delete('/api/competitions/{}'.format(cid))
        self.assertEqual(204, response.status_code)
        self.assertIsNone(DATABASE_SESSION.query(Competition)
                          .filter_by(cid=cid).first())


    def test_get_comp_teams(self):
        '''Test retreiving the teams for a competition'''
        competition = self._insert_comp_into_db()[0]
        cid = competition.cid
        user = CompUser(cid=cid, username=self.username, team='Team Test')
        user.commit_to_session(DATABASE_SESSION)

        response = test_app.get('/api/competitions/{}/teams'.format(cid))
        self.assertEqual(200, response.status_code)
        response_data = json.loads(response.data.decode())['data']

        self.assertIn('Team Test', response_data)
        self.assertEqual(
            self.username,
            response_data['Team Test'][0]['username']
        )

        DATABASE_SESSION.delete(user)
        DATABASE_SESSION.delete(competition)
        DATABASE_SESSION.commit()

    def test_edit_comp_teams(self):
        '''Test editing the teams for a competition'''
        self.login()
        teams = {
            'teams': json.dumps({
                'Team Test Edited': [self.username]
            })
        }
        competition = self._insert_comp_into_db()[0]
        cid = competition.cid
        DATABASE_SESSION.add(
            CompUser(
                cid=cid,
                username=self.username,
                team='Team Test'
            )
        )
        DATABASE_SESSION.expunge(competition)
        DATABASE_SESSION.commit()

        response = test_app.put('/api/competitions/{}/teams'.format(cid),
                                data=teams)
        self.assertEqual(200, response.status_code)
        response_body = json.loads(response.data.decode())['data']

        self.assertEqual({}, response_body)
        new_team = DATABASE_SESSION.query(CompUser).filter_by(cid=cid).first()
        self.assertEqual('Team Test Edited', new_team.team)
        self.assertEqual(self.username, new_team.username)

        DATABASE_SESSION.delete(DATABASE_SESSION.query(CompUser).filter_by(cid=cid).first())
        DATABASE_SESSION.delete(DATABASE_SESSION.query(Competition).filter_by(cid=cid).first())
        DATABASE_SESSION.commit()

    def _insert_comp_into_db(self, num=1):
        '''
        Inserts a number of competitions into the database. The problems of
        each competition are inserted as well, but those objects are not
        returned.

        :param num: the number of competitions to inser
        :returns: a list of the new competition ORM objects
        '''
        competitions = list()
        for i in range(num):
            competition = Competition(
                name='Test Competition {}'.format(i),
                start=int(time.time()),
                stop=int(time.time()) + 10,
                closed=0
            )
            cid = competition.commit_to_session(DATABASE_SESSION)
            DATABASE_SESSION.add(
                CompProblem(
                    label='A',
                    cid=cid,
                    pid=1
                )
            )
            competitions.append(competition)
        DATABASE_SESSION.commit()

        return competitions
