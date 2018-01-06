import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/switchMap';

import { UserService } from '../user.service';
import { CompetitionService } from '../competition.service';

import { SimpleUser, UserData, WebsocketRegisteredUser } from '../models/user';
import { Competition } from '../models/competition';
import { DragulaService } from 'ng2-dragula';
import { dragula } from 'ng2-dragula/components/dragula.class';
import { OnDestroy } from '@angular/core/src/metadata/lifecycle_hooks';

const DRAG_BOX_NAME = 'members';

@Component({
  selector: 'app-edit-teams',
  templateUrl: './edit-teams.component.html',
  styleUrls: ['./edit-teams.component.css']
})
export class EditTeamsComponent implements OnInit, OnDestroy {
  public userData: UserData = new UserData();
  public teams: Map<string, SimpleUser[]> = new Map<string, SimpleUser[]>();
  public individuals: SimpleUser[] = [];
  public teamName = '';
  public DND_BOX_NAME = DRAG_BOX_NAME;
  public competitionId = 0;
  public responseFailed = false;
  public responseSuccess = false;
  public responseMessage = '';

  constructor(private _router: Router, private _userService: UserService,
              private _competitionService: CompetitionService, private _activeRoute: ActivatedRoute,
              private _location: Location, private dragulaService: DragulaService) {
    this._userService.userData$.subscribe(data => {
      this.userData = data;
      if (!this.userData.loggedIn || !this.userData.isAdmin) {
        if (this._router.url.startsWith('/blog') && this._router.url.endsWith('/edit')) {
          this._router.navigate(['404']);
        }
      }
    });
    this._competitionService.teamSource.subscribe(teams => {
      this.updateTeams(teams);
    });
    dragulaService.drop.subscribe(next => {
      this.saveToSocket();
    });
  }

  ngOnInit() {
    this._activeRoute.params.switchMap((params: Params) => {
      if (params['cid']) {
        this.competitionId = +params['cid'];
        this._competitionService.watch(+params['cid']);
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
          }
        }
      }
    });
  }

  ngOnDestroy() {
    if (this.competitionId > 0) {
      this._competitionService.stopWatching(this.competitionId);
    }
  }

  back() {
    this._location.back();
  }

  removeTeam(name: string) {
    if (this.teams[name] !== undefined) {
      for (const user of this.teams[name]) {
        this.individuals.push(user);
      }
      delete this.teams[name];
    }
  }

  addTeam() {
    if (!this.teamExists()) {
      this.teams[this.teamName] = [];
    }
  }

  getTeamNames(): string[] {
    const test: string[] = [];
    for (const teamName in this.teams) {
      test.push(teamName);
    }
    return test;
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

  getTeamMap(): Map<string, SimpleUser[]> {
    const map = new Map<string, SimpleUser[]>();
    for (const teamName in this.teams) {
      if (this.teams[teamName].length > 0) {
        map[teamName] = this.teams[teamName];
      }
    }
    for (const individual of this.individuals) {
      map[individual.display] = [individual];
    }
    return map;
  }

  saveToSocket() {
    if (this.competitionId > 0) {
      const temp = this.getTeamMap();
      this._competitionService.updateCompetitionTeamsSocket(this.competitionId, temp);
    }
  }

  save() {
    if (this.competitionId > 0) {
      this._competitionService.updateCompetitionTeams(this.competitionId, this.getTeamMap())
          .then(() => {
        this.responseSuccess = true;
        this.responseFailed = false;
        this.responseMessage = 'Successfully updated the users!';
      }).catch((err: Response) => {
        this.responseSuccess = false;
        this.responseFailed = true;
        this.responseMessage = 'Failed to update the users!';
      });
    }
  }

  private updateTeams(teamMap: Map<string, SimpleUser[]>) {
    const toRemove: string[] = [];
    for (const teamName in this.teams) {
      toRemove.push(teamName);
    }
    const individualsToRemove: SimpleUser[] = this.individuals.slice(0);
    const toAdd = new Map<string, SimpleUser[]>();

    // Lets create our new teams and remove the unused ones
    for (const teamName in teamMap) {
      // Check to see if it is an individual team
      if (teamMap[teamName].length == 1 && teamMap[teamName][0].display === teamName) {
        const individual: SimpleUser = teamMap[teamName][0];
        // Check to see if the individual is already in the list
        let found = false;
        for (const simpleUser of this.individuals) {
          if (simpleUser.display === individual.display && simpleUser.username === individual.username) {
            // Since we found it, we need to remove it from the delete list
            individualsToRemove.splice(individualsToRemove.indexOf(simpleUser), 1);
            found = true;
            break;
          }
        }
        if (!found) {
          // Since we didn't find it, we will add it to the list
          this.individuals.push(individual);
        }
      } else {
        if (this.teams[teamName] === undefined) {
          // Creating a new team
          toAdd[teamName] = teamMap[teamName];
        } else {
          // Lets remove one that exists, then we will be left with teams
          // that need to be deleted
          toRemove.splice(toRemove.indexOf(teamName), 1);
          const teamList: SimpleUser[] = [];
          for (const user of this.teams[teamName]) {
            teamList.push(user);
          }
          for (const otherUser of teamMap[teamName]) {
            let found = false;
            for (const simpleUser of this.teams[teamName]) {
              if (simpleUser.display === otherUser.display && simpleUser.username === otherUser.username) {
                // Both teams have the same user
                teamList.splice(teamList.indexOf(simpleUser), 1);
                found = true;
                break;
              }
            }
            if (!found) {

              this.teams[teamName].push(otherUser);
            }
          }
          // Delete users not found
          for (const user of teamList) {
            this.teams[teamName].splice(this.teams[teamName].indexOf(user), 1);
            if (this.teams[teamName].length == 0) {
              delete this.teams[teamName];
            }
          }
        }
      }
    }

    // Finally, let's do some cleanup
    // Delete users that haven't been found in the individuals list
    for (const user of individualsToRemove) {
      let index = -1;
      for (let i = 0; i < this.individuals.length; i++) {
        const current = this.individuals[i];
        if (current.display === user.display && current.username === user.username) {
          index = i;
          break;
        }
      }
      this.individuals.splice(index, 1);
      // this.individuals.splice(this.individuals.indexOf(user), 1);
    }

    // Delete unnecessary teams
    for (const teamName of toRemove) {
      delete this.teams[teamName];
    }

    // Add new teams
    for (const teamName in toAdd) {
      this.teams[teamName] = toAdd[teamName];
    }
  }
}
