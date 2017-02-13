import { Injectable } from '@angular/core';
import { Http, Request, Response } from '@angular/http';
import { Subject } from 'rxjs';

import { ProblemService } from './problem.service';
import { Problem } from './models/problem';

@Injectable()
export class SubmissionService {

  constructor(private _problemService: ProblemService) { }

  /**
   * Call this to submit files! ^-^
   */
  submit(file: File, problem: Problem, pythonVersion: string) : Promise<boolean> {
    var bernitize = file.name.includes('bern');
    // TODO Handle the bernization of the problem

    var submitForm = new FormData();
    submitForm.append('pid', problem.pid);
    submitForm.append('file', file);
    if (file.name.endsWith('.py')) {
      submitForm.append('python', pythonVersion);
    }

    // TODO Finish it
    return undefined;
  };

  
}
