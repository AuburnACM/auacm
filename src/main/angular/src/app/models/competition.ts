import { BlogAuthor } from './blog';
import { SimpleUser } from 'app/models/user';

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

  public deserialize(object: any): Competition {
    this.deserializeMainObject(object);
    if (object['competition'] !== undefined) {
      this.deserializeMainObject(object['competition']);
    }
    if (object['compProblems'] !== undefined) {
      for (const label in object['compProblems']) {
        this.compProblems[label] = new CompetitionProblem().deserialize(object['compProblems'][label]);
      }
    }
    if (object['teams'] !== undefined) {
      for (const team of object['teams']) {
        this.teams.push(new CompetitionTeam().deserialize(team));
      }
    }
    return this;
  }

  private deserializeMainObject(object: any) {
    if (object['cid'] !== undefined) {
      this.cid = object['cid'];
    }
    if (object['name'] !== undefined) {
      this.name = object['name'];
    }
    if (object['startTime'] !== undefined) {
      this.startTime = object['startTime'];
    }
    if (object['length'] !== undefined) {
      this.length = object['length'];
    }
    if (object['registered'] !== undefined) {
      this.registered = object['registered'];
    }
    this.stop = this.startTime + this.length;
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

  public deserialize(object: any): CompetitionProblem {
    if (object['pid'] !== undefined) {
      this.pid = object['pid'];
    }
    if (object['name'] !== undefined) {
      this.name = object['name'];
    }
    if (object['shortName'] !== undefined) {
      this.shortName = object['shortName'];
    }
    return this;
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

  public deserialize(object: any): TeamProblemData {
    if (object['label'] !== undefined) {
      this.label = object['label'];
    }
    if (object['status'] !== undefined) {
      this.status = object['status'];
    }
    if (object['submitCount'] !== undefined) {
      this.submitCount = object['submitCount'];
    }
    if (object['submitTime'] !== undefined) {
      this.submitTime = object['submitTime'];
    }
    return this;
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

  public deserialize(object: any): CompetitionTeam {
    if (object['name'] !== undefined) {
      this.name = object['name'];
    }
    if (object['displayNames'] !== undefined) {
      for (const name of object['displayNames']) {
        this.displayNames.push(name);
      }
    }
    if (object['users'] !== undefined) {
      for (const name of object['users']) {
        this.users.push(name);
      }
    }
    if (object['problemData'] !== undefined) {
      for (const pid in object['problemData']) {
        this.problemData[pid] = new TeamProblemData().deserialize(object['problemData'][pid]);
      }
    }
    return this;
  }
}

export class CompetitionTeamWrapper {
  public data: Map<string, SimpleUser[]>;

  constructor() {
    this.data = new Map<string, SimpleUser[]>();
  }

  public deserialize(object: any): CompetitionTeamWrapper {
    for (const team in object) {
      this.data[team] = [];
      for (const user of object[team]) {
        this.data[team].push(new SimpleUser().deserialize(user));
      }
    }
    return this;
  }
}
