import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/switchMap';

import { UserService } from '../user.service';
import { CompetitionService } from '../competition.service';
import { ProblemService } from '../problem.service';

import { Competition, CompetitionProblem, CompetitionTeam, TeamProblemData } from '../models/competition';
import { UserData } from '../models/user';
import { Problem } from '../models/problem';

@Component({
  selector: 'app-edit-competition',
  templateUrl: './edit-competition.component.html',
  styleUrls: ['./edit-competition.component.css']
})
export class EditCompetitionComponent implements OnInit {

  public title = '';
  public userData: UserData = new UserData();
  public competition: Competition = new Competition();
  public problems: Problem[] = [];
  public selectedProblems: Problem[] = [];
  public selected: Problem;

  public responseMessage = '';
  public startTime = '';
  public compLength = '';
  public createComp = false;
  public responseFailed = false;
  public responseSuccess = false;
  public formDisabled = false;

  constructor(private _router: Router, private _activeRoute: ActivatedRoute,
              private _userService: UserService, private _competitionService: CompetitionService,
              private _problemService: ProblemService) {
    this._userService.userData$.subscribe(newData => {
      this.userData = newData;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if ((this._router.url.startsWith('/competition')
            && this._router.url.endsWith('/edit'))
            || (this._router.url === '/competitions/create')) {
          this._router.navigate(['404']);
        }
      }
    });
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
    this.startTime = this.defaultStartTime();
    this.compLength = '3:00';
    if (this._router.url === '/competitions/create') {
      this.title = 'Create Competition';
      this.createComp = true;
      this._problemService.getAllProblems().then(problems => {
        this.problems = problems;
      });
    } else {
      this.title = 'Edit Competition';
      this.createComp = false;
      this._activeRoute.params.switchMap((params: Params) => params['cid']
          ? this._competitionService.getCompetition(+params['cid'])
          : Observable.of<Competition>(undefined)).subscribe(competition => {
        if (competition === undefined) {
          this._router.navigate(['404']);
        } else {
          this.competition = competition;
          const date = new Date(competition.startTime * 1000);
          let startTime = '';
          startTime += (date.getMonth() + 1) + '-' + date.getDate() +
                  '-' + date.getFullYear();
          startTime += ' ' + date.getHours() + ':';
          if (date.getMinutes() < 10) {
              startTime += '0';
          }
          startTime += date.getMinutes();

          this.startTime = startTime;
          this.compLength = (competition.length / 3600) + ':';
          if (competition.length % 60 < 10) {
              this.compLength += '0';
          }
          this.compLength += competition.length % 60;
          this._problemService.getAllProblems().then(problems => {
            this.problems = problems;
            const labelKeys = Object.keys(competition.compProblems);
            for (let i = 0; i < labelKeys.length; i++) {
              for (const problem of problems) {
                if (problem.pid === competition.compProblems[labelKeys[i]].pid) {
                  this.selectedProblems.push(problem);
                  break;
                }
              }
            }
          });
        }
      });
    }
  }

  defaultStartTime() {
    const date = new Date(Date.now() + (24 * 3600 * 1000));
    return (date.getMonth() + 1) + '-' + date.getDate() + '-' +
      (date.getFullYear()) + ' 10:00';
  }

  addProblem(problem: Problem) {
    this.selectedProblems.push(problem);
  }

  removeProblem(problem: Problem) {
    this.selectedProblems.splice(this.selectedProblems.indexOf(problem), 1);
  }

  getStartTimeSeconds(): number {
    // get the string from scope and split it into its parts
    const parts = this.startTime.split(' ');

    // The parts consists of the day and time
    const day = parts[0].split('-');
    const time = parts[1].split(':');

    // Parse the parts into Numbers so that we can get the time from it.
    const month = parseInt(day[0], 10) - 1;
    const dayOfMonth = parseInt(day[1], 10);
    const year = parseInt(day[2], 10);
    const hourOfDay = parseInt(time[0], 10);
    const minute = parseInt(time[1], 10);

    // return the seconds of the date that the user entered.
    return new Date(year, month, dayOfMonth, hourOfDay, minute).valueOf() /
      1000; // to seconds instead of milliseconds.
  }

  getLengthSeconds() {
    const parts = this.compLength.split(':');
    return parseInt(parts[0], 10) * 3600 + parseInt(parts[1], 10) * 60;
  }

  createCompetition(): void {
    this.formDisabled = true;
    this.competition.startTime = this.getStartTimeSeconds();
    this.competition.length = this.getLengthSeconds();
    if (this.createComp) {
      this._competitionService.createCompetition(this.competition, this.selectedProblems).then(competition => {
        this.formDisabled = false;
        if (competition === undefined) {
          this.responseMessage = `Failed to ${this.createComp ? 'create' : 'update'} the competition!`;
          this.responseFailed = true;
          this.responseSuccess = false;
        } else {
          this.responseMessage = `Successfully ${this.createComp ? 'created' : 'updated'} the competition!`;
          this.responseFailed = false;
          this.responseSuccess = true;
        }
      });
    } else {
      this._competitionService.updateCompetition(this.competition, this.selectedProblems).then(competition => {
        this.formDisabled = false;
        if (competition === undefined) {
          this.responseMessage = `Failed to ${this.createComp ? 'create' : 'update'} the competition!`;
          this.responseFailed = true;
          this.responseSuccess = false;
        } else {
          this.responseMessage = `Successfully ${this.createComp ? 'created' : 'updated'} the competition!`;
          this.responseFailed = false;
          this.responseSuccess = true;
        }
      });
    }
  }
}
