#!/bin/sh
echo "Setting up virtualenv."
pip install virtualenv
cd ../auacm/
virtualenv -p $(which python3) flask
CFLAGS="-std=c99" ./flask/bin/pip install -r requirements.txt
echo "Setting up nodejs and bower."
npm install bower
bower install
