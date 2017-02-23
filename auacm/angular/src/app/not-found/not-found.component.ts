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
      if (!this.userData.loggedIn && newData.loggedIn) {
        this.userData = newData;
        if (this._router.url === '/404') {
          try {
            this._location.back();
          } catch(err) {}
        }
      }
      this.userData = newData;
    })
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
  }

}
