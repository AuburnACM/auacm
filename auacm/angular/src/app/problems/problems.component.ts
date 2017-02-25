import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { ProblemService } from '../problem.service';
import { UserService } from '../user.service';

import { UserData } from '../models/user';
import { Problem } from '../models/problem';

@Component({
  selector: 'app-problems',
  templateUrl: './problems.component.html',
  styleUrls: ['./problems.component.css']
})
export class ProblemsComponent implements OnInit {
  public user: UserData;
  public sortPredicate = 'name';
  public searchFilter = '';
  public sortReverse = false;
  public problems: Problem[] = [];

  constructor(private _problemService: ProblemService, private _userService: UserService,
              private _router: Router) {
    this._userService.userData$.subscribe(user => {
      // Check if the user was logged out or in. If so, refresh the problems
      if (this.user.loggedIn && !user.loggedIn || !this.user.loggedIn && user.loggedIn) {
        this.getProblems();
      }
      this.user = user;
    });
  }

  ngOnInit() {
    this.user = this._userService.getUserData();
    this.getProblems();
  }

  getProblems() {
    this._problemService.getAllProblems().then(problems => {
      this.problems = problems;
    });
  }

  order(sortPredicate: string) {
    this.sortReverse = (this.sortPredicate === sortPredicate) ? !this.sortReverse : false;
    this.sortPredicate = sortPredicate;
    this.problems = this.orderBy(this.problems, sortPredicate, this.sortReverse);
  }

  integerOrder(sortPredicate: string) {
    this.sortReverse = (this.sortPredicate === sortPredicate) ? !this.sortReverse : false;
    this.sortPredicate = sortPredicate;
    const self = this;
    this.problems.sort(function(a, b) {
      return self.sortReverse ?
        a.difficulty - b.difficulty : b.difficulty - a.difficulty;
    });
  }

  orderBy(array: Problem[], sortPredicate: string, reverse: boolean) {
    return array.sort(function(a, b) {
      if (reverse) {
        return -1 * a[sortPredicate].localeCompare(b[sortPredicate]);
      } else {
        return a[sortPredicate].localeCompare(b[sortPredicate]);
      }
    });
  };

  navigateTo(path: string) {
    this._router.navigate([path]);
  }
}
