#!/bin/sh
echo "This script will set up AUACM."
sudo sh ubuntu_dependencies.sh
echo "Setting up mysql database."
sudo mysql -uroot < acm.sql
sh initialize_database.sh
echo "Setting up virtualenv."
sudo pip install virtualenv
sh initialize_virtualenv.sh
echo "Setup complete!  You should now be able to launch the server."
echo "To get started execute $ cd ../auacm && ./run.py to get started"
