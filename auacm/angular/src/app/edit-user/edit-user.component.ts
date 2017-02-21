import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControlDirective, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../auth.service';

import { UserData } from '../models/user';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {

  private userData: UserData = new UserData();
  private oldPassword: string = '';
  private newPassword: string = '';
  private confirmPassword: string = '';
  private settingsForm: FormGroup;

  private responseSuccess: boolean = false;
  private responseFailed: boolean = false;
  private responseMessage: string = '';

  constructor(private _authService: AuthService, private _router: Router,
              private _formBuilder: FormBuilder) {
    this._authService.userData$.subscribe(user => {
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
    }, {validator: this.validatePassword('oldPassword', 'newPassword' ,'confirmPassword')});
  }

  ngOnInit() {
    this.userData = this._authService.getUserData();
  }

  changePassword(): void {
    this._authService.changePassword(this.settingsForm.controls['oldPassword'].value, this.settingsForm.controls['newPassword'].value).then(success => {
      if (success) {
        this.responseSuccess = true;
        this.responseFailed = false;
        this.responseMessage = 'Successfully updated your settings.';
      } else {
        this.responseSuccess = false;
        this.responseFailed = true;
        this.responseMessage = 'Failed to update your settings.';
      }
    })
  }

  validatePassword(oldPasswordKey, newPasswordKey, confirmPasswordKey) {
    return (group: FormGroup): {[key: string]: any} => {
      var oldPassword = group.controls[oldPasswordKey];
      var newPassword = group.controls[newPasswordKey];
      var confirmPassword = group.controls[confirmPasswordKey];
      if (oldPassword.value === newPassword.value) {
        return {
          passwordsMatch: true
        }
      }
      if (confirmPassword.value !== newPassword.value) {
        return {
          missmatchedPasswords: true
        }
      }
      return null;
    }
  };
}
