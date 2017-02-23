import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

import { UserService } from '../user.service';

import { UserData } from '../models/user';

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.css']
})
export class NotFoundComponent implements OnInit {

  private userData: UserData = new UserData();

  constructor(private _router: Router, private _userService: UserService,
              private _location: Location) {
    this._userService.userData$.subscribe(newData => {
      this.userData = newData;
    })
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
  }
}
