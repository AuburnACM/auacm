export class UserData {
  public username: string;
  public password: string;
  public displayName: string;
  public isAdmin: boolean;
  public loggedIn: boolean;

  constructor() {
    this.username = '';
    this.password = '';
    this.displayName = '';
    this.isAdmin = false;
    this.loggedIn = false;
  }
}

export class RankData {
  public displayName: string;
  public rank: number;
  public solved: number;
  public username: string;

  constructor() {
    this.displayName = '';
    this.rank = -1;
    this.solved = 0;
    this.username = '';
  }
}

export class SimpleUser {
  public display: string;
  public username: string;

  constructor() {
    this.display = '';
    this.username = '';
  }
}

export class WebsocketRegisteredUser {
  public display: string;
  public username: string;
  public cid: number;

  constructor() {
    this.display = '';
    this.username = '';
    this.cid = 0;
  }
}
