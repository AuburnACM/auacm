# AUACM

Welcome to the Auburn ACM site source code

We're Auburn students building a platform for collaborative and competitive
programming as well as a community of excited developers.

Think this is cool and want to get involved? Email us at auburnacm@gmail.com
or join us on Slack! We'd love you to help us out!

## Configuring your project

Since we don't want to share config files (that's bad), you should create your
own `config.py` file when once you clone the repository. There are a few things
you should drop in there. A good config file looks like this:

```py
# This file holds constants for configuration.
# DO NOT EXPOSE THIS TO GITHUB.
DEBUG = True
SECRET_KEY = 'developement key'
DATA_FOLDER = '/Path/to/your/clone/auacm/auacm/app/data'
TEMP_DIRECTORY = '/Path/to/your/clone/auacm/auacm/temp'

TEST_USERNAME = 'will'
TEST_PASSWORD = 'password'
```
Your `config.py` file belongs in the `auacm/auacm/app` directory.

## Getting started with Docker

Docker is the simplist and fastest way to get up and running with the AUACM
app.  ***This procedure works for all platforms, but your specific environment
may require specific steps or settings***

1. Install [Docker](https://docs.docker.com/engine/installation/) and
   [Docker Compose](https://docs.docker.com/compose/install/).
   * Docker is a *Linux containerization engine*. Containers are like virtual
     machines, but lower level and much more lightweight. The Docker engine is
     what creates, manages, and deletes the individual containers.

2. **Mac and Windows users** need to install a Docker machine to run docker
   containers on (this requires VirtualBox as well). For OSX, create a Docker
   machine with

   `$ docker-machine create --driver virtualbox default`

   Then set up some environment variables with

   `$ eval $(docker-machine env default)`

   To test, run `docker info` and make sure that it doesn't return an error.

   *NOTE* Docker containers will run on this virtual machine (called the
   Docker host). When the web app runs, you can find the correct IP with
   `docker-machine ip default`.

3. Clone this repo and build the Docker images

   `$ git clone https://github.com/AuburnACM/auacm`

   Navigate to the `auacm/auacm` directory and run:

   `$ docker-compose build`

   This will take a few minutes while all the images are downloaded and
   built, but it will only need to be run once.

4. Create a `config.py` file in the `app` directory. A minimal one is shown
   below:

   ```python
   import os
   DEBUG, TEST = True, False
   SECRET_KEY = 'development key'
   DATA_FOLDER = os.getcwd() + '/app/data'
   ```

5. In the `auacm/auacm` directory, run

    `$ docker-compose up`

   Add a `-d` flag if you don't want to see all the messy output.

The server start, and can be accessed access at the IP of your Docker engine
at port 5000 (usually `localhost:5000` or `192.168.99.100:5000` on
OSX/Windows). The running code is linked to the repository you cloned,
so any changes will cause the server to reload. You can bring down the server
with `docker-compose stop`.

### A note on Docker and database persistence

Due to the way Docker runs, the database only has a semi level of persistence.
If the app is brought down with

`$ docker-compose stop`

any changes to the database will still be there when it's brought up again
(either with `docker-compose up` or `docker-compose start`). However, if you
use the command

`$ docker-compose down`

this **destroys** the containers running the app, and any changes to the
database will **not** persist.

If you want to ensure that your changes last, you can dump the database with

`docker exec auacm_mysql_1 mysqldump --databases acm --add-drop-database > {{dump file here}}`

Upon creation, Docker reads from the `auacm/auacm/docker-data/acm.sql` file to
create the database, so dumping there will cause the database state to persist.


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

## Creating Judge Times for Problems

So, you probably want to actually time out after a reasonable amount of time.
To generate all of the judge times on your host machine, simply set up the
project as you usually would, then from the same directory as `run.py`, simply
run `$ ./time_problems.py`. It may take a while, as it runs all of the
submissions in serial.
