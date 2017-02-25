import { Injectable } from '@angular/core';
import { Http, Request, Response, Headers, URLSearchParams } from '@angular/http';
import { Subject } from 'rxjs/Subject';

import { WebsocketService } from './websocket.service';

import { CompetitionProblem, Competition, CompetitionTeam, TeamProblemData } from './models/competition';
import { RecentSubmission } from './models/submission';
import { Problem } from './models/problem';
import { SimpleUser, WebsocketRegisteredUser } from './models/user';


/**
 * This class manages the connection to the competition part of the backend.
 * You can add, edit, or remove competitions, update teams, and handle websocket
 * messages about competitions.
 *
 * Websocket messages:
 *   - system_time
 *   - status
 *   - new_user
 */
@Injectable()
export class CompetitionService {

  // Websocket subject for client time offset
  public clientTimeOffset = 0;
  public timeOffsetSource: Subject<number> = new Subject<number>();

  // Websocket subject for competition data
  public competition: Competition = new Competition();
  public competitionSource: Subject<Competition> = new Subject<Competition>();

  // Websocket subject for competition teams
  public competitionTeamSource: Subject<WebsocketRegisteredUser> = new Subject<WebsocketRegisteredUser>();

  /**
   * The constructor for CompetitionService. This handles initializing the
   * connection with the websocket endpoint and handling the responses from
   * the websocket. It currently handles the system_time, status, new_user
   * websocket messages.
   */
  constructor(private _http: Http, private _websocketService: WebsocketService) {
    this._websocketService.connect(window.location.host + '/websocket').subscribe(data => {
        // update the client's time offset and scoreboard data
        const response = JSON.parse(data.data);
        const responseData = response.data;
        const viewed = [];
        if (response.eventType === 'system_time') {
          this.clientTimeOffset = responseData.milliseconds - Date.now();
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
          for (const team of this.competition.teams) {
            if (team.users.indexOf(responseData.username) !== -1) {
              // If the user that submitted the problem was on this team
              const problem: TeamProblemData = team.problemData[responseData.problemId];
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
        } else if (response.eventType === 'new_user') {
          // Handles a new user registering for a competition
          // Probably should be renamed to comp_register or something
          const tempRegisteredUser = new WebsocketRegisteredUser();
          tempRegisteredUser.cid = responseData.cid;
          tempRegisteredUser.display = responseData.user.display;
          tempRegisteredUser.username = responseData.user.username;
          this.competitionTeamSource.next(tempRegisteredUser);
        }
    });
    const self = this;
    setTimeout(function() {
      self._websocketService.send({eventType: 'system_time'});
    }, 2000);
  }

  /**
   * Resets the scoreboard to the default one. This is typically called when
   * a user stops viewing a scoreboard for a competition.
   */
  resetScoreboard() {
    this.competition = new Competition();
    this.competitionSource.next(this.competition);
  }

  /**
   * Fetches the competition details for a competition.
   *
   * @param cid - the competition id
   */
  fetchCompetition(cid: number) {
    this.getCompetition(cid).then(competition => {
      this.competition = competition;
      this.competitionSource.next(this.competition);
    });
  }

  /**
   * Returns the client time offset for a competition.
   *
   * @returns the client-side time offset of a competition.
   */
  getClientTimeOffset(): number {
    return this.clientTimeOffset;
  }

  /**
   * Creates a new competition.
   *
   * @param competition - the competition to create
   * @param problems - the problems for the competition
   *
   * @returns the new competition
   */
  createCompetition(competition: Competition, problems: Problem[]): Promise<Competition> {
    const problemData = [];
    for (let i = 0; i < problems.length; i++) {
      problemData.push({
        label: String.fromCharCode('A'.charCodeAt(0) + i),
        pid: problems[i].pid
      });
    }
    const formData = new URLSearchParams();
    formData.append('name', competition.name);
    formData.append('start_time', competition.startTime.toString());
    formData.append('length', competition.length.toString());
    formData.append('problems', JSON.stringify(problemData));
    // in python, a non empty string that is parsed will be true,
    // while an empty string will be false.
    formData.append('closed', competition.closed ? '1' : '');

    const headers = new Headers();
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
      });
    });
  }

  /**
   * Returns a map of comptitions and their data.
   *
   * @returns a map of competitions
   */
  getAllCompetitions(): Promise<Map<string, Competition[]>> {
    return new Promise((resolve, reject) => {
      this._http.get('/api/competitions').subscribe((res: Response) => {
        const competitions = new Map<string, Competition[]>();
        const data = res.json().data;
        if (res.status === 200) {
          const competitionTypes = Object.keys(data);
          for (let i = 0; i < competitionTypes.length; i++) {
            const competitionType = competitionTypes[i];
            competitions[competitionType] = [];
            for (let j = 0; j < data[competitionType].length; j++) {
              // I'm unsure what is returned, going to check for all parameters     - John
              const competition = new Competition();
              if (data[competitionType][j]['cid'] !== undefined) {
                competition['cid'] = data[competitionType][j]['cid'];
              }
              if (data[competitionType][j]['closed'] !== undefined) {
                competition['closed'] = data[competitionType][j]['closed'];
              }
              if (data[competitionType][j]['length'] !== undefined) {
                competition['length'] = data[competitionType][j]['length'];
              }
              if (data[competitionType][j]['name'] !== undefined) {
                competition['name'] = data[competitionType][j]['name'];
              }
              if (data[competitionType][j]['registered'] !== undefined) {
                competition['registered'] = data[competitionType][j]['registered'];
              }
              if (data[competitionType][j]['startTime'] !== undefined) {
                competition['startTime'] = data[competitionType][j]['startTime'];
              }
              if (data[competitionType][j]['stop'] !== undefined) {
                competition['stop'] = data[competitionType][j]['stop'];
              }
              if (data[competitionType][j]['compProblems'] !== undefined) {
                competition['compProblems'] = data[competitionType][j]['compProblems'];
              }
              competitions[competitionType].push(competition);
            }
          }
        }
        resolve(competitions);
      }, (err: Response) => {
        const competitions = new Map<string, Competition[]>();
        competitions['ongoing'] = [];
        competitions['upcoming'] = [];
        competitions['past'] = [];
        resolve(competitions);
      });
    });
  }

  getCompetition(cid: number): Promise<Competition> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/competitions/${cid}`).subscribe((res: Response) => {
        if (res.status === 200) {
          const competition = new Competition();
          const data = res.json().data;

          // Parse competition compProblems
          const keys = Object.keys(data.compProblems);
          for (let i = 0; i < keys.length; i++) {
            const tempData = data.compProblems[keys[i]];
            const tempCompProblem = new CompetitionProblem();
            tempCompProblem.name = tempData.name;
            tempCompProblem.pid = tempData.pid;
            tempCompProblem.shortName = tempData.shortname;
            competition.compProblems[keys[i]] = tempCompProblem;
          }

          // Parse general competition data
          competition.cid = data.competition.cid;
          competition.closed = data.competition.closed;
          competition.length = data.competition.length;
          competition.name = data.competition.name;
          competition.registered = data.competition.registered;
          competition.startTime = data.competition.startTime;

          // Parse team data
          for (let i = 0; i < data.teams.length; i++) {
            const tempTeam = data.teams[i];
            const tempTeamData = new CompetitionTeam();
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
  }

  updateCompetition(competition: Competition, problems: Problem[]): Promise<Competition> {
    const problemData = [];
    for (let i = 0; i < problems.length; i++) {
      problemData.push({
        label: String.fromCharCode('A'.charCodeAt(0) + i),
        pid: problems[i].pid
      });
    }
    const formData = new URLSearchParams();
    formData.append('name', competition.name);
    formData.append('start_time', competition.startTime.toString());
    formData.append('length', competition.length.toString());
    formData.append('problems', JSON.stringify(problemData));
    // in python, a non empty string that is parsed will be true,
    // while an empty string will be false.
    formData.append('closed', competition.closed ? '1' : '');

    const headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');

    return new Promise((resolve, reject) => {
      this._http.put(`/api/competitions/${competition.cid}`,
          formData.toString(), { headers: headers }).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(new Competition());
        } else {
          resolve(undefined);
        }
      }, (err: Response) => {
        resolve(undefined);
      });
    });
  }

  deleteCompetition(cid: number): Promise<boolean> {
    // TODO Add this functionallity to the front and backend
    return undefined;
  }

  register(cid: number): Promise<boolean> {
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
  }

  unregister(cid: number): Promise<boolean> {
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
  }

  getCompetitionTeams(cid: number): Promise<Map<string, SimpleUser[]>> {
    return new Promise((resolve, reject) => {
      this._http.get(`/api/competitions/${cid}/teams`).subscribe((res: Response) => {
        if (res.status === 200) {
          const data = res.json().data;
          const map = new Map<string, SimpleUser[]>();
          const teams = Object.keys(data);
          for (let i = 0; i < teams.length; i++) {
            map[teams[i]] = data[teams[i]];
          }
          resolve(map);
        } else {
          resolve(new Map<string, SimpleUser[]>());
        }
      }, (err: Response) => {
        resolve(new Map<string, SimpleUser[]>());
      });
    });
  }

  updateCompetitionTeams(cid: number, team: Map<string, string[]>): Promise<boolean> {
    return new Promise((resolve, reject) => {
      const formData = new FormData();
      formData.append('teams', JSON.stringify(team));
      this._http.put(`/api/competitions/${cid}/teams`, formData).subscribe((res: Response) => {
        if (res.status === 200) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (err: Response) => {
        resolve(false);
      });
    });
  }

  private problemIsInComp(problemId: number): boolean {
    for (const problem in this.competition.compProblems) {
      if (problemId === this.competition.compProblems[problem].pid) {
        return true;
      }
    }
    return false;
  }
}
