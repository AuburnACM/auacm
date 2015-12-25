#!flask/bin/python
'''Run all tests in the project.

This will take all files that include *_test.py and run them.
'''
import unittest
from app import app
from app import database
# from app.database import session, engine


if __name__ == "__main__":
    loader = unittest.defaultTestLoader
    suite = loader.discover(start_dir=".", pattern="*_test.py")
    unittest.TextTestRunner().run(suite)
