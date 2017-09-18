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
  public passwordForm: FormGroup;
  public responseSuccess = false;
  public responseFailed = false;
  private responseMessage = '';

  // Display Name Form
  public displayNameForm: FormGroup;
  private newDisplayName = '';
  public displayNameResponseSuccess = false;
  public displayNameResponseFailed = false;
  public displayNameResponseMessage = '';

  constructor(private _userService: UserService, private _router: Router,
              private _formBuilder: FormBuilder) {
    this._userService.userData$.subscribe(user => {
      if (!user.loggedIn) {
        this._router.navigate(['404']);
      } else {
        this.userData = user;
      }
    });
    this.passwordForm = _formBuilder.group({
      oldPassword: [this.oldPassword, Validators.required],
      newPassword: [this.newPassword, Validators.required],
      confirmPassword: [this.confirmPassword, Validators.required],
    }, {validator: this.validatePassword('oldPassword', 'newPassword' , 'confirmPassword')});

    this.displayNameForm = _formBuilder.group({
      newDisplayName: [this.newDisplayName, Validators.required]
    }, {validator: this.validateDisplayName('newDisplayName')});
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
  }

  changePassword() {
    this._userService.changePassword(this.passwordForm.controls['oldPassword'].value,
        this.passwordForm.controls['newPassword'].value).then(success => {
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

  changeDisplayName() {
    this._userService.changeDisplayName(
        this.displayNameForm.controls['newDisplayName'].value).then(success => {

      if (success) {
        this.displayNameResponseSuccess = true;
        this.displayNameResponseFailed = false;
        this.displayNameResponseMessage = 'Successfully updated your settings.';
      } else {
        this.displayNameResponseSuccess = false;
        this.displayNameResponseFailed = true;
        this.displayNameResponseMessage = 'Failed to update your settings.';
      }
    });
  }

  validateDisplayName(newDisplayNameKey) {
    return (group: FormGroup): {[key: string]: any} => {
      const displayNameRegex = /^[a-zA-Z0-9 ',_]+$/;
      const newDisplayName = group.controls[newDisplayNameKey].value;
      if (displayNameRegex.test(newDisplayName) || newDisplayName.length === 0) {
        return null;
      } else {
        return {
          invalidDiplayName: true
        };
      }
    };
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
          mismatchedPasswords: true
        };
      }
      return null;
    };
  }
}
