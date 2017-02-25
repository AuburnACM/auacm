import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import 'rxjs/add/operator/switchMap';
import { Observable } from 'rxjs/Observable';

import { UserService } from '../user.service';
import { ProblemService } from '../problem.service';
import { CompetitionService } from '../competition.service';

import { UserData } from '../models/user';
import { Problem, SampleCase } from '../models/problem';
import { Competition } from '../models/competition';

declare var $: any;

@Component({
  selector: 'app-edit-problem',
  templateUrl: './edit-problem.component.html',
  styleUrls: ['./edit-problem.component.css']
})
export class EditProblemComponent implements OnInit {

  public problem: Problem = new Problem();
  public competitions: Competition[] = [];
  public userData: UserData = new UserData();
  public updateFailed = false;
  public updateSuccess = false;
  public disableForm = true;
  public createProblem = false;
  public showCaseList = true;
  public failMessage = `Failed to ${this.createProblem ? 'create' : 'update'} the problem!`;
  public successMessage = `Problem ${this.createProblem ? 'created' : 'updated'} successfully!`;
  public submitButton = this.createProblem ? 'Create' : 'Update';

  // Files
  public judgeInput: File;
  public judgeOutput: File;
  public judgeSolution: File;

  public addCaseErrorMsg = 'Please fill in the previous case before adding another';
  public addCaseError = false;

  constructor(private _userService: UserService, private _problemService: ProblemService,
              private _activeRoute: ActivatedRoute, private _location: Location,
              private _competitionService: CompetitionService, private _router: Router) {
    this._userService.userData$.subscribe(userData => {
      this.userData = userData;
      if (this._router.url.startsWith('/problem') && (this._router.url.endsWith('/edit') || this._router.url.endsWith('/create'))) {
        this._router.navigate(['404']);
      }
      this.disableForm = this.userData.isAdmin ? false : true;
    });
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
    this.disableForm = this.userData.isAdmin ? false : true;
    this._activeRoute.params.switchMap((params: Params) => params['pid']
        ? this._problemService.getProblemByPid(+params['pid'])
        : Observable.of(undefined)).subscribe(problem => {
      if (problem === undefined) {
        this.createProblem = true;
      } else {
        this.problem = problem;
      }
    });
    $(document).ready(() => {
        $('[data-toggle="tooltip"]').tooltip();
    });
    this._competitionService.getAllCompetitions().then(competitions => {
      this.competitions = competitions['upcoming'];
    });
  }

  save() {
    if (this.createProblem) {
      this._problemService.createProblem(this.problem, this.judgeInput, this.judgeOutput, this.judgeSolution).then(problem => {
        if (problem === undefined) {
          this.updateFailed = true;
          this.updateSuccess = false;
        } else {
          this.updateFailed = false;
          this.updateSuccess = true;
          this.problem = problem;
        }
      });
    } else {
      this._problemService.updateProblem(this.problem, this.judgeInput, this.judgeOutput, this.judgeSolution).then(problem => {
        if (problem === undefined) {
          this.updateFailed = true;
          this.updateSuccess = false;
        } else {
          this.updateFailed = false;
          this.updateSuccess = true;
          this.problem = problem;
        }
      });
    }
  }

  back() {
    this._location.back();
  }

  addCase() {
    const len = this.problem.sampleCases.length;
    if (this.problem.sampleCases.length > 0 && (this.problem.sampleCases[len - 1].input.length === 0
        && this.problem.sampleCases[len - 1].output.length === 0)) {
      this.addCaseError = true;
      return;
    }
    this.showCaseList = false;
    this.addCaseError = false;
    this.problem.sampleCases.push(new SampleCase());
    this.showCaseList = true;
  }

  removeCase() {
    this.showCaseList = false;
    this.problem.sampleCases.pop();
    this.showCaseList = true;
  }
}
