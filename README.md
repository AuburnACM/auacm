#AUACM

Welcome to the Auburn ACM site source code

We're Auburn students building a platform for collaborative and competitive
programming as well as a community of excited developers.

Think this is cool and want to get involved? Email us at auburnacm@gmail.com
or join us on Slack! We'd love you to help us out!

## Getting started with Docker

Docker is the simplist and fastest way to get up and running with the AUACM
app.  ***This procedure works for all platforms, but your specific environment
may require specific steps or settings***

1. Install [Docker](https://docs.docker.com/engine/installation/) and
   [Docker Compose](https://docs.docker.com/compose/install/).
   * Docker is a *Linux containerization engine*. Containers are like virtual
     machines, but lower level and much more lightweight. The Docker engine is
     what creates, manages, and deletes the individual containers.
   * Docker Compose is not strictly required, but it will make building and
     running the app demonstrably easier.
   * Note that if you're using OSX, both Docker and Docker Compose can be
     installed with Homebrew (see below).
2. Clone this repo and build the Docker images
   * In the terminal, run `$ git clone https://github.com/AuburnACM/auacm`
     wherever you want the project to live.
   * Navigate to the `auacm/auacm` directory and run `$ docker-compose build`.
     This will take a few minutes while all the images are downloaded and
     built, but it will only need to be run once.
3. Create a `config.py` file in the `app` directory. A minimal one is shown
   below:
   ```python
   import os
   DEBUG, TEST = True, False
   SECRET_KEY = 'development key'
   DATA_FOLDER = os.getcwd() + '/app/data'
   ```
4. Run `docker-compose up` in the `auacm/auacm` directory. Add a `-d` flag if
   you don't want to see all the messy output.

The server will take a few seconds to start, but then you should be able to
access it at the IP of your Docker engine at port 5000 (usually
`localhost:5000` or `192.168.99.100:5000` on OSX/Windows). The running code
is linked to the repository you cloned, so any changes will cause the server
to reload. You can bring down the server with `docker-compose stop`.

### A note on Docker and database persistence

Due to the way Docker runs, the database only has a semi level of persistence.
If the app is brought down with `docker-compose stop`, any changes to the
database will still be there when it's brought up again (either with 
`docker-compose up` or `docker-compose start`). However, if you use the command
`docker-compose down`, this **destroys** the containers running the app, and
any changes to the database will **not** persist.

If you want to ensure that your changes last, you can dump the database with
`docker exec auacm_mysql_1 mysqldump --databases acm --add-drop-database >
{{dump file here}}`. Upon creation, Docker reads from the
`auacm/auacm/docker-data/acm.sql` to create the database, so dumping there will
cause the database state to persist.


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

# Need test solutions or competitions?
## We've got you covered.

1. Navigate to /setup in terminal and type

    ``$ chmod +x create_competition.sh``

    ``$ ./create_competition.sh <<< "Your Mock Mock Competition Name"``

2. When prompted for your passwords, simply enter them there. If you
   don't have one, just press enter.

3. Have fun with your Mock Mock Competition. All of the solitions
   should be located in the /testing/testSolutions folder.
