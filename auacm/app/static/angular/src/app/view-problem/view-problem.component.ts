import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Converter } from 'showdown';

import { Observable } from 'rxjs';
import 'rxjs/add/operator/switchMap';

import { AuthService } from '../auth.service';
import { ProblemService } from '../problem.service';

import { UserData } from '../models/user';
import { Problem } from '../models/problem';

@Component({
  selector: 'app-view-problem',
  templateUrl: './view-problem.component.html',
  styleUrls: ['./view-problem.component.css']
})
export class ViewProblemComponent implements OnInit {

  converter: Converter = new Converter();

  userData: UserData = new UserData();

  problem: Problem = new Problem();

  constructor(private _router: Router, private _authService: AuthService,
              private _problemService: ProblemService, private _activeRoute: ActivatedRoute) {
    this._authService.userData$.subscribe(newData => {
      this.userData = newData;
    });
  }

  ngOnInit() {
    this.userData = this._authService.getUserData();
    this._activeRoute.params.switchMap((params: Params) => params['shortName'] ? this._problemService.getProblemByShortName(params['shortName']) : Observable.of(undefined)).subscribe(problem => {
      if (problem !== undefined) {
        this.problem = problem;
      } else {
        this._router.navigate(['/404']);
      }
    });
  }

}
