# AUACM

Welcome to the Auburn ACM site source code

We're Auburn students building a platform for collaborative and competitive
programming as well as a community of excited developers.

Think this is cool and want to get involved? Email us at auburnacm@gmail.com
or join us on Slack! We'd love you to help us out!

Just a quick note: we only support Ubuntu and MacOS for development. If you use any other development platform, we will not be able to assist you.

## Mac Installation

1. cd to the `/../auacm/setup/` directory

2. execute the script `./macos_setup.sh`. This will walk you through setting up the development environment for the site.

   To see more detailed instructions, view the [Installation docs](https://github.com/AuburnACM/auacm/blob/master/docs/installation.md)

## Ubuntu Installation

1. cd to the `/../auacm/setup/` directory

2. execute the script `./ubuntu_setup.sh`. This will walk you through setting up the development environment for the site.

   To see more detailed instructions, view the [Installation docs](https://github.com/AuburnACM/auacm/blob/master/docs/installation.md)

## Creating Judge Times for problems

So, you probably want to actually time out after a reasonable amount of time.
To generate all of the judge times on your host machine, simply set up the
project as you usually would, then from the same directory as `run.py`, simply
run `$ ./time_problems.py`. It may take a while, as it runs all of the
submissions in serial.
