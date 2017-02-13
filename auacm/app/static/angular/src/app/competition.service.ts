import { Injectable } from '@angular/core';
import { Http, Request, Response } from '@angular/http';
import { Subject } from 'rxjs';

import { CompetitionProblem, Competition, CompetitionTeam } from './models/competition';

@Injectable()
export class CompetitionService {

  constructor(private _http: Http) { }

  createCompetition(name: string, start: number, stop: number, closed: boolean, compProblems: CompetitionProblem[]) : Promise<Competition> {
    return undefined;
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
    return undefined;
  };

  updateCompetition(cid: number, name: string, start: number, stop: number, closed: boolean, compProblems: CompetitionProblem[]) : Promise<Competition> {
    return undefined;
  };

  deleteCompetition(cid: number) : Promise<boolean> {
    return undefined;
  };

  register(cid: number) : Promise<boolean> {
    return undefined;
  };

  unregister(cid: number) : Promise<boolean> {
    return undefined;
  };

  getCompetitionTeams(cid: number) : Promise<Map<string, CompetitionTeam>> {
    return undefined;
  };

  updateCompetitionTeams(cid: number, team: CompetitionTeam) {
    return undefined;
  }
}
