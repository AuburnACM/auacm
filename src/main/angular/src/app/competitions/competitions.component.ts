import { Component, OnInit, OnDestroy } from '@angular/core';

import { CompetitionService } from '../competition.service';
import { UserService } from '../user.service';

import { Competition, CompetitionProblem } from '../models/competition';
import { UserData } from '../models/user';

@Component({
  selector: 'app-competitions',
  templateUrl: './competitions.component.html',
  styleUrls: ['./competitions.component.css']
})
export class CompetitionsComponent implements OnInit, OnDestroy {

  public competitions: Map<string, Competition[]> = new Map<string, Competition[]>();
  public timer: NodeJS.Timer = undefined;
  public userData: UserData = new UserData();

  // Needs sorting eventually
  constructor(private _competitionService: CompetitionService,
              private _userService: UserService) {
    this.competitions['ongoing'] = [];
    this.competitions['upcoming'] = [];
    this.competitions['past'] = [];
    this._userService.userData$.subscribe(data => {
      this.userData = data;
    });
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
    this.getCompetitions();
    this.startTimer();
  }

  ngOnDestroy() {
    if (this.timer !== undefined) {
      clearInterval(this.timer);
    }
  }

  getCompetitions() {
    this._competitionService.getAllCompetitions().then(competitions => {
      this.competitions = competitions;
      for (const comp of this.competitions['ongoing']) {
        comp.timeRemaining = this.getRemainingTime(comp);
      }
    });
  }

  startTimer() {
    const self = this;
    this.timer = setInterval(function() {
      if (self.competitions['ongoing'].length > 0) {
        for (const comp of self.competitions['ongoing']) {
          if (comp.timeRemaining < 0) {
            self.getCompetitions();
            self.competitions['ongoing'].splice(self.competitions['ongoing'].indexOf(comp), 1);
            break;
          } else {
            comp.timeRemaining--;
          }
        }
      }
    }, 1000);
  }

  register(competition: Competition) {
    this._competitionService.register(competition.cid).then(success => {
      competition.registered = true;
    }).catch((err: Response) => {
      competition.registered = false;
    });
  }

  getRemainingTime(competition: Competition): number {
    return competition.startTime + competition.length - (Date.now() / 1000);
  }
}
