# AUACM API Documentation

The Auburn ACM website uses a RESTful api for managing blog posts, problems,
submissions, etc. This document is intended to serve as a one stop shop for the
method arguments and return values.

---

## Problem Management

### Create a New Problem
***This method requires being logged in as an administrator***

__URL:__ /api/problems/

__Method:__ POST

__Form Arguments:__

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
| Input Files | __Required__ | 'in_file' | Zipped (.zip) directory of all input files |
| Output Files | __Required__ | 'out_file' | Zipped (.zip) directory of all output files |
| Solution File | __Required__ | 'sol_file' | Solution program (not zipped) |

For __Sample Cases__, must be a string JSON array of mappings `input` and `output`.
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

If any of the required fields are not supplied, the API will return with status
code 400. If successful, the API will return a JSON object representation of
the newly created problem.


## Get Data on a Problem

__URL:__ /api/problems/{identifier}

 _Note:_ A problem's identifier can be its numeric id (i.e. 1) or it's
 alphanumeric "shortname" (i.e. blackvienna)

__Method:__ GET

Returns detailed data on a specific problem as a JSON object.
Specifically, the method returns
  * __Problem ID__
  * __Full Name__
  * __Short Name__
  * __Competition Appearance__
  * __Difficulty Rating__
  * __AUACM Competition Release__
  * __Description__
  * __Input Description__
  * __Output Description__
  * __Sample Cases Array__


## Get Data on all problems

__URL:__ /api/problems/

__Method:__ GET

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
