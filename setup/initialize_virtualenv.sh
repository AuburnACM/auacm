#!/bin/sh
echo "Setting up virtualenv."
pip install virtualenv
cd ../auacm/
virtualenv flask
flask/bin/pip install -r requirements.txt
echo "Setting up nodejs and bower."
npm install bower
bower install
