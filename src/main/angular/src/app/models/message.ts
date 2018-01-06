import { SimpleUser } from "app/models/user";

export interface Message {}

export class SystemTimeMessage implements Message {
  public systemTime: number;

  constructor() {
    this.systemTime = Date.now();
  }

  public deserialize(object: any): SystemTimeMessage {
    if (object['systemTime'] !== undefined) {
      this.systemTime = object['systemTime'];
    }
    return this;
  }
}

export class CompetitionUsersMessage implements Message {
  public teams: Map<string, SimpleUser[]>;

  constructor() {
    this.teams = new Map<string, SimpleUser[]>();
  }

  public deserialize(object: any): CompetitionUsersMessage {
    for (const teamName in object) {
      this.teams[teamName] = [];
      for (const simpleUser of object[teamName]) {
        this.teams[teamName].push(new SimpleUser().deserialize(simpleUser));
      }
    }
    return this;
  }
}

export class MessageWrapper {
  public eventType: string;
  public data: Message;

  constructor() {
    this.eventType = 'undefined';
    this.data = {};
  }

  deserialize(object: any): MessageWrapper {
    if (object['eventType'] !== undefined) {
      this.eventType = object['eventType'];
    }
    if (object['data'] !== undefined) {
      if (this.eventType === 'systemTime') {
        this.data = new SystemTimeMessage().deserialize(object['data']);
      } else if (this.eventType === 'compUsers') {
        this.data = new CompetitionUsersMessage().deserialize(object['data']);
      } else {
        this.data = object['data'];
      }
    }
    return this;
  }
}
