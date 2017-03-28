#!/bin/sh
echo "This script will set up AUACM."
sudo sh ubuntu/ubuntu_dependencies.sh
sh initialize_database.sh
sh initialize_virtualenv.sh
echo "Setup complete!  You should now be able to launch the server."
echo "To get started execute $ cd ../auacm && ./run.py to get started"
