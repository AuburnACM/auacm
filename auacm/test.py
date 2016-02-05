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
import sqlalchemy
from sys import argv


# Get a KeyError after custom tests without this. Full explanaition on SO
# https://stackoverflow.com/questions/8774958/keyerror-in-module-threading-after-a-successful-py-test-run
from gevent import monkey; monkey.patch_thread()

import app
import app.database as db

# Switch the database session variable to point to the test database
test_engine = sqlalchemy.create_engine(
    'mysql+pymysql://root@localhost/acm_test?charset=utf8')
test_conn = test_engine.connect()
db.session = sqlalchemy.orm.Session(test_engine)


if __name__ == "__main__":
    loader = unittest.defaultTestLoader
    if len(argv) > 1:
        print('Using custom tests')
        for arg in argv[1:]:
            pattern = arg + '_test.py'
            print('Testing with ' + pattern)
            suite = loader.discover(start_dir='.', pattern=pattern)
            unittest.TextTestRunner().run(suite)
    else:
        suite = loader.discover(start_dir='.', pattern='*_test.py')
        unittest.TextTestRunner().run(suite)
