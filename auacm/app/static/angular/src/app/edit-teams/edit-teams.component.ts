import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { AuthService } from '../auth.service';
import { CompetitionService } from '../competition.service';

import { SimpleUser, UserData } from '../models/user';
import { Competition } from '../models/competition';

@Component({
  selector: 'app-edit-teams',
  templateUrl: './edit-teams.component.html',
  styleUrls: ['./edit-teams.component.css']
})
export class EditTeamsComponent implements OnInit {

  private userData: UserData = new UserData();

  constructor(private _router: Router, private _authService: AuthService,
              private _competitionService: CompetitionService) {
    this._authService.userData$.subscribe(data => {
      this.userData = data;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if (this._router.url.startsWith('/blog') && this._router.url.endsWith('/edit')) {
          this._router.navigate(['404']);
        }
      }
    })
  }

  ngOnInit() {
  }

}
