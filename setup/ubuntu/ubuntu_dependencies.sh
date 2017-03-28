#!/bin/sh
echo "Installing dependencies."
sudo apt-get install python-dev
sudo apt-get install libffi-dev

if dpkg -l nodejs > /dev/null
then
	curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
fi

sudo apt-get install mysql-server
sudo apt-get install libmysqlclient-dev
sudo apt-get install build-essential
echo "Dependencies installed."
