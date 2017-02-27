import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControlDirective, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { UserService } from '../user.service';

import { UserData } from '../models/user';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {

  private userData: UserData = new UserData();
  private oldPassword = '';
  private newPassword = '';
  private confirmPassword = '';
  private settingsForm: FormGroup;
  private responseSuccess = false;
  private responseFailed = false;
  private responseMessage = '';

  constructor(private _userService: UserService, private _router: Router,
              private _formBuilder: FormBuilder) {
    this._userService.userData$.subscribe(user => {
      if (!user.loggedIn) {
        this._router.navigate(['404']);
      } else {
        this.userData = user;
      }
    });
    this.settingsForm = _formBuilder.group({
      oldPassword: [this.oldPassword, Validators.required],
      newPassword: [this.newPassword, Validators.required],
      confirmPassword: [this.confirmPassword, Validators.required],
    }, {validator: this.validatePassword('oldPassword', 'newPassword' , 'confirmPassword')});
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
  }

  changePassword() {
    this._userService.changePassword(this.settingsForm.controls['oldPassword'].value,
        this.settingsForm.controls['newPassword'].value).then(success => {
      if (success) {
        this.responseSuccess = true;
        this.responseFailed = false;
        this.responseMessage = 'Successfully updated your settings.';
      } else {
        this.responseSuccess = false;
        this.responseFailed = true;
        this.responseMessage = 'Failed to update your settings.';
      }
    });
  }

  validatePassword(oldPasswordKey, newPasswordKey, confirmPasswordKey) {
    return (group: FormGroup): {[key: string]: any} => {
      const oldPassword = group.controls[oldPasswordKey];
      const newPassword = group.controls[newPasswordKey];
      const confirmPassword = group.controls[confirmPasswordKey];
      if (oldPassword.value === newPassword.value) {
        return {
          passwordsMatch: true
        };
      }
      if (confirmPassword.value !== newPassword.value) {
        return {
          missmatchedPasswords: true
        };
      }
      return null;
    };
  }
}
