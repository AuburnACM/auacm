import { Component, OnInit, OnDestroy } from '@angular/core';

import { CompetitionService } from '../competition.service';
import { AuthService } from '../auth.service';

import { Competition, CompetitionProblem } from '../models/competition';
import { UserData } from '../models/user';

@Component({
  selector: 'app-competitions',
  templateUrl: './competitions.component.html',
  styleUrls: ['./competitions.component.css']
})
export class CompetitionsComponent implements OnInit, OnDestroy {

  private competitions: Map<string, Competition[]> = new Map<string, Competition[]>(); 
  private timer: NodeJS.Timer = undefined;

  user: UserData = new UserData();

  // Needs sorting eventually
  constructor(private _competitionService: CompetitionService,
              private _authService: AuthService) { 
    this.competitions['ongoing'] = [];
    this.competitions['upcoming'] = [];
    this.competitions['past'] = [];
    this._authService.userData$.subscribe(data => {
      this.user = data;
    })
  }

  ngOnInit() {
    this.user = this._authService.getUserData();
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
      for (var comp of this.competitions['ongoing']) {
        comp.timeRemaining = this.getRemainingTime(comp);
      }
    });
  }

  startTimer() {
    var self = this;
    this.timer = setInterval(function() {
      if (self.competitions['ongoing'].length > 0) {
        for (var comp of self.competitions['ongoing']) {
          if (comp.remainingTime < 0) {
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
      if (success) {
        competition.registered = true;
      } else {
        console.log('Failed to register you!');
      }
    });
  };

  getRemainingTime(competition: Competition): number {
    return competition.startTime + competition.length - (Date.now() / 1000);
  }

}
