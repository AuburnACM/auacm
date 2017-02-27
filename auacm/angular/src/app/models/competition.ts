import { BlogAuthor } from './blog';

export class Competition {
  public cid: number;
  public name: string;
  public startTime: number;
  public stop: number;
  public closed: boolean;
  public length: number;
  public compProblems: Map<string, CompetitionProblem>;
  public teams: CompetitionTeam[];
  public registered: boolean;
  public timeRemaining: number;

  constructor() {
    this.cid = 0;
    this.name = '';
    this.startTime = 0;
    this.stop = 0;
    this.length = 0;
    this.closed = false;
    this.compProblems = new Map<string, CompetitionProblem>();
    this.teams = [];
    this.registered = false;
    this.timeRemaining = 0;
  }
}

export class CompetitionProblem {
  public pid: number;
  public name: string;
  public shortName: string;

  constructor() {
    this.pid = 0;
    this.name = '';
    this.shortName = '';
  }
}

export class TeamProblemData {
  public label: string;
  public status: string;
  public submitCount: number;
  public submitTime: number;
  public penaltyTime: number;

  constructor() {
    this.label = '';
    this.status = '';
    this.submitCount = 0;
    this.submitTime = 0;
    this.penaltyTime = 0;
  }
}

export class CompetitionTeam {
  public name: string;
  public displayNames: string[];
  public users: string[];
  public rank: number;
  public solved: number;
  public time: number;
  public problemData: Map<string, TeamProblemData>;

  constructor() {
    this.name = '';
    this.displayNames = [];
    this.users = [];
    this.problemData = new Map<string, TeamProblemData>();
    this.rank = 0;
    this.solved = 0;
    this.time = 0;
  }
}
