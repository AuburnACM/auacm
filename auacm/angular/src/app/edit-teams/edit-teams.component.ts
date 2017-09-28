import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/switchMap';

import { UserService } from '../user.service';
import { CompetitionService } from '../competition.service';

import { SimpleUser, UserData, WebsocketRegisteredUser } from '../models/user';
import { Competition } from '../models/competition';

const DRAG_BOX_NAME = 'members';

@Component({
  selector: 'app-edit-teams',
  templateUrl: './edit-teams.component.html',
  styleUrls: ['./edit-teams.component.css']
})
export class EditTeamsComponent implements OnInit {
  public userData: UserData = new UserData();
  public teams: Map<string, SimpleUser[]> = new Map<string, SimpleUser[]>();
  public teamNames: string[] = [];
  public individuals: SimpleUser[] = [];
  public teamName = '';
  public DND_BOX_NAME = DRAG_BOX_NAME;
  public competitionId = 0;
  public responseFailed = false;
  public responseSuccess = false;
  public responseMessage = '';

  constructor(private _router: Router, private _userService: UserService,
              private _competitionService: CompetitionService, private _activeRoute: ActivatedRoute,
              private _location: Location) {
    this._userService.userData$.subscribe(data => {
      this.userData = data;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if (this._router.url.startsWith('/blog') && this._router.url.endsWith('/edit')) {
          this._router.navigate(['404']);
        }
      }
    });
    this._competitionService.competitionTeamSource.subscribe(socketData => {
      const newUser = new SimpleUser();
      newUser.display = socketData.display;
      newUser.username = socketData.username;
      this.individuals.push(newUser);

      // rare edge-case, but just to be thorough, we need to check to see
      // if a team with the same name as the user exists, and if so, break
      // it down into individuals.
      this.removeTeam(socketData.display);
    });
  }

  ngOnInit() {
    this._activeRoute.params.switchMap((params: Params) => {
      if (params['cid']) {
        this.competitionId = +params['cid'];
        return this._competitionService.getCompetitionTeams(+params['cid']);
      } else {
         return Observable.of<Map<string, SimpleUser[]>>(undefined);
      }
    }).subscribe(data => {
      if (data === undefined) {
        this._router.navigate(['404']);
      } else {
        for (const team in data) {
          if (data[team].length === 1 && data[team][0].display === team) {
            this.individuals.push(data[team][0]);
          } else {
            this.teams[team] = data[team];
            this.teamNames.push(team);
          }
        }
      }
    });
  }

  back() {
    this._location.back();
  }

  removeTeam(name: string) {
    if (this.teams[name] !== undefined) {
      for (const user of this.teams[name]) {
        this.individuals.push(user);
      }
      this.teamNames.splice(this.teamNames.indexOf(name), 1);
      delete this.teams[name];
    }
  }

  addTeam() {
    if (!this.teamExists()) {
      this.teams[this.teamName] = [];
      this.teamNames.push(this.teamName);
    }
  }

  teamExists(): boolean {
    if (this.teamName === '') {
      return true;
    }
    // Check individuals
    for (const teamName in this.individuals) {
      if (teamName === this.teamName) {
        return true;
      }
    }
    // Check team
    for (const teamName in this.teams) {
      if (teamName === this.teamName) {
        return true;
      }
    }
    return false;
  }

  save() {
    const map = new Map<string, string[]>();

    for (const teamName in this.teams) {
      if (this.teams[teamName].length > 0) {
        map[teamName] = [];
        for (const user1 of this.teams[teamName]) {
          map[teamName].push(user1.username);
        }
      }
    }

    for (const user of this.individuals) {
      map[user.display] = [];
      map[user.display].push(user.username);
    }

    if (this.competitionId > 0) {
      this._competitionService.updateCompetitionTeams(this.competitionId, map)
          .then(success => {
        if (success) {
          this.responseSuccess = true;
          this.responseFailed = false;
          this.responseMessage = 'Successfully updated the users!';
        } else {
          this.responseSuccess = false;
          this.responseFailed = true;
          this.responseMessage = 'Failed to update the users!';
        }
      });
    }
  }
}
