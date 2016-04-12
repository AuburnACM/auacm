"""Tests for AUACM competition manager"""

import unittest, json

from app import app, test_app
from app.util import AUACMTest
from app.modules.competition_manager.models import Competition, CompProblem, CompUser

import app.database as database
session = database.session


class AUACMCompetitionTests(AUACMTest):
    """Test cases for the AUACM competition manager"""

    def testCreate(self):
        """Test creating a new competition"""
        pass

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
