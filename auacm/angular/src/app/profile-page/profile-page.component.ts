import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { UserService } from '../user.service';
import { ProfileService } from '../profile.service';

import { UserProfile } from '../models/profile';
import { UserData } from '../models/user';

import 'rxjs/add/operator/switchMap';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {
  public profile: UserProfile = new UserProfile();
  public user: UserData;
  public hasRecentActivity: boolean;

  constructor(private _userService: UserService,
              private _profileService: ProfileService,
              private _router: Router,
              private _activeRoute: ActivatedRoute) {
    this._userService.userData$.subscribe(data => {
      this.user = data;
      if (!this.user.loggedIn && this._router.url === '/profile') {
        this._router.navigate(['/']);
      } else {
        this.getProfile();
      }
    });
  }

  ngOnInit() {
    this.getProfile();
  }

  getProfile() {
    this.user = this._userService.getUserData();
    this._activeRoute.params.switchMap((params: Params) => params['username'] ?
      this._profileService.getUserProfile(params['username']) :
      this._profileService.getUserProfile(this.user.username)).subscribe(profileData => {
          this.profile = profileData;
          this.hasRecentActivity = this.profile.recentAttempts.length > 0 ||
              this.profile.recentCompetitions.length > 0 ||
              this.profile.recentBlogPosts.length > 0;
        }
    );
  }
}
