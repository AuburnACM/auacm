#!/bin/sh
echo "Installing dependencies."
apt-get update
apt-get install -y python-dev
apt-get install -y python-pip
apt-get install -y python3-dev
apt-get install -y python3-pip
apt-get install -y virtualenv
apt-get install -y libffi-dev
apt-get install -y zip unzip

curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
apt-get install -y nodejs

# Check if @angular/cli is installed
ng --version > /dev/null
if [ $? != 0 ]
then
	echo "Installing @angular/cli"
	npm install -g @angular/cli@1.4.0
else
	echo "@angular/cli already installed"
fi

DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server
apt-get install -y libmysqlclient-dev
apt-get install -y build-essential
echo "Dependencies installed."
