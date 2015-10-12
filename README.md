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

3. Navigate to ``/../AUACM/auacm/`` and set up your virtual environment.
Do this by executing the following commands: 

    ``$ pip install virtualenv``
  
    ``$ virtualenv flask``
    
    ``$ pip install -r requirements.txt``

4. Install npm with

    ``$ brew install npm``
    
5. Install Bower with

    ``$ npm install -g bower``
    
6. Install local components using Bower

    ``$ bower install`` 

7. Now you can run the server on ``localhost:5000`` by running

    ``$ ./run.py``
    
More steps to come.

##Ubuntu

0. Install git
    
    ``$ sudo apt-get install git ``

1. Clone the repo.

    ``git clone https://github.com/AuburnACM/AUACM.git ``

2. Navigate to ``.../AUACM/auacm/`` and execute this to setup the environment:
    
    ``source ubuntu_setup.env``
    
    Follow all the setup instructions.

3. Now you can run the server on localhost:5000 by running
    ``$ ./run.py``
