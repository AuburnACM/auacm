import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from './auth.service';

import { UserData } from './models/user';

declare var $:any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private _router: Router, private _authService: AuthService) {
    this.user = _authService.getUserData();
    _authService.userData$.subscribe(user => {
      this.user = user;
    });
  }

  private user: UserData;

  failedLogin: boolean = false;

  loginOpen: boolean = false;
  navCollapsed: boolean = false;

  ngOnInit() {
    this._authService.refreshUserData();
  };

  logout() {
    this._authService.logout();
  };

  logIn() {
    this._authService.login(this.user.username, this.user.password).then(success => {
      if (!success) {
        this.failedLogin = true;
        document.getElementById('username').focus();
      } else {
        // Closes the gosh dern menu if login is successful. I spent years trying to figure this out.
        // Send help.
        $("#dropdownMenu1").dropdown("toggle");
        this.failedLogin = false;
      }
    });
  }

  navigateTo(page: string) {
    this._router.navigate([page]);
  };

  openDropdownMenu() {
    // Sets the focus on the username form.
    // Needs a slight delay so the username form can be rendered by the *ngIf
    setTimeout(function() {
      var doc = document.getElementById('username');
      if (doc !== undefined && doc !== null) {
        doc.focus();
      }
    }, 100);
  };
}
