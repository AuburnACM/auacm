import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import 'rxjs/add/operator/switchMap';
import { Observable } from 'rxjs/Rx';
import { Subject } from 'rxjs/Subject';

import { UserService } from '../user.service';
import { SubmissionService } from '../submission.service';
import { ProblemService } from '../problem.service';

import { UserData } from '../models/user';
import { Problem } from '../models/problem';
import { RecentSubmission } from '../models/submission';

declare var $: any;

const KEY_UP = 38;
const KEY_DOWN = 40;
const KEY_ENTER = 13;

/**
 * This component is a little crazy. It uses some Angular magic to create searchable problems.
 */

@Component({
  selector: 'app-judge',
  templateUrl: './judge.component.html',
  styleUrls: ['./judge.component.css']
})
export class JudgeComponent implements OnInit {
  public user: UserData;
  public file: File;
  public searchFilter = '';
  public problem: Problem = new Problem();
  public problems: Problem[] = [];
  public problemsMap: Map<number, Problem> = new Map<number, Problem>();
  public problemsObs: Observable<Problem[]>;
  public searchTerms = new Subject<string>();
  public highlightIndex = 0;
  public submitted: RecentSubmission[] = [];
  public currentHighlight: any;

  public python: any = {
    version: 'py3'
  };

  constructor(private _userService: UserService,
              private _submissionService: SubmissionService,
              private _problemService: ProblemService,
              private _router: Router, private _route: ActivatedRoute) {
    this._userService.userData$.subscribe(user => {
      if (!this.user.loggedIn && user.loggedIn) {
        this._submissionService.refreshSubmits(user.username, 10);
      }
      this.user = user;
      // if (!this.user.loggedIn) {
      //   if (this._router.url.startsWith('/judge')) {
      //     this._router.navigate(['404']);
      //   }
      // }
    });
    this._submissionService.recentSubmitsData$.subscribe(submissions => {
      this.submitted = submissions;
    });
  }

  /**
   * This is needed
   */
  search(event: any, term: string) {
    if (event.keyCode === KEY_DOWN) {
      const sub = this.problemsObs.subscribe((value: Problem[]) => {
        if (this.highlightIndex < value.length - 1) {
          this.highlightIndex++;
        } else {
          this.highlightIndex = 0;
        }
        sub.unsubscribe();
      });
    } else if (event.keyCode === KEY_ENTER) {
      const sub = this.problemsObs.subscribe((value: Problem[]) => {
        this.problem = value[this.highlightIndex];
        this.searchFilter = value[this.highlightIndex].name;
        $('.dropdown-custom').removeClass('open');
        this.highlightIndex = 0;
        sub.unsubscribe();
      });
    } else if (event.keyCode === KEY_UP) {
      const sub = this.problemsObs.subscribe((value: Problem[]) => {
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
  }

  // WARNING: Wizard magic ahead. Proceed with caution.
  ngOnInit() {
    // Get the user information
    this.user = this._userService.getUserData();
    // Gets a list of problems matching the search term
    this.problemsObs = this.searchTerms.distinctUntilChanged().switchMap(term => term
          ? this.searchProblem(term) : Observable.of<Problem[]>([])).catch(err => {
      return Observable.of<Problem[]>([]);
    });

    // Gets the problem id from the url parameters and fetches the problem id
    this._route.params.switchMap((params: Params) => params['problem'] !== undefined
        ? this._problemService.getProblemByPid(+params['problem'])
        : Observable.of<Problem>(new Problem())).subscribe((problem: Problem) => {
      if (problem !== undefined) {
        this.searchFilter = problem.name;
        this.problem = problem;
      } else {
        this.problem = new Problem();
      }
    }, (err) => {
      this.problem = new Problem();
    });

    // Fetches all the problems
    this._problemService.getAllProblems().then(problems => {
      this.problems = problems;
      for (let i = 0; i < problems.length; i++) {
        this.problemsMap[problems[i].pid] = problems[i];
      }
    });

    // Fetches the most recent submissions
    if (this.user.username !== '') {
      this._submissionService.refreshSubmits(this.user.username, 10);
    }
  }

  // called when a user clicks a problem from the problem search list
  problemSelected(problem: Problem) {
    this.problem = problem;
    this.searchFilter = problem.name;
    $('.dropdown-custom').removeClass('open');
  }

  // Opens and closes the dropdown search pane
  boxUpdate(event: string) {
    if (event.length > 0) {
      if (this.problem.name !== event) {
        this.problem = new Problem();
      }
      $('.dropdown-custom').addClass('open');
    } else {
      $('.dropdown-custom').removeClass('open');
    }
  }

  // Takes in a term and returns a promise containing an array of matches
  searchProblem(term: string): Promise<Problem[]> {
    return new Promise((resolve, reject) => {
      if (term === '') {
        resolve(this.problems);
      } else {
        const validMatches = [];
        for (let i = 0; i < this.problems.length; i++) {
          if (this.problems[i].name.toLowerCase().startsWith(term.toLowerCase())) {
            validMatches.push(this.problems[i]);
          }
        }
        resolve(validMatches);
      }
    });
  }

  // Implment the submission stuff here
  onSubmit() {
    if (this.submitReady()) {
      this._submissionService.submit(this.file, this.problem, this.python.version, this.user).then(data => {
      });
      this._submissionService.refreshSubmits(this.user.username, 10);
      return true;
    } else {
      return false;
    }
  }

  submitReady() {
    return this.user.loggedIn && this.problem !== undefined && this.file !== undefined;
  }

  fileSelect(event: any) {
    this.file = event.target.files[0];
  }
}
