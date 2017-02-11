# AUACM API Documentation

The Auburn ACM website uses a RESTful api for managing blog posts, problems,
submissions, etc. This document is intended to serve as a one stop shop for the
method arguments and return values.

### Table of Contents

1. [Problem Management](#problem-management)
2. [Competition Management](#competition-management)
3. [Submission Management](#submission-management)
4. [Blog Management](#submission-management)
5. [User Management](#user-management)

---

## Problem Management

Problem management allows access to the various problems that exist on the website.
Problems will always be described using the problem JSON object:

__[Problem Object](#the-problem-object)__

Problem objects contain an array of sample cases objects, which are used to describe
an example mapping of input to output for a problem:

__[Sample Case Object](#the-sample-case-object)__


From here, any user can perform the following actions:

1. [Get Data on all Problems](#get-data-on-all-problems)
2. [Get Data on a Specific Problem](#get-data-on-a-problem)

Those logged in as an administrator can perform additional actions:

3. [Create a New Problem](#create-a-new-problem)
4. [Edit an Existing Problem](#edit-an-existing-problem)
5. [Delete a Problem](#delete-a-problem)


## Problem Management Objects

### The Problem Object

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
|`sample_cases`|`Sample Case[]`|A list of samples cases, as described below|
|`shortname`|`String`|A unique string with no spaces used to identify the problem|

The `sample_cases` field consists of an array of [Sample Case](#the-sample-case-object) objects.


__Example:__

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

The above problem object represents a problem that was added to the website March 14, 2015, at 12:30 AM, that
originally appeared in the 2014 Mid-Central ICPC. It was first used in an AUACM competition with id 6. The
problem has no description, input description, output description, or sample cases. It is named (More) Multiplication,
with a problem id of 34, and is also uniquely identified by the string "multiplication". (More) Multiplication
has a difficulty of 67.

### The Sample Case Object

| Name | Type | Description |
| --- | --- | --- |
|`input`|`String`|The input fed to standard in for a test run|
|`output`|`String`|The output that would be produced to standard out by a correct program for that test run|

__Example:__

```json
{
    "input": "1",
    "output": "2"
}
```

The above sample cases object represents a sample case where an input of "1" to a program that
correctly solves the associated problem produces a corresponding output of "2".

## Problem Management Endpoints

### Get Data on all problems

__URL:__ `/api/problems`

__Method:__ `GET`

__Returns:__ a JSON array of all the publicly available data on all the problems
in the database.
Specifically, the method returns an array of [problem objects](#the-problem-object)
describing every public problem in the database.

__Example use:__

`GET /api/problems`

### Get Data on a Problem

__URL:__ `/api/problems/{identifier}`

 _Note:_ A problem's identifier can be its numeric id (i.e. 1) or its
 alphanumeric "shortname" (i.e. blackvienna)

__Method:__ `GET`

__Returns:__ detailed data on a specific problem as a JSON object.
Specifically, the method returns a [problem object](#the-problem-object).

__Example uses:__

`GET /api/problems/multiplication`

`GET /api/problems/34`


### Create a New Problem
***This method requires being logged in as an administrator***

__URL:__ `/api/problems`

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

The `cases` field consists of a JSON array of [Sample Case](#the-sample-case-object) objects.

__Returns:__ If any of the required fields are not supplied, the API will return with status
code 400. If successful, the API will return a [JSON object representation](#the-problem-object)
of the newly created problem.


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

The `cases` field consists of a JSON array of [Sample Case](#the-sample-case-object) objects.

___Note:___ Any form data parameters not given will remain unaffected by the request.

__Returns:__ A [JSON object](#the-problem-object) of the detailed information of the newly-edited problem.


### Delete a Problem
***This method requires being signed in as an administrator***

__URL:__ `/api/problems/{identifier}`

_Note:_ {identifier} can be the numeric problem ID or the alphanumeric
"shortname"

__Method:__ `DELETE`

Deletes a problem completely from the database. If any judge files are
associated with the problem, they will be deleted as well.

__Returns:__ A [JSON object](#the-problem-object) containing the problem identifier of the
successfully deleted problem (`deleted_pid`)

__Example uses:__

`DELETE /api/problems/multiplication`

`DELETE /api/problems/34`

---

## Competition Management

Competition management allows users to participate in, create, or view past competitions. Competitions in general will be represented using the competition JSON object:

__[Competition Object](#the-competition-object)__

More detailed information on the content of a competition will be specified in the form of a competion problems object, which maps a problem label (A, B, C ... ) to more information about the given problem:

__[Competition Problem Object](#the-competition-problems-object)__

Finally, every competition consists of teams, allowing for multiple people to participate, or to at least change their team name:

__[Team Object](#the-team-object)__

From the competition endpoint, all users can perform a variety of tasks:

1. [Get data on all competitions](#get-data-on-all-competitions)
2. [Get data on a specific competition](#get-data-on-a-specific-competition)
3. [Register for a competition](#register-a-user-for-a-competition)
4. [Unregister from a competition](#unregister-a-user-for-a-competition)

Additionally, administrators can perform the following tasks:

1. [Create a competition](#create-a-competition)
2. [Edit a competition](#edit-an-existing-competition)
3. [Get all teams in a competition](#get-all-teams-in-a-competition)
4. [Edit a competition's teams](#edit-the-teams-for-a-competition)

## Competition Management Objects

### The Competition Object

Competitions are always represented in JSON with the following format:

| Name | Type | Description |
| --- | --- | --- |
|`cid`|`int`|The integer competition ID|
|`closed`|`boolean`|`true` if administrators are the only users that can register participants, `false` otherwise|
|`length`|`int`|The length of the competition, in seconds|
|`name`|`String`|The name of this competition|
|`registered`|`boolean`|`true` if the current user is registered for this competition, `false` otherwise|
|`startTime`|`int`|The time the competition starts, in seconds since the Unix epoch|

### The Competition Problems Object

The Competition Problems object represents the collection of problems that appear
in a particular competition. It consists of a mapping of problem letters
(i.e. "A" for the first problem, "B" for the second, etc.) to a Competition Problem
object, described below:

__Competition Problem:__

| Name | Type | Description |
| --- | --- | --- |
|`name`|`String`|The full name of the problem|
|`pid`|`int`|An integer uniquely identifying the problem|
|`shortname`|`String`|A string that can be used to uniquely identify the problem|

__Example Competition Problems Object:__

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

In the above example, there are two problems in the competition. The first, problem
"A", is named "Islands in the Data Stream", with problem ID 23 and the shortname
"islands", while the second, problem "B" is named "Von Neumann's Fly", and has
problem ID 63 with the shortname "vonneumann".


### The Team Object

The teams in a competition will be described with an array of team objects, described below:

__Team:__

| Name | Type | Description |
| --- | --- | --- |
|`display_names`|`String[]`|The display name of every member of the team.|
|`name`|`String`|The name of the team.|
|`problemData`|`map[String]ProblemData`|A map of problem id to the status of every problem in the contest.|
|`users`|`String[]`|The usernames of everyone in the time. Note that this will be aligned with `display_names`, so `users[i]` is the username with the display name at `display_names[i]`.|


__Problem Data__:

| Name | Type | Description |
| --- | --- | --- |
| `label` | `String` | The label (A, B, C ... etc) of the problem within the contest|
| `status` | `String` | One of "correct", "incorrect", or "unattempted" |
| `submitCount` | `int` | The number of submissions this team has made for this problem |
| `submitTime` | `int` | The number of minutes into the contest that this team solved this problem, or 0 if it is unsolved.|

__Example Teams Data__:

```json
"teams": [
	{
		"display_names": [
			"Julius Caesar",
			"Pompey the Great",
			"Marcus Licinius Crassus"
		],
		"name": "First Triumvirate",
		"problemData": {
			"4": {
				"label": "A",
				"status": "incorrect",
				"submitCount": 1,
				"submitTime": 0
			},
			"11": {
				"label": "B",
				"status": "correct",
				"submitCount": 1,
				"submitTime": 40
			}
		},
		"users": [
			"juliusc",
			"pompey",
			"marcusc"
		]
	}, ...
]
```

The above example illustrates the first in a list of teams. The team is called the First Triumvirate, consiting of Julius Caesar (juliusc), Pompey the Great (pompey), and Marcus Licinius Crassus (marcusc). They have 1 incorrect submission for problem A (which has a pid of 4), and solved problem B (which has a pid of 11) on their first attempt 40 minutes into the contest.

__Competition Timing:__

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

Returns a JSON object of with detailed info for the competition with the ID matching `{competition_id}`
in the route.

| Name | Type | Description |
| --- | --- | --- |
|`compProblems`|[Competition Problems](#the-competition-problems-object)|Data on all problems in the competition.|
|`competition`|[Competition Info](#the-competition-object)|Basic info about the competition.|
|`teams`|[Team Info](#the-team-object)|Data for all teams in the competition.|

__Example use:__

`GET /api/competitions/1`

__Example response:__

```json
{
  "data": {
    "compProblems": {
      "A": {
        "name": "The Ides of March",
        "pid": 65,
        "shortname": "idesofmarch"
      },
      "B": {
        "name": "Rubicon",
        "pid": 49,
        "shortname": "rubicon"
      }
    },
    "competition": {
      "cid": 1,
      "closed": false,
      "length": 18000,
      "name": "Roman Mock",
      "registered": false,
      "startTime": 1413050400
    },
    "teams": [
      {
        "display_names": [
          "Julius Caesar",
          "Pompey the Great",
          "Marcus Licinus Crassus"
        ],
        "name": "First Trirumvirate",
        "problemData": {
          "4": {
            "label": "A",
            "status": "incorrect",
            "submitCount": 1,
            "submitTime": 0
          },
          "11": {
            "label": "B",
            "status": "correct",
            "submitCount": 1,
            "submitTime": 40
          }
        },
        "users": [
          "juliusc",
          "pompey",
          "marcusc"
        ]
      }
    ]
  },
  "status": 200
}
```

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
{

```json
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

### Get All Teams in a Competition
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
