create_config() {
	printf "Enter a secret key: "
	read SECRET_KEY
	DATA_FOLDER="$PWD/app/data"
	TEMP_FOLDER="$PWD/temp"

	printf "Enter a test username: "
	read TEST_USER

	printf "Enter a test password: "
	read TEST_PASSWORD

	printf "# This file holds constants for configuration.
EXPOSE THIS TO GITHUB.
DEBUG = True
SECRET_KEY = '$SECRET_KEY'
DATA_FOLDER = '$DATA_FOLDER'
TEMP_DIRECTORY = '$TEMP_FOLDER'
TEST_USERNAME = '$TEST_USER'
TEST_PASSWORD = '$TEST_PASSWORD'" > app/config.py
	
	echo "Config created!"
}

cd ../auacm/

if [ -f app/config.py ]
then
	echo "Reset config file? [y/n]: "
	read RESET_CONFIG
	if [ $RESET_CONFIG = 'y' ]
	then
		echo "Resetting config..."
		create_config
	else
		echo "Not resetting config."
	fi
else 
	echo "Creating config file..."
	create_config
fi