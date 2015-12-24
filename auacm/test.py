#!flask/bin/python
'''Run all tests in the project.

This will take all files that include *_test.py and run them.
'''
import unittest
from app import app
from app import database


if __name__ == "__main__":
    database.use_test_db()
    loader = unittest.defaultTestLoader
    suite = loader.discover(start_dir=".", pattern="*_test.py")
    unittest.TextTestRunner().run(suite)
