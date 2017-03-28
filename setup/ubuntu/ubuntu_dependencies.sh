#!/bin/sh
echo "Installing dependencies."
apt-get update
apt-get install -y python-dev
apt-get install -y python-pip
apt-get install -y python3-dev
apt-get install -y python3-pip
apt-get install -y libffi-dev

if dpkg -l nodejs > /dev/null
then
	curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
fi

DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server
apt-get install -y libmysqlclient-dev
apt-get install -y build-essential
echo "Dependencies installed."
