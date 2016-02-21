# Contributing to Auburn ACM Website

Welcome! We're glad you've taken an interest in the Auburn ACM website and want
to help out. We're glad to take any and all help you might be willing to give.
This document will give you an idea of the contributing process and some of
the specific requirements of our project. Even if you contributed to other open-
source projects before, it would greatly benefit you to glance over this
document.

## The Contribution Process

### 1. Fork* the Project
AUACM follows a standard branch/request/merge process that is typical for open-
source projects on Github. To get started, you'll want to fork the
[project](https://github.com/AuburnACM/auacm) into your own account. From there,
you can create a new branch to make all of your changes. Give your branch name
a short, descriptive name, and separate words with hyphens (i.e. problem-upload
and better-docs).

*AUACM members are not required to fork, but can work in the project repository
directly. They still need to create their own branches.

### 2. Make Your Changes
You should keep the scope of your changes narrow: a branch should incorporate
changes for _one_ specific purpose. Our [backlog](https://github.com/AuburnACM/auacm/issues)
has lots of items that need to be addressed. If you plan on working on one of
these issues, feel free to drop a comment and let other contributors know that
you're working on that bug.

Commit messages should be descriptive and in the present subjunctive tense
("Add submit button", "Correct scoreboard timing"). If any additional details
would be helpful, include them in the subsequent lines of the message, but be
careful that your lines dont exceed 60-80 characters. See [this](http://chris.beams.io/posts/git-commit/)
guide for creating excellent commit messages.

### 3. Open a Pull Request
When you feel like your work is done, open a pull request on Github. This will
alert the members that you are ready for your code to be reviewed and considered
for merging into the master branch. ***All changes must be reviewed by at least
one AUACM member. Period.***

The reveiwer will leave comments on the pull request if they have any questions
or concerns about the code. If there are any issues, they should be
resolved _before_ the request gets merged. While separate pull requests can be
made to fix other branches, this is unadivsable. As much as possible, the
original author should work to correct their own changes.

When the branch is ready and all issues are resolved, the reviewing member will
merge the branch into master, and then subsequently delete the branch that was
just merged. ***No member should ever merge their own branch.***

---

## Before You Submit a Pull Request

### Do a Self-Review
Do a `git diff` (or look at the diff on Github) and inspect your code line by
line. Make sure no residual debug or print statements are left behind. The code
should make sense and be readable to anybody who may look at it. _Remember:_ 
Code gets read much more often than it gets written. As soon as you hit "save",
it becomes legacy code that will inevitably have to be worked on later. Comments
are enthusiastically encouraged, but don't overcomment or simply state what the
code does. Assume that the reader understands the language better than you do.

### Run Unit Tests
The project includes a (small) suite of unit tests. These can serve as warning
signals that some changes may have broken another part of the app. To run them,
simply go to the top-level directory and execute the `test.py` script. If any
of the tests fail or throw an error, they should be corrected before submitting
the pull request. ***No branch should be merged if it causes unit tests to
fail.***

### Do a Dry Run of any Changes
This almost goes without saying, but you should run any changed components on
your own machine and ensure they work as expected. A reviewer will checkout
your changes and do this as well, so it saves them time and increases the
likelyhood that your changes will be merged sooner rather than later.

### Update Docs and Tests
Before submitting a pull request, you should check if your changes alter or
add any components of the API that will need to have the documentation altered.
Additionally, any new components should have complete (passing) unit tests.
***Every developer is responsible for documenting and testing their own code.***

---

## Style and Additional Considerations

All code should follow consistent style the existing codebase. Specifically,
follow the Google style guides for [Python](https://google.github.io/styleguide/pyguide.html)
and [JavaScript](https://google.github.io/styleguide/javascriptguide.xml), as
well as the [PEP 8](https://www.python.org/dev/peps/pep-0008/) style guide for
Python. If you are working with legacy code that does not adhere to any of
these styles, feel free to correct them. Readability and consistency are core
values of the project.

A few key style notes:

  * **Always** uses spaces for indentation
  * HTML uses 2 spaces for indentation; Python and JavaScript use 4
  * Docstrings are required for every Python method
  * Python and JavaScript lines should not exceed 80 characters in length

---

If you have any questions or comments, feel free to email a member, leave a
comment on the repo, or drop a note in the `#auacm-web-dev` channel on Slack.
Have fun, and thanks for helping us make AUACM awesome!