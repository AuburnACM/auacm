import { Injectable } from '@angular/core';
import { Http, Request, Response } from '@angular/http';
import { Subject } from 'rxjs/Subject';

import { Problem, SampleCase } from './models/problem';

@Injectable()
export class ProblemService {

  constructor(private _http: Http) { }

  // Returns a copy of itself if successful
  createProblem(problem: Problem, judgeInput: File, judgeOutput: File, judgeSolution: File): Promise<Problem> {
    const requestForm = new FormData();
    requestForm.append('pid', problem.pid);
    requestForm.append('name', problem.name);
    requestForm.append('description', problem.description);
    requestForm.append('input_desc', problem.inputDesc);
    requestForm.append('output_desc', problem.outputDesc);
    requestForm.append('cases', JSON.stringify(problem.sampleCases));
    if (problem.difficulty >= 0) {
      requestForm.append('difficulty', problem.difficulty);
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
    requestForm.append('comp_release', problem.compRelease);

    return new Promise((resolve, reject) => {
      const xmlSubmit = new XMLHttpRequest();
      xmlSubmit.onreadystatechange = () => {
        if (xmlSubmit.readyState === 4) {
          if (xmlSubmit.status === 200) {
            const data = JSON.parse(xmlSubmit.response).data;
            const newProblem = new Problem();
            newProblem.added = data.added;
            newProblem.appeared = data.appeared;
            newProblem.compRelease = data.comp_release;
            newProblem.description = data.description;
            newProblem.difficulty = data.difficulty;
            newProblem.inputDesc = (data.input_desc === undefined || data.input_desc === null) ? '' : data.input_desc;
            newProblem.name = data.name;
            newProblem.outputDesc = (data.output_desc === undefined || data.output_desc === null) ? '' : data.output_desc;
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
    requestForm.append('pid', problem.pid);
    requestForm.append('name', problem.name);
    requestForm.append('description', problem.description);
    requestForm.append('input_desc', problem.inputDesc);
    requestForm.append('output_desc', problem.outputDesc);
    requestForm.append('cases', JSON.stringify(problem.sampleCases));
    if (problem.difficulty >= 0) {
      requestForm.append('difficulty', problem.difficulty);
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
    requestForm.append('comp_release', problem.compRelease);

    return new Promise((resolve, reject) => {
      const xmlSubmit = new XMLHttpRequest();
      xmlSubmit.onreadystatechange = () => {
        if (xmlSubmit.readyState === 4) {
          if (xmlSubmit.status === 200) {
            const data = JSON.parse(xmlSubmit.response).data;
            const newProblem = new Problem();
            newProblem.added = data.added;
            newProblem.appeared = data.appeared;
            newProblem.compRelease = data.comp_release;
            newProblem.description = data.description;
            newProblem.difficulty = data.difficulty;
            newProblem.inputDesc = (data.input_desc === undefined || data.input_desc === null) ? '' : data.input_desc;
            newProblem.name = data.name;
            newProblem.outputDesc = (data.output_desc === undefined || data.output_desc === null) ? '' : data.output_desc;
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
      this._http.get(`/api/problems/${identifier}`).subscribe((res: Response) => {
        const problem = new Problem();
        const data = res.json().data;
        if (res.status === 200) {
          problem.added = data.added;
          problem.appeared = data.appeared;
          problem.compRelease = data.comp_release;
          problem.description = data.description;
          problem.difficulty = data.difficulty;
          problem.inputDesc = (data.input_desc === undefined || data.input_desc === null) ? '' : data.input_desc;
          problem.name = data.name;
          problem.outputDesc = (data.output_desc === undefined || data.output_desc === null) ? '' : data.output_desc;
          problem.pid = data.pid;
          problem.sampleCases = data.sample_cases;
          problem.shortName = data.shortname;
        }
        resolve(problem);
      }, (err: Response) => {
        resolve(new Problem());
      });
    });
  }

  getProblemByShortName(identifier: string): Promise<Problem> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/problems/${identifier}`).subscribe((res: Response) => {
        const problem = new Problem();
        const data = res.json().data;
        if (res.status === 200) {
          problem.added = data.added;
          problem.appeared = data.appeared;
          problem.compRelease = data.comp_release;
          problem.description = (data.description === undefined || data.description === null) ? '' : data.description;
          problem.difficulty = data.difficulty;
          problem.inputDesc = (data.input_desc === undefined || data.input_desc === null) ? '' : data.input_desc;
          problem.name = data.name;
          problem.outputDesc = (data.output_desc === undefined || data.output_desc === null) ? '' : data.output_desc;
          problem.pid = data.pid;
          problem.sampleCases = data.sample_cases;
          problem.shortName = data.shortname;
        }
        resolve(problem);
      }, (err: Response) => {
        resolve(undefined);
      });
    });
  }

  getAllProblems(): Promise<Problem[]> {
    return new Promise((resolve, reject) => {
      this._http.get('/api/problems').subscribe((res: Response) => {
        const problems = [];
        if (res.status === 200) {
          const data = res.json().data;
          for (let i = 0; i < data.length; i++) {
            const tempProblem = new Problem();
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
  }
}
