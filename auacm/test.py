#!flask/bin/python
'''Run all tests in the project, or specific test modules

Usage:
    To test everything, simply use

    $ ./test.py

    To specify specific modules to test, list the names with spaces between

    $ ./test.py module1 [module2 [...]]
    for example,
    $ ./test.py problem submission

    The modules will be tested individually. The module name needs to
    correspond to the test file name, that is, "problem" corresponds to
    "problem_test.py".

This will take all files that include *_test.py and run them.
'''
import unittest
from sys import argv
import sqlalchemy

import APP.database as db

# Switch the database session variable to point to the test database
TEST_ENGINE = sqlalchemy.create_engine(
    'mysql+pymysql://root@localhost/acm_test?charset=utf8')
TEST_CONN = TEST_ENGINE.connect()
db.session = sqlalchemy.orm.Session(TEST_ENGINE)


if __name__ == "__main__":
    LOADER = unittest.defaultTestLoader
    if len(argv) > 1:
        print('Using custom tests')
        for arg in argv[1:]:
            pattern = arg + '_test.py'
            print('Testing with ' + pattern)
            suite = LOADER.discover(start_dir='.', pattern=pattern)
            unittest.TextTestRunner().run(suite)
    else:
        SUITE = LOADER.discover(start_dir='.', pattern='*_test.py')
        unittest.TextTestRunner().run(suite)
