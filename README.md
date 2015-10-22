#AUACM

Welcome to the Auburn ACM programming competition source code.

You're probably wondering how to set up your environment, so here we go:

##Mac

0. Install Homebrew 
    ``$ ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)" ``

1. Install git
    ``brew install git ``

2. Clone the repo. (Or install git if you have not)

    ``git clone https://github.com/AuburnACM/AUACM.git ``

3. Navigate to ``/../auacm/auacm/`` and set up your virtual environment.
Do this by executing the following commands: 

    ``$ pip install virtualenv``
  
    ``$ virtualenv flask``
    
    ``$ CFLAGS='-std=c99' ./flask/bin/pip install -r requirements.txt``

4. Install npm with

    ``$ brew install npm``
    
5. Install Bower with

    ``$ npm install -g bower``
    
6. Install local components using Bower

    ``$ bower install`` 

7. Now you can run the server on ``localhost:5000`` by running

    ``$ ./run.py``
    
8. Navigate to the setup folder.

9. Copy data.zip to auacm/app/data.zip and extract the contents
   there. You should now have two folders inside auacm/app/data/,
   problems and submits.

##Ubuntu

0. Install git
    
    ``$ sudo apt-get install git ``

1. Clone the repo.

    ``git clone https://github.com/AuburnACM/AUACM.git ``

2. Navigate to ``.../AUACM/setup/`` and execute this to setup the environment:
    
    ``$ sh ubuntu_setup.sh``
    
    Follow all the setup instructions.

3. Now you can run the server on localhost:5000 by running
    
    ``$ cd ../auacm``

    ``$ ./run.py``

##Additional Notes

###Flask Configuration

In order to run properly, a flask configuration file needs to be added. For
security purposes, the file is not included in the repository. However, a simple
development one can be created. Add a ``config.py`` file to
``auacm/auacm/app/`` directory with the following:

```python
# This file holds constants for configuration.
# DO NOT EXPOSE SECRET KEY TO GITHUB
import os

DEBUG = True
SECRET_KEY = 'development key'
DATA_FOLDER = os.getcwd() + '/app/data'
```

###Database Configuration

In order for the database to work properly, you need to have MySQL installed
and running on your computer. For Mac, use ``brew install mysql`` to install.
Once installed, you'll need to run ``mysql.server start`` to spin up the server.
Then move to the setup folder and run the ``initialize_database.sh`` script
to configure the database with the appropriate schema.

# Need test solutions or competitions?
## We've got you covered.

1. Navigate to /setup in terminal and type

    ``$ chmod +x create_competition.sh``

    ``$ ./create_competition.sh <<< "Your Mock Mock Competition Name"``

2. When prompted for your passwords, simply enter them there. If you
   don't have one, just press enter.

3. Have fun with your Mock Mock Competition. All of the solitions
   should be located in the /testing/testSolutions folder.
