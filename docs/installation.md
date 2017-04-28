The website currently supports Ubuntu 16.04 and Centos 7 for production use. You may use MacOS or Ubuntu for development purposes, but Windows is not supported at all at this time.

## Table of Contents
1. [MacOS Development Installation](https://github.com/AuburnACM/auacm/wiki/Installation#macos-development-installation)

   1. [Install Steps](https://github.com/AuburnACM/auacm/wiki/Installation#macos-install-steps)
   2. [Packages Installed](https://github.com/AuburnACM/auacm/wiki/Installation#macos-packages-installed)

2. [Ubuntu Development Installation](https://github.com/AuburnACM/auacm/wiki/Installation#ubuntu-development-installation)

   1. [Install Steps](https://github.com/AuburnACM/auacm/wiki/Installation#ubuntu-install-steps)
   2. [Packages Installed](https://github.com/AuburnACM/auacm/wiki/Installation#ubuntu-packages-installed)

3. [Config File](https://github.com/AuburnACM/auacm/wiki/Installation#config-file)

4. [Setup Script Questions](https://github.com/AuburnACM/auacm/wiki/Installation#setup-script-questions)

***

## MacOS Development Installation

### MacOS Install Steps

1. Clone this repo with Git. `git clone https://github.com/AuburnACM/AUACM.git`

2. CD to the folder `/../auacm/setup/`

3. Run the script `macos_setup.sh` by executing the command `./macos_setup.sh`. This script will walk you through setting up your development environment. It will ask you a few questions about setting up a config and the database. To see what the questions are asking specifically, see [below](https://github.com/AuburnACM/auacm/wiki/Installation#setup-script-questions)

### MacOS Packages Installed

* [Homebrew](https://brew.sh/)
* [Git](https://git-scm.com/)
* [Node and NPM](https://nodejs.org/en/)
* [Angular Cli](https://cli.angular.io/)
* [Python 3](https://www.python.org/)
* [MySQL](https://www.mysql.com/)

## Ubuntu Development Installation

### Ubuntu Install Steps

1. Clone this repo with Git. `git clone https://github.com/AuburnACM/AUACM.git`

2. CD to the folder `/../auacm/setup/`

3. Run the script `ubuntu_setup.sh` by executing the command `./ubuntu_setup.sh`. This script will walk you through setting up your development environment. It will ask you a few questions about setting up a config and the database. To see what the questions are asking specifically, see [below](https://github.com/AuburnACM/auacm/wiki/Installation#setup-script-questions)

### Ubuntu Packages Installed

* [Git](https://git-scm.com/)
* [Node and NPM](https://nodejs.org/en/)
* [Angular Cli](https://cli.angular.io/)
* [Python 2 & 3](https://www.python.org/)
* [MySQL](https://www.mysql.com/)
* Zip and Unzip

## Config File

Since we don't want to share config files (that's bad), you should create your
own `config.py` file if you haven't already. If you run one of the setup scripts, the config file
will be generated for you. There are a few things you should drop in there. A good config file looks like this:

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

## Setup Script Questions

### Database Questions

1. `Create default MySQL acm user?` - This will create a new `acm` user in your MySQL database. If you have ran the script before, or already have the `acm` user, then you can say `n`.

2. `Create empty database?` - If you say `y` to this, it will create a new `acm` database with no data in it. If you say `n`, then the data in `acm.sql` will be loaded into the database.

3. `Create new AUACM admin?` - This asks if you want to create a new admin user for the website. There is already a default admin account called `admin` with the default password `password` that you may use instead of creating your own.

4. `Create test database?` - This asks if you want to create a separate acm test database. It will backup the main database and load it into a new database called `acm_test`.

5. `Do you want to reset the data directory?` - If you already have the `/../auacm/auacm/app/data` directory, you can reset it with the data inside the `data.zip` file.

### Config Questions

1. `Reset config file?` - If you already have the `/../auacm/auacm/app/config.py` file, this will reset it.

1. `Enter a secret key:` - the key used for encrypting cookies.

2. `Enter a test username:` - the username used for testing the problem data

3. `Enter a test password:` - the password for the test user