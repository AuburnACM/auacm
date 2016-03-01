# AUACM API Documentation

The Auburn ACM website uses a RESTful api for managing blog posts, problems,
submissions, etc. This document is intended to serve as a one stop shop for the
method arguments and return values.

### Table of Contents

1. [Problem Management](#problem-management)
2. [Competition Management](#submission-management)
3. [Submission Management](#submission-management)
4. [Blog Management](#submission-management)
5. [User Management](#user-management)

---

## Problem Management

### Create a New Problem
***This method requires being logged in as an administrator***

__URL:__ `/api/problems/`

__Method:__ `POST`

__Form Data:__

| Title | Required | Form Name | Description|
| --- | --- | --- | --- |
| Problem Name | __Required__ | `name` | Full problem name (<= 32 characters) |
| Description | __Required__ | `description` | Problem background story |
| Input Description | __Required__ | `input_desc` | Description of input values |
| Output Description | __Required__ | `output_desc` | Description of output values |
| Sample Case(s) | __Required__ | `cases` | JSON array of sample input/output (see below) |
| Time Limit | Optional | `time_limit` | Max execution time (in seconds) |
| Difficulty | Optional | `difficulty` | 0-100 difficulty rating |
| Appeared In | Optional | `appeared_in` | String name of original competition |
| Input Files | __Required__ | `in_file` | Zipped (.zip) directory of all input files |
| Output Files | __Required__ | `out_file` | Zipped (.zip) directory of all output files |
| Solution File | __Required__ | `sol_file` | Solution program (not zipped) |

__Sample Cases__ must be a string JSON array of mappings `input` and `output`.
For example,

```json
[
  {
    "input": "1",
    "output": "2"
  },
  {
    "input": "2",
    "output": "3"
  }
]
```

will correspond to a sample case where an input of 1 produces
an output of 2, and a sample case where an input of 2 produces
an output of 3.

If any of the required fields are not supplied, the API will return with status
code 400. If successful, the API will return a JSON object representation of
the newly created problem.


### Get Data on a Problem

__URL:__ `/api/problems/{identifier}`

 _Note:_ A problem's identifier can be its numeric id (i.e. 1) or it's
 alphanumeric "shortname" (i.e. blackvienna)

__Method:__ `GET`

Returns detailed data on a specific problem as a JSON object.
Specifically, the method returns:

__Response:__

| Name | Type | Description |
| --- | --- | --- |
|`added`|`int`|The UTC timestamp indicating when this problem was uploaded|
|`appeared`|`String`|The contest this problem originally appeared in|
|`comp_release`|`int`|The competition `cid` for the competition this was used in|
|`description`|`String`|The problem's description|
|`difficulty`|`int`|The difficulty of the problem from 0 (easiest) to 100 (hardest)|
|`input_desc`|`String`|A description of the problem's input|
|`name`|`String`|The name of the problem|
|`output_desc`|`String`|A description of the problem's output|
|`pid`|`int`|The problem's unique id.|
|`sample_cases`|`List<SampleCase>`|A list of samples cases, as described above|
|`shortname`|`String`|A unique string with no spaces used to identify the problem|

Example use:

```json
{
  "added": 1426310855,
  "appeared": "2014 Mid-Central",
  "comp_release": 6,
  "description": null,
  "difficulty": "67",
  "input_desc": null,
  "name": "(More) Multiplication",
  "output_desc": null,
  "pid": 34,
  "sample_cases": [],
  "shortname": "multiplication"
}
```


### Get Data on all problems

__URL:__ `/api/problems/`

__Method:__ `GET`

Returns a JSON array of all the publicly available data on all the problems problems
in the database.
Specifically, the method returns
  * __pid__
  * __full name__
  * __short name__
  * __competition appearance__
  * __difficulty rating__
  * __auacm competition release__
  * __date added__
  * __solved by user__
  * __problem url__

of all the problems.

### Edit an Existing Problem
***This method requires being signed in as an administrator***

__URL:__ `/api/problems/{identifier}`

_Note:_ {identifier} can be the numeric problem ID or the alphanumeric
"shortname"

__Method:__ `PUT`

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Name | Optional | `name` | Full title of the problem (also becomes shortname) |
| Description | Optional | `description` | Full text of problem description |
| Input Description | Optional | `input_desc` | Input description text |
| Output Description | Optional | `output_desc` | Output description text |
| Appearance | Optional | `appeared_in` | Original competition of problem |
| Difficulty | Optional | `difficulty` | 0-100 difficulty rating |
| Sample Cases | Optional | `cases` | JSON array of input/output (see problem creation) |
| Input Files | Optional | `in_file` | Zipped (.zip) directory of judge input files (must be titled `in.zip`) |
| Output Files | Optional | `out_file` | Zipped (.zip) directory of judge output files (must be titled `out.zip`) |
| Judge Solution | Optional | `sol_file` | Judge solution program (do not zip) |

___Note:___ Any form data parameters not given will remain unaffected by the request.

__Returns:__ A JSON object of the detailed information of the newly-edited problem.


### Delete a Problem
***This method requires being signed in as an administrator***

__URL:__ `/api/problems/{identifier}`

_Note:_ {identifier} can be the numeric problem ID or the alphanumeric
"shortname"

__Method:__ `DELETE`

Deletes a problem completely from the database. If any judge files are
associated with the problem, they get deleted as well.

__Returns:__ A JSON object containing the problem identifier of the
successfully deleted problem (`deleted_pid`)

---

## Competition Management

Competitions are always represented in JSON with the following format:

  * __cid:__ The integer competition ID
  * __closed:__ `true` if administrators are the only users that can register
   participants, `false` otherwise
  * __length:__ The length of the competition, in seconds
  * __name:__ The string name of this competition
  * __registered:__ `true` if the current user is registered for this
   comptition, `false` otherwise
  * __startTime:__ The time the competition starts, in seconds since the Unix
   epoch
  * __compProblems:__ An array of all the problems in the competition


The `compProblems` object is formatted as follows:

```json
"compProblems": {
  "A": {
    "name": "Islands in the Data Stream ",
    "pid": 23,
    "shortname": "islands"
  },
  "B": {
    "name": "Von Neumann's Fly",
    "pid": 63,
    "shortname": "vonneumann"
  }
}
```

The API will also accept a Web Socket message `system_time` with no data. The
API will reply with another Web Socket message with the same name and the
server's system time in milliseconds since the Unix epoch, exampled below:

```json
{
  "milliseconds": 1456071193888
}
```

### Get Data on all Competitions

__URL:__ `/api/competitions`

__Method:__ `GET`

Returns a JSON object of all competitions, broken up into three categories:
__ongoing__ (in progress), __past__, and __upcoming__. Each one of these objects
contains an array of all the competitions in that category, if any.


### Get Data on a Specific Competition

__URL:__ `/api/competitions/{competition_id}`

__Method:__ `GET`

Returns a JSON object of the competition with the ID matching `{competition_id}`
in the route.


### Create a Competition
***This method requires being signed in as an administrator***

__URL:__ `/api/competitions`

__Method:__ `POST`

Creates a competition and returns the new competition as a JSON object. The
JSON follows the normal formatting for competitions, outlined above. Below is
a table of the form arguments for creating the competition.

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Name | Required | `name` | Name of the competition |
| Start Time | Required | `start_time` | The start time of the competition, in seconds since the Unix epoch |
| Length | Required | `length` | The length of the competition, in seconds |
| Closed | Required | `closed` | `true` if the competition is closed, `false` otherwise |
| Problems | Required | `problems` | A JSON object of the problems in the competitions (see format below) |

The structure of the `problems` is identical to the `compProblems` object
demonstrated at the top of this heading.


### Edit an Existing Competition
***This method required being signed in as an administrator***

__URL:__ `/api/competitions/{competition_id}`

__Method:__ `PUT`

Change an existing competition, identified by `competition_id`. Nothing gets
returned upon success.

Form fields are identical to creating a new competition (see above).


### Register a User for a Competition

__URL:__ `/api/competitions/{competition_id}/register`

__Method:__ `POST`

Registers the current user for a competition. No form data is required to self-
register, though if the current user is an admin, a JSON array of usernames
can be supplied. All users in the array will be registered, but the admin will
not (unless also included in the array).

An error will be returned upon trying to register a user who is already
registered. Otherwise, no response is returned.

__Web Sockets:__ A message `new_user` will be sent to every connected client
when a new user is registered with the following data:

```json
{
  "cid": 12,
  "user": {
    "display": "Yeezus",
    "username": "kanyew"
  }
}
```


### Unregister a User for a Competition

__URL:__ `/api/competitions/{competition_id}/unregister`

__Method:__ `POST`

Unregisters the current user for a competition. No form data is required to
self-unregister, though if the current user is an admin, a JSON array of
usernames can be supplied. All users in the array will be registered, but the
admin will not (unless also included in the array).

### Get All Teams in an Competition
***This method requires being signed in as an administrator***

__URL:__ `/api/competitions/{competition_id}/teams`

__Method:__ `GET`

Returns all of the teams in a competition.

The format of the JSON is as follows:

```json
"Team Awesome": [
  {
    "display": "Hester",
    "username": "will"
  },
  {
    "display": "Jeff Overbey",
    "username": "jeffo"
  }
],
"Bernie Sanders": [
  {
    "display": "The 99 Percent",
    "username": "feelthebern"
  }
]
```

### Edit the Teams for a Competition
***This method requires being signed in as an administrator***

__URL:__ `/api/competitions/{competition_id}/teams`

__Method:__ `PUT`

Take the JSON object in the form and use it to arrange the participants in a
competition. Any teams or users not included in the JSON data will not be a part
of the competition and will have to re-register, though this should not be used
for the sole purpose of unregistering participants.

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Teams | Required | `teams` | A JSON object of the arrangment of teams |

The JSON object should be formatted as an array of teams, each team mapped
to an array of the participants (see above for example).

---

## Submission Management

### Submit a Solution

__URL:__ `/api/submit`

__Method:__ `POST`

Submit a solution to server for evaluation. Note that the results of the
submission are sent back via websockets, and at the time of this writing cannot
be obtained from the API. The submission id is returned from the API upon
success.

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Solution File | Required | `file` | The program file that contains the submission |
| Problem ID | Required | `pid` | The integer ID of the problem for this submission |
| Python Version | Optional | `python` | Specify the version of Python: `py` for Python 2.7 and `py3` for Python 3 |

After the submission, the API will send out several Web Socket messages to all
connected clients regarding the execution status of the submission. All these
messages will be named `status` and have the following form:

```json
{
  "submissionId": 2339,
   "problemId": 12,
   "username": "brandonm",
   "submitTime": 1456071193,
   "testNum": 17,
   "status": "compile"/"start"/"runtime"/"timeout"/"wrong"/"good"
}
```

### Get one or more Submissions

__URL:__ `/api/submit`

__Method:__ `GET`

Return a JSON representation of one or more submissions. The URL can take three
optional query string arguments: `username` and `limit`.

- `username`: Will only return submissions made by this user. If left blank,
submissions from all users will be returned.
- `limit`: limits the number of submissions returned (default and max is 100)

The structure of the JSON object is as follows:

```json
{
  "job_id": 20013,
  "pid": 17,
  "username": "bernitup",
  "file_type": "java",
  "status": "timeout"
}
```

Submissions are always returned chronologically from when the submission was
made. The most recent submissions will appear first.

__Examples__

* `/api/submit?username=moon_yu` will return up to 100 of the most recent
submissions made by the user `moon_yu`

* `/api/submit?username=tswizzle&limit=6` will return up to 6 of the most recent
submissions made by the user `tswizzle`

* `/api/submit` will return up to 100 of the most recent submissions, regardless
of the user

### Get a Submission from its ID

__URL:__ `/api/submit/{job_id}

__Method:__ `GET`

Returns a JSON object of the submission with the id `job_id`. If no such
submission can be found, a 401 error response is returned.


---

## Blog Management

### Get All Blog Posts

__URL:__ `/api/blog`

__Method:__ `GET`

Returns all the blog posts as JSON objects, in reverse chronological order.

The JSON structure of a blog post is as follows

```json
{
  "title": "A Blog Post",
  "subtitle": "This title is a little smaller",
  "postTime": 1456009720,
  "body": "This is a blog post. It has words and things. _It_ `also` **has** ***markdown***.",
  "author" : {
    "username": "hermancain",
    "display": "2012!"
  }
}
```

### Create a New Blog Post

__URL:__ `/api/blog`

__Method:__ `POST`

Creates a new blog post fromt the given form data.

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Title | Required | `title` | Title of the blog post |
| Subtitle | Required | `subtitle` | Subtitle of the blog post |
| Body | Required | `body` | Body of the blog post |

---

## Problem Management

### Get Info on the Current User

__URL:__ `/api/me`

__Method:__ `GET`

Retrieves data about the currently logged in user as a JSON object. The returned
JSON is formatted as follows:

```json
{
  "username": "George Washington",
  "displayName": "Merica",
  "isAdmin": 1
}
```

Note that the `isAdmin` field will be 1 if the user is an administrator and 0
if not.

### Log In

__URL:__ `/api/login`

__Method:__ `POST`

Attempts to log in the current user.

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Username | Required | `username` | The username of the user |
| Password | Required | `password` | The password of the user |

Note that the api will return a `401` response if the authentication fails.

### Log Out

__URL:__ `/api/logout`

__Method:__ `POST`

Logs out the current user. No data is returned upon success.

### Create a New User

__URL:__ `/api/create_user`

__Method:__ `POST`

Creates a new user, according to the data supplied in the form.

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Username | Required | `username` | The username of the new user |
| Password | Required | `password` | The password of the new user |
| Display | Required | `display` | The display name of the new user |

The API will serve a `401` response if the username is already taken.

### Change the Password of a User

__URL:__ `/api/change_password`

__Method:__ `POST`

Updates the password of the currently signed in user.

__Form Data__

| Title | Required | Form Name | Description |
| --- | --- | --- | --- |
| Previous Password | Required | `oldPassword` | The old password of the current user |
| New Password | Required | `newPassword` | The the new password for the current user |

The API will serve a `401` response if the `oldPassword` field does not match
the current password of the current user.

### Get a Ranking of all Users

__URL:__ `/api/ranking[/{timeframe}]`

__Method:__ `GET`

Obtain a JSON array of all users who have solved at least one problem, ranked
in order of number of problems solved (within a time frame if provided).

The time frame portion of the route is optional. Acceptable arguments are:
`day`, `week`, `month`, `year`, and `all` (default). If one of the values is
selected, only solution submitted (the first time) within the time frame since
the request is made will be used for ranking.

The request will return a JSON array in the following format:

```json
[{
      "displayName": "Mitch Loser Price",
      "rank": 1,
      "solved": 10000,
      "username": "mitchp"
    },
    {
      "displayName": "Brian Newb Pwn3r Da Roach",
      "rank": 2,
      "solved": 6,
      "username": "brian"
    },
    ...
}]
```
