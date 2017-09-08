import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { UserService } from './user.service';
import { ProfileService } from './profile.service';

import { UserData } from './models/user';

declare var $: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  public user: UserData;
  public failedLogin = false;
  public loginOpen = false;
  public navCollapsed = false;
  public profilePictureSource = '';

  constructor(public _router: Router,
    private _userService: UserService,
    private _profileService: ProfileService) {
    this.user = _userService.getUserData();
    _userService.userData$.subscribe(user => {
      this.user = user;
      this.profilePictureSource = 'api/profile/image/' + user.username + '?' + new Date().getTime();
    });
    _profileService.profilePictureObservable().subscribe(update => {
      if (update) {
        this.refreshUserProfilePicture();
      }
    });
  }

  ngOnInit() {
    this._userService.refreshUserData();
  };

  logout() {
    this._userService.logout();
  };

  logIn() {
    this._userService.login(this.user.username, this.user.password).then(success => {
      if (!success) {
        this.failedLogin = true;
        document.getElementById('username').focus();
      } else {
        // Closes the gosh dern menu if login is successful. I spent years trying to figure this out.
        // Send help.
        $('#dropdownMenu1').dropdown('toggle');
        this.failedLogin = false;
      }
    });
  }

  register() {
    $('#dropdownMenu1').dropdown('toggle');
    this._router.navigate(['register'])
  }

  navigateTo(page: string) {
    this._router.navigate([page]);
  };

  openDropdownMenu() {
    // Sets the focus on the username form.
    // Needs a slight delay so the username form can be rendered by the *ngIf
    setTimeout(() => {
      const doc = document.getElementById('username');
      if (doc !== undefined && doc !== null) {
        doc.focus();
      }
    }, 100);
  };

  refreshUserProfilePicture() {
    this.profilePictureSource = 'api/profile/image/' +
      this.user.username + '?' + new Date().getTime();
  }
}
