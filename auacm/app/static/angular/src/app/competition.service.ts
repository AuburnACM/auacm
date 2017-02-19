import { Injectable } from '@angular/core';
import { Http, Request, Response, Headers, URLSearchParams } from '@angular/http';
import { Subject } from 'rxjs';

import { WebsocketService } from './websocket.service'; 

import { CompetitionProblem, Competition, CompetitionTeam, TeamProblemData } from './models/competition';
import { RecentSubmission } from './models/submission';
import { Problem } from './models/problem';
import { SimpleUser } from './models/user';

@Injectable()
export class CompetitionService {

  private clientTimeOffset: number = 0;
  timeOffsetSource: Subject<number> = new Subject<number>();

  private competition: Competition = new Competition();
  competitionSource: Subject<Competition> = new Subject<Competition>();

  constructor(private _http: Http, private _websocketService: WebsocketService) {
    this._websocketService.connect(window.location.host + '/websocket').subscribe(data => {
        // update the client's time offset and scoreboard data
        var response = JSON.parse(data.data);
        var responseData = response.data;
        var viewed = [];
        if (response.eventType === 'system_time') {
          this.clientTimeOffset = -Date.now() + responseData.milliseconds;
          this.timeOffsetSource.next(this.clientTimeOffset);
        } else if (response.eventType === 'status' && this.competition.cid > 0) {
          if (viewed.indexOf(responseData.submissionId) > -1 
              || !this.problemIsInComp(responseData.problemId)
              || responseData.submitTime > this.competition.startTime + this.competition.length) {
            // The scoreboard ignores the problem for any of the following
            // reasons:
            // The submission has already been handled,
            // The competition does not contain this problem, or
            // The problem was accepted after the contest was over.
            return;
          }
          if (responseData.status !== 'running') {        // if the verdict isn't "running"
            viewed.push(responseData.submissionId);       // note that we've seen this
          }
          for (var team of this.competition.teams) {
            if (team.users.indexOf(responseData.username) != -1) {
              // If the user that submitted the problem was on this team
              var problem: TeamProblemData = team.problemData[responseData.problemId];
              if (problem.status !== 'correct') {
                if (responseData.status === 'correct') {
                  problem.submitCount++;
                  problem.submitTime = Math.floor((responseData.submitTime - this.competition.startTime) / 60);
                  problem.penaltyTime = (problem.submitCount - 1) * 20;
                  problem.status = 'correct';
                } else if (responseData.status === 'running') {
                  problem.penaltyTime = problem.submitCount * 20;
                  problem.status = 'running';
                } else {
                  problem.submitCount++;
                  problem.penaltyTime = problem.submitCount * 20;
                  problem.status = 'incorrect';
                }
              }
            }
          }
          this.competitionSource.next(this.competition);
        }
    });
    var self = this;
    setTimeout(function() {
      self._websocketService.send({eventType: 'system_time'});
    }, 2000);
  }

  resetScoreboard() {
    this.competition = new Competition();
    this.competitionSource.next(this.competition);
  };

  fetchCompetition(cid: number) {
    this.getCompetition(cid).then(competition => {
      this.competition = competition;
      this.competitionSource.next(this.competition);
    });
  };

  getClientTimeOffset(): number {
    return this.clientTimeOffset;
  };

