# Ensure brew is installed

brew --version > /dev/null

if [ $? != 0 ]
then
	echo "Installing brew..."
	ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
else
	echo "Brew already installed"
fi

# Check if git is installed

if brew ls --versions git > /dev/null
then
	echo "Git already installed"
else
	echo "Installing Git..."
	brew install git
fi

# Check if npm is installed
if brew ls --versions node > /dev/null
then
	echo "Node already installed"
else
	echo "Installing node..."
	brew install node
fi

# Check if @angular/cli is installed
ng --version > /dev/null
if [ $? != 0 ]
then
	echo "Installing @angular/cli"
	npm install -g @angular/cli
else
	echo "@angular/cli already installed"
fi

# Check if python3 is installed
if brew ls --versions python3 > /dev/null
then
	echo "python3 already installed"
else
	echo "Installing python3"
	brew install python3
fi

# Check if python3 is installed
if brew ls --versions mysql > /dev/null
then
	echo "mysql already installed"
else
	printf "Do you want to install MySQL? [y/n]: "
	read INSTALL_MYSQL

	if [ $INSTALL_MYSQL == 'y' ]
	then
		# Check if mysql is installed
		if brew ls --versions mysql > /dev/null
		then
			echo "mysql already installed"
		else
			echo "Installing mysql"
			brew install mysql
		fi

		echo "Adding Service tap"
		brew tap homebrew/services
		brew services start mysql

		echo "You can stop the MySQL server with 'brew services stop mysql'"
		sleep 3
	fi
fi
