import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { Observable } from 'rxjs';
import 'rxjs/add/operator/switchMap';

import { AuthService } from '../auth.service';
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

  private title: string = ""; 
  private userData: UserData = new UserData();
  private competition: Competition = new Competition();
  private problems: Problem[] = [];
  private selectedProblems: Problem[] = [];
  private selected: Problem;

  private responseMessage: string = '';
  private startTime: string = '';
  private compLength: string = '';
  private createComp: boolean = false;
  private responseFailed: boolean = false;
  private responseSuccess: boolean = false;
  private formDisabled: boolean = false;

  constructor(private _router: Router, private _activeRoute: ActivatedRoute,
              private _authService: AuthService, private _competitionService: CompetitionService, 
              private _problemService: ProblemService) {
    this._authService.userData$.subscribe(newData => {
      this.userData = newData;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if ((this._router.url.startsWith('/competition') && this._router.url.endsWith('/edit')) || (this._router.url === '/competitions/create')) {
          this._router.navigate(['404']);
        }
      }
    });
  }

  ngOnInit() {
    this.userData = this._authService.getUserData();
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
      this._activeRoute.params.switchMap((params: Params) => params['cid'] ? this._competitionService.getCompetition(+params['cid']) : Observable.of<Competition>(undefined)).subscribe(competition => {
        if (competition === undefined) {
          this._router.navigate(['404']);
        } else {
          this.competition = competition;
          var date = new Date(competition.startTime * 1000);
          var startTime = '';
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
            for (var label in competition.compProblems) {
              for (var problem of problems) {
                if (problem.pid === competition.compProblems[label].pid) {
                  this.selectedProblems.push(problem);
                  break;
                }
              }
            }
          });
        }
      });
    }
  };

  defaultStartTime() {
    var date = new Date(Date.now() + (24 * 3600 * 1000));
    return (date.getMonth() + 1) + "-" + date.getDate() + "-" +
      (date.getFullYear()) + " 10:00";
  };

  addProblem(problem: Problem) {
    this.selectedProblems.push(problem);
  };

  removeProblem(problem: Problem) {
    this.selectedProblems.splice(this.selectedProblems.indexOf(problem), 1);
  };

  getStartTimeSeconds() {
    // get the string from scope and split it into its parts
    var parts = this.startTime.split(' ');

    // The parts consists of the day and time
    var day = parts[0].split('-');
    var time = parts[1].split(':');

    // Parse the parts into Numbers so that we can get the time from it.
    var month = parseInt(day[0]) - 1;
    var dayOfMonth = parseInt(day[1]);
    var year = parseInt(day[2]);
    var hourOfDay = parseInt(time[0]);
    var minute = parseInt(time[1]);

    // return the seconds of the date that the user entered.
    return new Date(year, month, dayOfMonth, hourOfDay, minute).valueOf() /
      1000; // to seconds instead of milliseconds.
  };

  getLengthSeconds() {
    var parts = this.compLength.split(':');
    return parseInt(parts[0]) * 3600 + parseInt(parts[1]) * 60;
  };

  createCompetition(): void {
    this.formDisabled = true;
    this.competition.startTime = this.getStartTimeSeconds();
    this.competition.length = this.getLengthSeconds();
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
  }
}
