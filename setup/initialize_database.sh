#!/bin/sh
echo "Setting up mysql database..."

printf "Create default MySQL acm user? [y/n]: "
read CREATE_ACM

if [ $CREATE_ACM == 'y' ]
then
  mysql -u root -e "create user 'acm'@'localhost'"
  mysql -u root -e "grant all on acm.* to 'acm'@'localhost'"
  mysql -u root -e "flush privileges"
else
  echo "Skipping create acm user"
fi

printf "Create empty database? [y/n]: "
read CREATE_EMPTY

if [ $CREATE_EMPTY == 'y' ]
then
  echo "Creating an empty database..."
  mysql -uroot -D acm < acm_blank.sql
else
  echo "Creating database with data..."
  mysql -uroot < acm.sql
fi

printf "Create new AUACM admin? [y/n]: "
read CREATE_ADMIN

if [ $CREATE_ADMIN == 'y' ]
then
  # Create an admin user
  echo "Creating AUACM admin user..."
  USER_PWD='$2b$12$95hJ.4TE68Aj15vIxv8gPOGD0AH.qw623YM/zlX99xmTA/DXpvflG'
  printf "Enter a display name for the user: "
  read USER_DISPLAY

  printf "Enter a username for the user: "
  read USER_NAME

  mysql -u root -D acm -e "insert into users values ('$USER_NAME', '$USER_PWD', '$USER_DISPLAY', 1);"
  printf "You can log into AUACM with the username '$USER_NAME' and the password 'password'.\n"
  sleep 3
fi

# echo "Setting up test database..."
# if [ ! -e acm_test.sql ]
# then
#   echo "Please back up the databse first using backup_database.sh."
#   echo "That will create the necessary acm_test.sql file"
#   exit
# fi
# echo "DROP DATABASE IF EXISTS acm_test;" | mysql -uroot -p
# echo "CREATE DATABASE acm_test;" | mysql -uroot -p
# echo "DROP DATABASE IF EXISTS acm_test; CREATE DATABASE acm_test; \n" | mysql -uroot acm_test < acm_test.sql
# mysql -uroot acm_test < acm_test.sql

# Ask to purge the data directory if it exists
if [ -e ../auacm/app/data ]
then
  printf "Do you want to reset the data directory? [y/n]: "
  read purge
fi
if [ "$purge" = "y" ]
then
  echo "Purging and resetting data directory..."
  rm -rf ../auacm/app/data
fi

# Create the data directory if it doesn't exist
if [ ! -e ../auacm/app/data ]
then
  echo "Setting up submissions and problems data."
  mkdir ../auacm/app/data
  cp data.zip ../auacm/app/data.zip
  cd ../auacm/app
  unzip data.zip > /dev/null
  rm data.zip
fi

# Remove the __MACOSX directory if created from the unzip
if [ -e __MACOSX ]
then
  rm -rf __MACOSX
fi
echo "Done!"
