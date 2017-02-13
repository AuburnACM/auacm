import { Injectable } from '@angular/core';
import { Http, Request, Response } from '@angular/http';
import { Subject } from 'rxjs';

import { Problem, SampleCase } from './models/problem';

@Injectable()
export class ProblemService {

  constructor(private _http: Http) { }

  // Returns a copy of itself if successful
  createProblem(problem: Problem) : Promise<Problem> {
    return undefined;
  };

  getProblemByPid(identifier: number) : Promise<Problem> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/problems/${identifier}`).subscribe((res: Response) => {
        var problem = new Problem();
        var data = res.json().data;
        if (res.status == 200) {
          problem.added = data.added;
          problem.appeared = data.appeared;
          problem.compRelease = data.comp_release;
          problem.description = data.description;
          problem.difficulty = data.difficulty;
          problem.inputDesc = (data.input_desc === undefined || data.input_desc === null) ? "" : data.input_desc;
          problem.name = data.name;
          problem.outputDesc = (data.output_desc === undefined || data.output_desc === null) ? "" : data.output_desc;
          problem.pid = data.pid;
          problem.sampleCases = data.sample_cases;
          problem.shortName = data.shortname;
        }
        resolve(problem);
      }, (err: Response) => {
        resolve(new Problem());
      });
    });
  };

  getProblemByShortName(identifier: string) : Promise<Problem> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/problems/${identifier}`).subscribe((res: Response) => {
        var problem = new Problem();
        var data = res.json().data;
        if (res.status == 200) {
          problem.added = data.added;
          problem.appeared = data.appeared;
          problem.compRelease = data.comp_release;
          problem.description = data.description;
          problem.difficulty = data.difficulty;
          problem.inputDesc = (data.input_desc === undefined || data.input_desc === null) ? "" : data.input_desc;
          problem.name = data.name;
          problem.outputDesc = (data.output_desc === undefined || data.output_desc === null) ? "" : data.output_desc;
          problem.pid = data.pid;
          problem.sampleCases = data.sample_cases;
          problem.shortName = data.shortname;
        }
        resolve(problem);
      }, (err: Response) => {
        resolve(undefined);
      });
    });
  };

  getAllProblems() : Promise<Problem[]> {
    return new Promise((resolve, reject) => {
      this._http.get('/api/problems').subscribe((res: Response) => {
        var problems = [];
        if (res.status === 200) {
          var data = res.json().data;
          for (var i = 0; i < data.length; i++) {
            var tempProblem = new Problem();
            tempProblem.added = data[i].added;
            tempProblem.appeared = data[i].appeared;
            tempProblem.compRelease = data[i].comp_release;
            tempProblem.difficulty = data[i].difficulty;
            tempProblem.name = data[i].name;
            tempProblem.pid = data[i].pid;
            tempProblem.shortName = data[i].shortname;
            tempProblem.solved = data[i].solved;
            tempProblem.url = data[i].url;
            problems.push(tempProblem);
          }
        }
        resolve(problems);
      }, (err: Response) => {
        console.log(err);
        resolve([]);
      });
    });
  };

  deleteProblemByPid(identifier: number) : Promise<number> {
    return undefined;
  };

  deleteProblemByShortName(identifier: string) : Promise<number> {
    return undefined;
  };

  updateProblemWithPid(problem: Problem) : Promise<Problem> {
    return undefined;
  };

  updateProblemWithShortName(problem: Problem) : Promise<Problem> {
    return undefined;
  };
}
