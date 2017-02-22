import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../auth.service';

import { UserData } from '../models/user';
import { SimpleResponse } from '../models/response';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.css']
})
export class CreateUserComponent implements OnInit {

  private activeUser: UserData = new UserData();
  private userData: UserData;
  private confirmPassword: string = '';
  private serverResponse: SimpleResponse;

  constructor(private _authService: AuthService, private _router: Router) {
    this._authService.userData$.subscribe(userData => {
      this.activeUser = userData;
      if (!this.activeUser.loggedIn || !this.activeUser.isAdmin) {
        if (this._router.url === '/users/create') {
          this._router.navigate(['404']);
        }
      }
    })
  }

  ngOnInit() {
    this.activeUser = this._authService.getUserData();
    this.userData = new UserData();
    this.userData.displayName = "";
  }

  submit(usernameForm, passwordForm, displayNameForm, confirmPassword) {
    this._authService.createUser(this.userData.username, this.userData.password, this.userData.displayName).then(response => {
      this.serverResponse = response;
      if (response.success) {
        this.userData = new UserData();
        this.confirmPassword = "";
        usernameForm.reset();
        passwordForm.reset();
        displayNameForm.reset();
        confirmPassword.reset();
      }
    });
  }
}
