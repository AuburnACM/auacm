#!/bin/sh
echo "Installing dependencies."
sudo apt-get install python-dev
sudo apt-get install libffi-dev

if dpkg -l nodejs > /dev/null
then
	curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
fi

export DEBIAN_FRONTEND="noninteractive"
echo "mysql-server mysql-server/root_password password " | sudo debconf-set-selections
echo "mysql-server mysql-server/root_password_again password " | sudo debconf-set-selections

sudo apt-get install mysql-server
sudo apt-get install libmysqlclient-dev
sudo apt-get install build-essential
echo "Dependencies installed."
