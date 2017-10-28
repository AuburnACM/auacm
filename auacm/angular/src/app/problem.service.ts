import { Injectable } from '@angular/core';
import { Http, Request, Response } from '@angular/http';
import { Subject } from 'rxjs/Subject';

import { Problem, SampleCase } from './models/problem';
import { HttpClient } from '@angular/common/http';
import { DataWrapper } from 'app/models/datawrapper';

@Injectable()
export class ProblemService {

  constructor(private _http: Http, private _httpClient: HttpClient) { }

  // Returns a copy of itself if successful
  createProblem(problem: Problem, judgeInput: File, judgeOutput: File, judgeSolution: File): Promise<Problem> {
    const requestForm = new FormData();
    requestForm.append('pid', `${problem.pid}`);
    requestForm.append('name', problem.name);
    requestForm.append('description', problem.description);
    requestForm.append('inputDesc', problem.inputDesc);
    requestForm.append('outputDesc', problem.outputDesc);
    requestForm.append('cases', JSON.stringify(problem.sampleCases));
    if (problem.difficulty >= 0) {
      requestForm.append('difficulty', `${problem.difficulty}`);
    }
    if (judgeInput !== undefined) {
      requestForm.append('inFile', judgeInput);
    }
    if (judgeOutput !== undefined) {
      requestForm.append('outFile', judgeOutput);
    }
    if (judgeSolution !== undefined) {
      requestForm.append('solFile', judgeSolution);
    }
    requestForm.append('appearedIn', problem.appeared);
    requestForm.append('compRelease', `${problem.compRelease}`);

    return new Promise((resolve, reject) => {
      const xmlSubmit = new XMLHttpRequest();
      xmlSubmit.onreadystatechange = () => {
        if (xmlSubmit.readyState === 4) {
          if (xmlSubmit.status === 200) {
            const data = JSON.parse(xmlSubmit.response).data;
            const newProblem = new Problem();
            newProblem.added = data.added;
            newProblem.appeared = data.appeared;
            newProblem.compRelease = data.compRelease;
            newProblem.description = data.description;
            newProblem.difficulty = data.difficulty;
            newProblem.inputDesc = (data.inputDesc === undefined || data.inputDesc === null) ? '' : data.inputDesc;
            newProblem.name = data.name;
            newProblem.outputDesc = (data.outputDesc === undefined || data.outputDesc === null) ? '' : data.outputDesc;
            newProblem.pid = data.pid;
            newProblem.sampleCases = data.cases;
            newProblem.shortName = data.shortname;
            resolve(newProblem);
          } else {
            resolve(undefined);
          }
        }
      };
      xmlSubmit.open('POST', window.location.protocol + '//' + window.location.host + '/api/problems/', true);
      xmlSubmit.send(requestForm);
    });
  }

  updateProblem(problem: Problem, judgeInput: File, judgeOutput: File, judgeSolution: File): Promise<Problem> {
    const requestForm = new FormData();
    requestForm.append('pid', `${problem.pid}`);
    requestForm.append('name', problem.name);
    requestForm.append('description', problem.description);
    requestForm.append('inputDesc', problem.inputDesc);
    requestForm.append('outputDesc', problem.outputDesc);
    requestForm.append('cases', JSON.stringify(problem.sampleCases));
    if (problem.difficulty >= 0) {
      requestForm.append('difficulty', `${problem.difficulty}`);
    }
    if (judgeInput !== undefined) {
      requestForm.append('in_file', judgeInput);
    }
    if (judgeOutput !== undefined) {
      requestForm.append('out_file', judgeOutput);
    }
    if (judgeSolution !== undefined) {
      requestForm.append('sol_file', judgeSolution);
    }
    requestForm.append('appeared_in', problem.appeared);
    requestForm.append('compRelease', `${problem.compRelease}`);

    return new Promise((resolve, reject) => {
      const xmlSubmit = new XMLHttpRequest();
      xmlSubmit.onreadystatechange = () => {
        if (xmlSubmit.readyState === 4) {
          if (xmlSubmit.status === 200) {
            const data = JSON.parse(xmlSubmit.response).data;
            const newProblem = new Problem();
            newProblem.added = data.added;
            newProblem.appeared = data.appeared;
            newProblem.compRelease = data.compRelease;
            newProblem.description = data.description;
            newProblem.difficulty = data.difficulty;
            newProblem.inputDesc = (data.inputDesc === undefined || data.inputDesc === null) ? '' : data.inputDesc;
            newProblem.name = data.name;
            newProblem.outputDesc = (data.outputDesc === undefined || data.outputDesc === null) ? '' : data.outputDesc;
            newProblem.pid = data.pid;
            newProblem.sampleCases = data.cases;
            newProblem.shortName = data.shortname;
            resolve(newProblem);
          } else {
            resolve(undefined);
          }
        }
      };
      xmlSubmit.open('PUT', window.location.protocol + '//' + window.location.host + '/api/problems/' + problem.pid, true);
      xmlSubmit.send(requestForm);
    });
  }

  getProblemByPid(identifier: number): Promise<Problem> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<Problem>>(`/api/problems/${identifier}`).subscribe(data => {
        const problem = new Problem().deserialize(data.data);
        resolve(problem);
      }, (err: Response) => {
        resolve(new Problem());
      });
    });
  }

  getProblemByShortName(identifier: string): Promise<Problem> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<Problem>>(`/api/problems/${identifier}`).subscribe(data => {
        const problem = new Problem().deserialize(data.data);
        resolve(problem);
      }, (err: Response) => {
        resolve(undefined);
      });
    });
  }

  getAllProblems(): Promise<Problem[]> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<Problem[]>>('/api/problems').subscribe(data => {
        const problems = <Problem[]> [];
        const list = data.data;
        for (const problem of list) {
          problems.push(new Problem().deserialize(problem));
        }
        resolve(problems);
      }, (err: Response) => {
        console.log(err);
        resolve([]);
      });
    });
  }
}
