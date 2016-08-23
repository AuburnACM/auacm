#!/bin/sh
if !(which brew 2>/dev/null;) then
    /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
fi

if !(which python3 2>/dev/null;) then
    brew install python3
fi

if !(which pip 2>/dev/null;) then
    curl "https://bootstrap.pypa.io/get-pip.py" | sudo python3
fi

if !(which mysql 2>/dev/null;) then
    brew install mysql
fi

# Change to where the action happens
cd ../auacm

# Install virtualenv (if it doesn't already exist
pip install virtualenv
# Create our virtualenv and install its dependencies
virtualenv -p python3 flask
CFLAGS='-std=c99' ./flask/bin/pip install -r requirements.txt

# Install node to install bower for dependency-management
brew install npm
npm install -g bower
bower install

# Make the default config file
echo "# This file holds constants for configuration.
# DO NOT EXPOSE THIS TO GITHUB.

DEBUG = True
SECRET_KEY = 'development key'
DATA_FOLDER = '`pwd`/app/data'
TEMP_DIRECTORY = '`pwd`/temp'

TEST_USERNAME = 'will'
TEST_PASSWORD = 'password'" > app/config.py

# Database setup
mysql.server restart
echo "Setting up database... when prompted, enter your database root password"
mysql -u root -p < ../setup/acm.sql
