from flask import request
from flask_restful import Resource
import models
import os

class Problem(Resource):

    def get(self, id):
        """ get returns a problem with the following format:
            \n<code>
            {
                'status': 200,
                'data':{
                    'id': '1',
                    'name': 'N Days of Christmas',
                    'contest': '2013 Southeast Regional',
                    'difficulty': 'medium'
                    'description_url': 'auacm.com/problems/001.pdf'
                }
            }</code>
        """
        try:
            problem = models.Problem.load(id)
        except RequestParseError as rpe:
            return {'status': 400, 'error': rpe.message}, 400
        except ProblemNotFoundError as pnfe:
            return {'status': 404, 'error': pnfe.message}, 404
        return 
            {'status': 200, 'data': { 
                'id': problem.id,
                'name': problem.name,
                'contest': problem.contest,
                'difficulty': problem.difficulty,
                'description_url': self.url(id) 
                }
            }, 200

    def delete(self):
        """ delete removes a problem from the database of problems.
        The response will be 
        <code>
        {
            status: 200,
            success: true
        }
        """
        try:
            problem = models.Problem.load(id)
        except ProblemNotFoundError as pnfe:
            return {'status': 404, 'error': pnfe.message}, 404
        if problem.delete():
            return {'status': 200, 'success': True}, 200
        else:
            return {'status': 400, 'success': False}, 400

    @classmethod
    def url(cls, id):
        return os.path.join('localhost:5000/problem', id, '.pdf')

class ProblemCollection(Resource):

    def get(self):
        """ get returns a list of problems with the following format:
            \n<code>
            {
                'status': 200,
                'data':[
                    'auacm.com/problem/1',
                    'auacm.com/problem/2'
                    ...
                ]
            }</code>
        """

    def post(self):
        """ post allows the posting of a new problem. The body should be formatted
        as follows:
        <code>
        {
            'name': 'New Problem',
            'contest': 'Contest name',
            'difficulty': 'medium'
        }
        </code>
        
        It returns a <code>Problem</code>
        """


