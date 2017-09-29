export class UserData {
  public username: string;
  public password: string;
  public displayName: string;
  public isAdmin: boolean;
  public loggedIn: boolean;
  public permissions: String[];

  constructor() {
    this.username = '';
    this.password = '';
    this.displayName = '';
    this.isAdmin = false;
    this.loggedIn = false;
  }

  deserialize(object: any): UserData {
    this.username = object.username;
    this.displayName = object.displayName;
    this.isAdmin = object.isAdmin === 1;
    this.permissions = object.permissions;
    return this;
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

  deserialize(object: any): RankData {
    this.displayName = object.displayName;
    this.rank = object.rank;
    this.solved = object.solved;
    this.username = object.username;
    return this;
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
