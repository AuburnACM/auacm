#AUACM

Welcome to the Auburn ACM programming competition source code.

You're probably wondering how to set up your environment, so here we go:

1. Start by cloning the repo. 
``git clone https://github.com/AuburnACM/AUACM.git ``
2. Navigate to ``/../AUACM/auacm/`` and set up your virtual environment.
Do this by executing the following commands: 

    ``$ pip install virtualenv``
  
    ``$ virtualenv flask``
    
    ``$ pip install -r requirements.txt``
    
3. Install Bower with

    ``$ npm install -g bower``
    
4. Install local components using Bower

    ``$ bower install`` 

5. Now you can run the server on ``localhost:5000`` by running

    ``$ python run.py``
    
More steps to come.
