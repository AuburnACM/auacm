#!/bin/sh
echo "Setting up mysql database..."
sudo mysql -uroot < acm.sql
echo "Setting up test database..."
echo "DROP DATABASE IF EXISTS acm_test; CREATE DATABASE acm_test; USE acm_test;\n" | mysql -uroot < acm.sql

# Ask to purge the data directory if it exists
if [ -e ../auacm/app/data ]
then
  echo "Do you want to reset the data directory? (y/n)"
  read purge
fi
if [ "$purge"="y" ]
then
  echo "Purging and resetting data directory..."
  rm -rf ../auacm/app/data
  rmdir ../auacm/app/data
fi

# Create the data directory if it doesn't exist
if [ ! -e ../auacm/app/data ]
then
  echo "Setting up submissions and problems data."
  mkdir ../auacm/app/data
  cp data.zip ../auacm/app/data.zip
  cd ../auacm/app
  unzip data.zip
  rm data.zip
fi

# Remove the __MACOSX directory if created from the unzip
if [ -e __MACOSX ]
then
  rm -rf __MACOSX
fi
echo "Done!"
