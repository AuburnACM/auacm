import { Component, OnInit } from '@angular/core';

import { AuthService, SimpleResponse } from '../auth.service';

import { UserData } from '../models/user';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.css']
})
export class CreateUserComponent implements OnInit {

  userData: UserData;
  confirmPassword: string = '';
  serverResponse: SimpleResponse;

  constructor(private _authService: AuthService) { }

  ngOnInit() {
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
