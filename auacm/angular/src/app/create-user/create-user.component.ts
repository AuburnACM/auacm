import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { UserService } from '../user.service';

import { UserData } from '../models/user';
import { SimpleResponse } from '../models/response';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.css']
})
export class CreateUserComponent implements OnInit {

  public activeUser: UserData = new UserData();
  public userData: UserData;
  public confirmPassword = '';
  public serverResponse: SimpleResponse;

  constructor(private _userService: UserService, private _router: Router) {
    this._userService.userData$.subscribe(userData => {
      this.activeUser = userData;
      if (!this.activeUser.loggedIn || !this.activeUser.isAdmin) {
        if (this._router.url === '/users/create') {
          this._router.navigate(['404']);
        }
      }
    });
  }

  ngOnInit() {
    this.activeUser = this._userService.getUserData();
    this.userData = new UserData();
    this.userData.displayName = '';
  }

  submit(usernameForm, passwordForm, displayNameForm, confirmPassword) {
    this._userService.createUser(this.userData.username,
        this.userData.password, this.userData.displayName).then(response => {
      this.serverResponse = response;
      if (response.success) {
        this.userData = new UserData();
        this.confirmPassword = '';
        usernameForm.reset();
        passwordForm.reset();
        displayNameForm.reset();
        confirmPassword.reset();
      }
    });
  }
}
