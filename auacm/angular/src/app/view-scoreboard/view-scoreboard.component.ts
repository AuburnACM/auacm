import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/switchMap';

import { CompetitionService } from '../competition.service';
import { UserService } from '../user.service';
import { WebsocketService } from '../websocket.service';

import { Competition, CompetitionProblem, CompetitionTeam, TeamProblemData } from '../models/competition';
import { UserData } from '../models/user';

@Component({
  selector: 'app-view-scoreboard',
  templateUrl: './view-scoreboard.component.html',
  styleUrls: ['./view-scoreboard.component.css']
})
export class ViewScoreboardComponent implements OnInit, OnDestroy {
  public userData: UserData = new UserData();
  public competition: Competition = new Competition();
  public timeUntil = 0;
  private timeLeft = 0;
  public ended = false;
  public active = false;
  private clientTimeOffset = 0;
  private scoreboardTimer: NodeJS.Timer[] = [];

  constructor(private _activeRoute: ActivatedRoute, private _competitionService: CompetitionService,
              private _userService: UserService, private _websocketService: WebsocketService,
              private _router: Router) {
    this._userService.userData$.subscribe(data => this.userData = data);

    // Subscribe to the competition service so that the component receives the latest
    // time offset and competition data
    this._competitionService.timeOffsetSource.subscribe(newClientOffset => this.clientTimeOffset = newClientOffset);
    this._competitionService.competitionSource.subscribe(competition => {
      // check if the competition was cleared (page reloaded, page change)
      if (!(this.competition.cid > 0 && competition.cid === 0)) {
        this.competition = competition;
        this.refreshScoreboard();
        this.calculateCompetitionProgress();
      } else {
        this.competition = competition;
      }
    });

    // When a user is added to the competition refresh the competition data
    this._competitionService.competitionTeamSource.subscribe(data => {
      if (this.competition.cid > 0) {
        this._competitionService.fetchCompetition(this.competition.cid);
      }
    });
  }

  ngOnInit() {
    this.userData = this._userService.getUserData();
    this.clientTimeOffset = this._competitionService.getClientTimeOffset();
    this._activeRoute.params.subscribe(params => {
      if (params['cid']) {
        this._competitionService.fetchCompetition(+params['cid']);
      } else {
        this._router.navigate(['404']);
      }
    });
  }

  ngOnDestroy() {
    // You need to clear the timer when the component is destroyed
    // otherwise the client will build up 1 billion timers. xD
    if (this.scoreboardTimer !== undefined && this.scoreboardTimer !== null) {
      for (const timer of this.scoreboardTimer) {
        clearInterval(timer);
      }
    }
    // If the user is on this page, clear the scoreboard data
    this._competitionService.resetScoreboard();
  }

  refreshScoreboard() {
    for (const team of this.competition.teams) {
      let solved = 0;
      let time = 0;
      for (const key in team.problemData) {
        if (team.problemData[key].status === 'correct') {
          solved++;
          team.problemData[key].penaltyTime = (team.problemData[key].submitCount - 1) * 20;
          time += team.problemData[key].submitTime + team.problemData[key].penaltyTime;
        } else {
          team.problemData[key].penaltyTime = team.problemData[key].submitCount * 20;
        }
      }
      team.solved = solved;
      team.time = time;
    }

    this.competition.teams.sort((a, b) => {
      if (a.solved !== b.solved) {
        return b.solved - a.solved;
      } else {
        return a.time - b.time;
      }
    });

    if (this.competition.teams.length > 0) {
      let rank = 1;
      let prevSolved = this.competition.teams[0].solved;
      let prevTime = this.competition.teams[0].time;
      this.competition.teams[0].rank = rank;
      for (let i = 1; i < this.competition.teams.length; i++) {
        const team = this.competition.teams[i];
        if (team.solved < prevSolved) {
          rank++;
          team.rank = rank;
        } else if (team.solved === prevSolved && team.time > prevTime) {
          rank++;
          team.rank = rank;
        } else {
          team.rank = rank;
        }
        prevSolved = team.solved;
        prevTime = team.time;
      }
    }
  }

  problemIsInComp(problemId: number): boolean {
    for (const problem in this.competition.compProblems) {
      if (problemId === this.competition.compProblems[problem].pid) {
        return true;
      }
    }
    return false;
  }

  calculateCompetitionProgress() {
    if (this.scoreboardTimer.length < 1) {
      let clientTime = Math.floor((Date.now() + this.clientTimeOffset) / 1000);
      if (clientTime < this.competition.startTime + this.competition.length) {
        // only start the timer if the competition is still going or
        // it hasn't yet started
        this.ended = false;
        this.active = false;
        const self = this;
        const timer = setInterval(() => {
          const timeToEnd = self.competition.startTime + self.competition.length - clientTime;

          // Compute the remaining time as the minimum of the time
          // until the compeition is over and the length of the
          // competition.
          self.timeLeft = Math.min(timeToEnd, self.competition.length);
          if (self.timeLeft < self.competition.length) {
            self.active = true;
          } else {
            self.timeUntil = self.competition.startTime - clientTime;
          }

          clientTime = Math.floor((Date.now() + self.clientTimeOffset) / 1000);
          if (self.timeLeft <= 0) {
            self.active = false;
            self.ended = true;
            self.timeLeft = 0;
            self.scoreboardTimer.splice(self.scoreboardTimer.indexOf(timer), 1);
            clearInterval(timer);
          }
        }, 1000);
        this.scoreboardTimer.push(timer);
      } else {
        this.ended = true;
      }
    }
  }
}
