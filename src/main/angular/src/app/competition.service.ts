import { Injectable } from '@angular/core';
import { Http, Request, Response, Headers, URLSearchParams } from '@angular/http';
import { Subject } from 'rxjs/Subject';
import { HttpClient } from '@angular/common/http';

import { WebsocketService } from './websocket.service';

import { CompetitionProblem, Competition, CompetitionTeam, TeamProblemData, CompetitionTeamWrapper } from './models/competition';
import { DataWrapper } from './models/datawrapper';
import { RecentSubmission } from './models/submission';
import { Problem } from './models/problem';
import { SimpleUser, WebsocketRegisteredUser } from './models/user';
import { UrlEncodedHeader } from './models/service.utils';
import { environment } from './../environments/environment';
import { setTimeout } from 'timers';
import { MessageWrapper, SystemTimeMessage, CompetitionUsersMessage } from './models/message';

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
  public teamSource: Subject<Map<string, SimpleUser[]>> = new Subject<Map<string, SimpleUser[]>>();

  /**
   * The constructor for CompetitionService. This handles initializing the
   * connection with the websocket endpoint and handling the responses from
   * the websocket. It currently handles the system_time, status, new_user
   * websocket messages.
   */
  constructor(private _http: Http, private _httpClient: HttpClient,
      private _websocketService: WebsocketService) {
    // this._websocketService.connect(window.location.host + '/websocket').subscribe(data => {
    //     // update the client's time offset and scoreboard data
    //     const response = JSON.parse(data.data);
    //     const responseData = response.data;
    //     const viewed = [];
    //     if (response.eventType === 'system_time') {
    //       this.clientTimeOffset = responseData.milliseconds - Date.now();
    //       this.timeOffsetSource.next(this.clientTimeOffset);
    //     } else if (response.eventType === 'status' && this.competition.cid > 0) {
    //       if (viewed.indexOf(responseData.submissionId) > -1
    //           || !this.problemIsInComp(responseData.problemId)
    //           || responseData.submitTime > this.competition.startTime + this.competition.length) {
    //         // The scoreboard ignores the problem for any of the following
    //         // reasons:
    //         // The submission has already been handled,
    //         // The competition does not contain this problem, or
    //         // The problem was accepted after the contest was over.
    //         return;
    //       }
    //       if (responseData.status !== 'running') {        // if the verdict isn't "running"
    //         viewed.push(responseData.submissionId);       // note that we've seen this
    //       }
    //       for (const team of this.competition.teams) {
    //         if (team.users.indexOf(responseData.username) !== -1) {
    //           // If the user that submitted the problem was on this team
    //           const problem: TeamProblemData = team.problemData[responseData.problemId];
    //           if (problem.status !== 'correct') {
    //             if (responseData.status === 'correct') {
    //               problem.submitCount++;
    //               problem.submitTime = Math.floor((responseData.submitTime - this.competition.startTime) / 60);
    //               problem.penaltyTime = (problem.submitCount - 1) * 20;
    //               problem.status = 'correct';
    //             } else if (responseData.status === 'running') {
    //               problem.penaltyTime = problem.submitCount * 20;
    //               problem.status = 'running';
    //             } else {
    //               problem.submitCount++;
    //               problem.penaltyTime = problem.submitCount * 20;
    //               problem.status = 'incorrect';
    //             }
    //           }
    //         }
    //       }
    //       this.competitionSource.next(this.competition);
    //     } else if (response.eventType === 'new_user') {
    //       // Handles a new user registering for a competition
    //       // Probably should be renamed to comp_register or something
    //       const tempRegisteredUser = new WebsocketRegisteredUser();
    //       tempRegisteredUser.cid = responseData.cid;
    //       tempRegisteredUser.display = responseData.user.display;
    //       tempRegisteredUser.username = responseData.user.username;
    //       this.competitionTeamSource.next(tempRegisteredUser);
    //     }
    // });
    const self = this;
    setTimeout(function() {
      // self._websocketService.send({eventType: 'system_time'});
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
    const formData = new FormData();
    formData.append('name', competition.name);
    formData.append('startTime', competition.startTime.toString());
    formData.append('length', competition.length.toString());
    for (const problem of problems) {
      formData.append('problems', `${problem.pid}`);
    }
    formData.append('closed', `${competition.closed}`);

    return new Promise((resolve, reject) => {
      this._httpClient.post<DataWrapper<Competition>>(`${environment.apiUrl}/competitions`, formData, { withCredentials: true }).subscribe(data => {
        resolve(new Competition().deserialize(data.data));
      }, (err: Response) => {
        reject(err);
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
      this._httpClient.get<DataWrapper<Map<string, Competition>>>(`${environment.apiUrl}/competitions`, {withCredentials: true}).subscribe(data => {
        const map = data.data;
        const compMap = new Map<string, Competition[]>();
        for (const type in map) {
          compMap[type] = [];
          for (const comp of map[type]) {
            compMap[type].push(new Competition().deserialize(comp));
          }
        }
        if (compMap['past'] === undefined) {
          compMap['past'] = [];
        }
        if (compMap['ongoing'] === undefined) {
          compMap['ongoing'] = [];
        }
        if (compMap['upcoming'] === undefined) {
          compMap['upcoming'] = [];
        }
        resolve(compMap);
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  getCompetition(cid: number): Promise<Competition> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<DataWrapper<Competition>>(`${environment.apiUrl}/competitions/${cid}`, {withCredentials: true}).subscribe(data => {
        resolve(new Competition().deserialize(new Competition().deserialize(data.data)));
      });
    });
  }

  updateCompetition(competition: Competition, problems: Problem[]): Promise<Competition> {
    const problemData = [];
    const formData = new FormData();
    formData.append('name', competition.name);
    formData.append('startTime', competition.startTime.toString());
    formData.append('length', competition.length.toString());
    for (const problem of problems) {
      formData.append('problems', `${problem.pid}`);
    }
    formData.append('closed', `${competition.closed}`);

    return new Promise((resolve, reject) => {
      this._httpClient.post<DataWrapper<Competition>>(`${environment.apiUrl}/competitions/${competition.cid}`, formData, { withCredentials: true }).subscribe(data => {
        resolve(new Competition().deserialize(data.data));
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  deleteCompetition(cid: number): Promise<any> {
    return new Promise((resolve, reject) => {
      this._httpClient.delete(`${environment.apiUrl}/competitions/${cid}`, { withCredentials: true }).subscribe(() => {
        resolve();
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  register(cid: number): Promise<any> {
    return new Promise((resolve, reject) => {
      this._httpClient.post(`${environment.apiUrl}/competitions/${cid}/register`, undefined, { withCredentials: true}).subscribe(() => {
        resolve();
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  unregister(cid: number): Promise<any> {
    return new Promise((resolve, reject) => {
      this._httpClient.post(`${environment.apiUrl}/competitions/${cid}/unregister`, undefined, { withCredentials: true }).subscribe(() => {
        resolve();
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  getCompetitionTeams(cid: number): Promise<Map<string, SimpleUser[]>> {
    return new Promise((resolve, reject) => {
      this._httpClient.get<CompetitionTeamWrapper>(`${environment.apiUrl}/competitions/${cid}/teams`, { withCredentials: true }).subscribe(data => {
        resolve(data.data);
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  updateCompetitionTeams(cid: number, team: Map<string, SimpleUser[]>): Promise<any> {
    console.log(team);
    return new Promise((resolve, reject) => {
      this._httpClient.post(`${environment.apiUrl}/competitions/${cid}/teams`, {teams: team}, { withCredentials: true }).subscribe(data => {
        resolve();
      }, (err: Response) => {
        reject(err);
      });
    });
  }

  updateCompetitionTeamsSocket(cid: number, team: Map<string, SimpleUser[]>) {
    this._websocketService.sendMessage(`/api/competitions/${cid}/teams`, {teams: team});
  }

  watch(cid: number) {
    setTimeout(function() {
      this._websocketService.stompOn(`/competitions/${cid}`, {'systemTime': [this.handleSystemTime.bind(this)],
          'compUsers': [this.handleUpdateUsers.bind(this)]});
    }.bind(this), 1000);
  }

  stopWatching(cid: number) {
    this._websocketService.stompOff(`/competitions/${cid}`);
  }

  private handleSystemTime(data: SystemTimeMessage) {
      this.clientTimeOffset = data.systemTime - Date.now();
      this.timeOffsetSource.next(this.clientTimeOffset);
  }

  private handleUpdateUsers(data: CompetitionUsersMessage) {
    this.teamSource.next(data.teams);
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
