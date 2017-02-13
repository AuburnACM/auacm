import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import 'rxjs/add/operator/switchMap';
import { Subject, Observable } from 'rxjs';

import { AuthService } from '../auth.service';
import { ProblemService } from '../problem.service';

import { UserData } from '../models/user';
import { Problem } from '../models/problem';

declare var $:any;

/**
 * This component is a little crazy. It uses some Angular magic to create searchable problems.
 */

@Component({
  selector: 'app-judge',
  templateUrl: './judge.component.html',
  styleUrls: ['./judge.component.css']
})
export class JudgeComponent implements OnInit {

  user: UserData;

  file: File = undefined;

  searchFilter: string = ""
  problem: Problem = new Problem();
  problems: Problem[] = [];
  problemsObs: Observable<Problem[]>;

  private searchTerms = new Subject<string>();

  highlightIndex = 0;

  /**
   * This is needed
   */
  search(event: any, term: string) {
    if (event.keyCode === 40) {
      var sub = this.problemsObs.subscribe((value: Problem[]) => {
        if (this.highlightIndex < value.length - 1) {
          this.highlightIndex++;
        } else {
          this.highlightIndex = 0;
        }
        sub.unsubscribe();
      });
    } else if (event.keyCode === 13) {
      var sub = this.problemsObs.subscribe((value: Problem[]) => {
        this.problem = value[this.highlightIndex];
        this.searchFilter = value[this.highlightIndex].name;
        $('.dropdown-custom').removeClass("open");
        this.highlightIndex = 0;
        sub.unsubscribe();
      });
    } else if (event.keyCode === 38) {
      var sub = this.problemsObs.subscribe((value: Problem[]) => {
        if (this.highlightIndex > 0) {
          this.highlightIndex--;
        } else {
          this.highlightIndex = value.length - 1;
        }
        sub.unsubscribe();
      });
    } else {
      this.highlightIndex = 0;
    }
    this.searchTerms.next(term);
  };

  constructor(private _authService: AuthService,
      private _problemService: ProblemService,
      private _router: Router, private _route: ActivatedRoute) {
    this._authService.userData$.subscribe(user => {
      this.user = user;
    });
  };

  python: any = {
    version: 'py3'
  };

  currentHighlight: any = undefined;

  // WARNING: Wizard magic ahead. Proceed with caution.
  ngOnInit() {
    this.user = this._authService.getUserData();                                            /* Get the user information */
    var self = this;
    this.problemsObs = this.searchTerms.distinctUntilChanged().switchMap(term => term       /* gets a list of problems matching the search term */
          ? this.searchProblem(term) : Observable.of<Problem[]>([])).catch(err => {
      return Observable.of<Problem[]>([]);
    });

    // Gets the problem id from the url parameters and fetches the problem id
    this._route.params.switchMap((params: Params) => params['problem'] !== undefined  ? this._problemService.getProblemByPid(+params['problem']) : Observable.of<Problem>(new Problem()))
    .subscribe((problem: Problem) => {
      this.searchFilter = problem.name;
      this.problem = problem;
    }, (err) => {
      this.problem = new Problem();
    });

    // Fetches all the problems
    this._problemService.getAllProblems().then(problems => {
      this.problems = problems;
    })
  };

  // called when a user clicks a problem from the problem search list
  problemSelected(problem: Problem) {
    this.problem = problem;
    this.searchFilter = problem.name;
    $('.dropdown-custom').removeClass("open");
  };

  // Opens and closes the dropdown search pane
  boxUpdate(event: string) {
    if (event.length > 0) {
      if (this.problem.name !== event) {
        this.problem = new Problem();
      }
      $('.dropdown-custom').addClass("open");
    } else {
      $('.dropdown-custom').removeClass("open");
    }
  }

  // Takes in a term and returns a promise containing an array of matches
  searchProblem(term: string) : Promise<Problem[]> {
    return new Promise((resolve, reject) => {
      if (term === "") {resolve(this.problems)} else {
        var validMatches = [];
        for (var i = 0; i < this.problems.length; i++) {
          if (this.problems[i].name.toLowerCase().startsWith(term.toLowerCase())) {
            validMatches.push(this.problems[i]);
          }
        }
        resolve(validMatches);
      }
    });
  };

  // Implment the submission stuff here
  onSubmit() {
    if (this.submitReady()) {
      return true;
    } else {
      return false;
    }
  };

  submitReady() {
    return this.user.loggedIn && this.problem !== undefined && this.file !== undefined;
  };

  fileSelect(event: any) {
    this.file = event.target.files[0];
  }
}
