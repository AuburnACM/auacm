import { Component, OnInit } from '@angular/core';

import { CompetitionService } from '../competition.service';

import { Competition, CompetitionProblem } from '../models/competition';
import { UserData } from '../models/user';

@Component({
  selector: 'app-competitions',
  templateUrl: './competitions.component.html',
  styleUrls: ['./competitions.component.css']
})
export class CompetitionsComponent implements OnInit {

  competitions: Map<string, Competition[]> = new Map<string, Competition[]>(); 

  // Bind to AuthService
  user: UserData = new UserData();

  // Needs sorting eventually
  constructor(private _competitionService: CompetitionService) { 
    this.competitions['ongoing'] = [];
    this.competitions['upcoming'] = [];
    this.competitions['past'] = [];
  }

  ngOnInit() {
    this._competitionService.getAllCompetitions().then(competitions => {
      this.competitions = competitions;
    });
  }

  register(competition: Competition) {

  }

}
