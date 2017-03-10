import { Component, OnInit } from '@angular/core';

import { UserService } from '../user.service';
import { ProfileService } from '../profile.service';

import { UserProfile } from '../models/profile';
import { UserData } from '../models/user';

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
              private _profileService: ProfileService) {
    this._userService.userData$.subscribe(data => {
      this.user = data;
      this.getProfile();
    });
  }

  ngOnInit() {
    this.getProfile();
  }

  getProfile() {
    this.user = this._userService.getUserData();
    this._profileService.getUserProfile(this.user.username).then(profileData => {
      this.profile = profileData;
      console.log(this.profile);
      this.hasRecentActivity = this.profile.recentAttempts.length > 0 ||
          this.profile.recentCompetitions.length > 0 ||
          this.profile.recentBlogPosts.length > 0;
    });
  }
}