  createCompetition(competition: Competition, problems: Problem[]) : Promise<Competition> {
    var problemData = [];
    for (var i = 0; i < problems.length; i++) {
      problemData.push({
        label: String.fromCharCode("A".charCodeAt(0) + i),
        pid: problems[i].pid
      });
    }
    var formData = new URLSearchParams();
    formData.append('name', competition.name);
    formData.append('start_time', competition.startTime.toString());
    formData.append('length', competition.length.toString());
    formData.append('problems', JSON.stringify(problemData));
    formData.append('closed', competition.closed.toString());

    var headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');

    return new Promise((resolve, reject) => {
      this._http.post('/api/competitions', formData.toString(), { headers: headers }).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(new Competition());
        } else {
          resolve(undefined);
        }
      }, (err: Response) => {
        resolve(undefined);
      })
    });
  };

  getAllCompetitions() : Promise<Map<string, Competition[]>> {
    return new Promise((resolve, reject) => {
      this._http.get('/api/competitions').subscribe((res: Response) => {
        var competitions = new Map<string, Competition[]>();
        var data = res.json().data;
        if (res.status == 200) {
          for (var competitionType in data) {
            competitions[competitionType] = [];
            for (var i = 0; i < data[competitionType].length; i++) {
              // I'm unsure what is returned, going to check for all parameters     - John
              var competition = new Competition();
              if (data[competitionType][i]['cid'] !== undefined) competition['cid'] = data[competitionType][i]['cid'];
              if (data[competitionType][i]['closed'] !== undefined) competition['closed'] = data[competitionType][i]['closed'];
              if (data[competitionType][i]['length'] !== undefined) competition['length'] = data[competitionType][i]['length'];
              if (data[competitionType][i]['name'] !== undefined) competition['name'] = data[competitionType][i]['name'];
              if (data[competitionType][i]['registered'] !== undefined) competition['registered'] = data[competitionType][i]['registered'];
              if (data[competitionType][i]['startTime'] !== undefined) competition['startTime'] = data[competitionType][i]['startTime'];
              if (data[competitionType][i]['stop'] !== undefined) competition['stop'] = data[competitionType][i]['stop'];
              if (data[competitionType][i]['compProblems'] !== undefined) competition['compProblems'] = data[competitionType][i]['compProblems'];
              competitions[competitionType].push(competition);
            }
          }
        }
        resolve(competitions);
      }, (err: Response) => {
        var competitions = new Map<string, Competition[]>();
        competitions['ongoing'] = [];
        competitions['upcoming'] = [];
        competitions['past'] = [];
        resolve(competitions);
      });
    })
  };

  getCompetition(cid: number) : Promise<Competition> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/competitions/${cid}`).subscribe((res: Response) => {
        if (res.status === 200) {
          var competition = new Competition();
          var data = res.json().data;

          // Parse competition compProblems
          for (var key in data.compProblems) {
            var tempData = data.compProblems[key];
            var tempCompProblem = new CompetitionProblem();
            tempCompProblem.name = tempData.name;
            tempCompProblem.pid = tempData.pid;
            tempCompProblem.shortName = tempData.shortname;
            competition.compProblems[key] = tempCompProblem;
          }

          // Parse general competition data
          competition.cid = data.competition.cid;
          competition.closed = data.competition.closed;
          competition.length = data.competition.length;
          competition.name = data.competition.name;
          competition.registered = data.competition.registered;
          competition.startTime = data.competition.startTime;

          // Parse team data
          for (var i = 0; i < data.teams.length; i++) {
            var tempTeam = data.teams[i];
            var tempTeamData = new CompetitionTeam();
            tempTeamData.displayNames = tempTeam.display_names;
            tempTeamData.name = tempTeam.name;
            tempTeamData.problemData = tempTeam.problemData;
            tempTeamData.users = tempTeam.users;
            competition.teams.push(tempTeamData);
          }
          resolve(competition);
        } else {
          resolve(new Competition());
        }
      }, (err: Response) => {
        console.log('Failed to get the competition data!');
        resolve(new Competition());
      });
    });
  };

  updateCompetition(cid: number, name: string, start: number, stop: number, closed: boolean, compProblems: CompetitionProblem[]) : Promise<Competition> {
    return undefined;
  };

  deleteCompetition(cid: number) : Promise<boolean> {
    return undefined;
  };

  register(cid: number) : Promise<boolean> {
    return new Promise((resolve, reject) => {
      this._http.post(`/api/competitions/${cid}/register`, '').subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        resolve(false);
      });
    });
  };

  unregister(cid: number) : Promise<boolean> {
    return new Promise((resolve, reject) => {
      this._http.post(`/api/competitions/${cid}/unregister`, '').subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        resolve(false);
      });
    });
  };

  getCompetitionTeams(cid: number) : Promise<Map<string, SimpleUser[]>> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/competitions/${cid}/teams`).subscribe((res: Response) => {
        if (res.status === 200) {
          var data = res.json().data;
          var map = new Map<string, SimpleUser[]>();
          for (var team of data) {
            map[team] = data[team];
          }
          resolve(map);
        } else {
          resolve(new Map<string, SimpleUser[]>());
        }
      }, (err: Response) => {
        console.log('Failed to fetch the teams for the competition!');
        resolve(new Map<string, SimpleUser[]>());
      });
    });
  };

  updateCompetitionTeams(cid: number, team: Map<string, SimpleUser[]>): Promise<boolean> {
    return new Promise((resolve, reject) => {
      var formData = new FormData();
      formData.append('teams', JSON.stringify(team));
      this._http.put(`/api/competitions/${cid}/teams`, formData).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        console.log('Failed to update the teams for the competition!');
        resolve(false);
      });
    });
  }

  private problemIsInComp(problemId: number): boolean {
    for (var problem in this.competition.compProblems) {
      if (problemId === this.competition.compProblems[problem].pid) {
        return true;
      }
    }
    return false;
  };
}
