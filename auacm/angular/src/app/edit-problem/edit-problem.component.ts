import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import 'rxjs/add/operator/switchMap';
import { Observable } from 'rxjs'; 

import { AuthService } from '../auth.service';
import { ProblemService } from '../problem.service';
import { CompetitionService } from '../competition.service';

import { UserData } from '../models/user';
import { Problem, SampleCase } from '../models/problem';
import { Competition } from '../models/competition';

declare var $:any;

@Component({
  selector: 'app-edit-problem',
  templateUrl: './edit-problem.component.html',
  styleUrls: ['./edit-problem.component.css']
})
export class EditProblemComponent implements OnInit {

  private problem: Problem = new Problem();
  private competitions: Competition[] = [];

  private userData: UserData = new UserData();

  private updateFailed: boolean = false;
  private updateSuccess: boolean = false;
  private disableForm: boolean = true;
  private createProblem: boolean = false;
  private showCaseList: boolean = true;

  private failMessage: string = `Failed to ${this.createProblem ? 'create' : 'update'} the problem!`
  private successMessage: string = `Problem ${this.createProblem ? 'created' : 'updated'} successfully!`
  private submitButton: string = this.createProblem ? 'Create' : 'Update';

  // Files
  private judgeInput: File;
  private judgeOutput: File;
  private judgeSolution: File;

  private addCaseErrorMsg: string = 'Please fill in the previous case before adding another';
  private addCaseError: boolean = false;

  constructor(private _authService: AuthService, private _problemService: ProblemService,
              private _activeRoute: ActivatedRoute, private _location: Location,
              private _competitionService: CompetitionService, private _router: Router) {
    this._authService.userData$.subscribe(userData => {
      this.userData = userData;
      if (this._router.url.startsWith('/problem') && (this._router.url.endsWith('/edit') || this._router.url.endsWith('/create'))) {
        this._router.navigate(['404']);
      }
      this.disableForm = this.userData.isAdmin ? false : true;
    });
  }

  ngOnInit() {
    this.userData = this._authService.getUserData();
    this.disableForm = this.userData.isAdmin ? false : true;
    this._activeRoute.params.switchMap((params: Params) => params['pid']
        ? this._problemService.getProblemByPid(+params['pid']) 
        : Observable.of(undefined)).subscribe(problem => {
      if (problem == undefined) {
        this.createProblem = true;
      } else {
        this.problem = problem;
      }
    });
    $(document).ready(function(){
        $('[data-toggle="tooltip"]').tooltip(); 
    });
    this._competitionService.getAllCompetitions().then(competitions => {
      this.competitions = competitions['upcoming'];
    })
  };

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
  };

  back() {
    this._location.back();
  };

  addCase() {
    var len = this.problem.sampleCases.length;
    if (this.problem.sampleCases.length > 0 && (this.problem.sampleCases[len-1].input.length === 0 && this.problem.sampleCases[len-1].output.length === 0)) {
      console.log('Previous case not filled in!');
      this.addCaseError = true;
      return;
    };
    this.showCaseList = false;
    this.addCaseError = false;
    this.problem.sampleCases.push(new SampleCase());
    console.log(this.problem.sampleCases);
    this.showCaseList = true;
  };

  removeCase() {
    this.showCaseList = false;
    this.problem.sampleCases.pop();
    console.log(this.problem.sampleCases);
    this.showCaseList = true;
  };
}