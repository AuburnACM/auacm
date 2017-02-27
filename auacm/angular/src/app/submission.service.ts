import { Injectable } from '@angular/core';
import { Http, Request, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { WebsocketService } from './websocket.service';

import { Problem } from './models/problem';
import { RecentSubmission } from './models/submission';
import { UserData } from './models/user';

@Injectable()
export class SubmissionService {

  private STATUS_NAMES: any = {
    compile: 'Compilation Error',
    runtime: 'Runtime Error',
    running: 'Running',
    timeout: 'Time Limit Exceeded',
    incorrect: 'Incorrect',
    correct: 'Correct'
  };

  private submissionsSource: Subject<RecentSubmission[]> = new Subject<RecentSubmission[]>();
  private recentSubmissions: RecentSubmission[] = [];
  recentSubmitsData$: Observable<RecentSubmission[]> = this.submissionsSource.asObservable();

  constructor(private _http: Http, private _websocketService: WebsocketService) {
    this._websocketService.connect(window.location.host + '/websocket').subscribe(data => {
        // Make sure the websocket eventType is for updating a submission
        const response = JSON.parse(data.data);
        if (response.eventType === 'status') {
          const responseData = response.data;
          for (let i = 0; i < this.recentSubmissions.length; i++) {
            if (this.recentSubmissions[i].jobId === responseData.submissionId) {
              this.recentSubmissions[i].status = responseData.status;
              this.recentSubmissions[i].statusDescription = this.STATUS_NAMES[responseData.status];
              break;
            }
          }
        }
    });
  }

  /**
   * Call this to submit files! ^-^
   */
  submit(file: File, problem: Problem, pythonVersion: string, user: UserData): Promise<boolean> {
    const bernitize = file.name.includes('bern');
    // TODO Handle the bernization of the problem

    const submitForm = new FormData();
    submitForm.append('pid', problem.pid);
    submitForm.append('file', file);
    if (file.name.endsWith('.py')) {
      submitForm.append('python', pythonVersion);
    }
    const submission = new RecentSubmission();
    submission.pid = problem.pid;
    submission.fileName = file.name;
    submission.status = 'uploading';
    submission.statusDescription = 'Uploading';
    submission.username = user.username;
    submission.fileType = file.name.split('.').pop();

    // Lets push this onto the current recent submits
    // and pop the oldest one off the list. Then we need to push our changes
    // using the source object
    this.recentSubmissions.splice(0, 0, submission);
    this.recentSubmissions.pop();
    this.submissionsSource.next(this.recentSubmissions);

    return new Promise((resolve, reject) => {
      const xmlSubmit = new XMLHttpRequest();
      submitForm.append('files', file, 'file');
      xmlSubmit.onreadystatechange = () => {
        if (xmlSubmit.readyState === 4) {
          if (xmlSubmit.status === 200) {
            submission.status = 'compiling';
            submission.statusDescription = 'Compiling';
            const data = JSON.parse(xmlSubmit.response);
            submission.jobId = data.data.submissionId;
            resolve(true);
          } else {
            resolve(false);
          }
        }
      };
      xmlSubmit.open('POST', window.location.protocol + '//' + window.location.host + '/api/submit', true);
      xmlSubmit.send(submitForm);
    });
  };

  getRecentSubmits(username: string, amount: number): Promise<RecentSubmission[]> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/submit?username=${username}&limit=${amount}`).subscribe((res: Response) => {
        const submits = [];
        if (res.status === 200) {
          const data = res.json().data;
          for (let i = 0; i < data.length; i++) {
            const recentSubmit = new RecentSubmission();
            recentSubmit.fileType = data[i].file_type;
            recentSubmit.jobId = data[i].job_id;
            recentSubmit.pid = data[i].pid;
            if (data[i].status === 'good') {
              recentSubmit.status = 'correct';
            } else if (data[i].status === 'wrong') {
              recentSubmit.status = 'incorrect';
            } else {
              recentSubmit.status = data[i].status;
            }
            recentSubmit.submitTime = data[i].submit_time;
            recentSubmit.username = data[i].username;
            recentSubmit.statusDescription = this.STATUS_NAMES[recentSubmit.status];
            submits.push(recentSubmit);
          }
        }
        resolve(submits);
      }, (err: Response) => {
        console.log('Failed to get the most recent submits for ' + username + '.');
        resolve([]);
      });
    });
  }

  refreshSubmits(username: string, amount: number): void {
    this.getRecentSubmits(username, amount).then(data => {
      this.recentSubmissions = data;
      this.submissionsSource.next(this.recentSubmissions);
    });
  }
}
