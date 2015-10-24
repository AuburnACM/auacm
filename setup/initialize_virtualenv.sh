#!/bin/sh
cd ../auacm/
virtualenv flask
flask/bin/pip install -r requirements.txt
echo "Setting up nodejs and bower."
npm install bower
node_modules/bower/bin/bower install
