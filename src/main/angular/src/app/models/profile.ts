export class SubmissionActivity {
  public correct: boolean;
  public name: string;
  public pid: number;
  public shortName: string;
  public submissionCount: number;
  public submissionIds: number[];

  constructor() {
    this.correct = false;
    this.submissionCount = 0;
    this.shortName = '';
    this.name = '';
  }

  deserialize(object: any): SubmissionActivity {
    if (object['correct'] !== undefined) {
      this.correct = object['correct'];
    }
    if (object['name'] !== undefined) {
      this.correct = object['name'];
    }
    if (object['pid'] !== undefined) {
      this.correct = object['pid'];
    }
    if (object['shortName'] !== undefined) {
      this.correct = object['shortName'];
    }
    if (object['submissionCount'] !== undefined) {
      this.correct = object['submissionCount'];
    }
    if (object['submissionIds'] !== undefined) {
      this.correct = object['submissionIds'];
    }
    return this;
  }
}

export class CompetitionActivity {
  public cid: number;
  public compName: string;
  public teamName: string;
  public teamSize: number;

  constructor() {
    this.cid = 0;
    this.compName = '';
    this.teamName = '';
    this.teamSize = 1;
  }

  deserialize(object: any): CompetitionActivity {
    if (object['cid'] !== undefined) {
      this.cid = object['cid'];
    }
    if (object['compName'] !== undefined) {
      this.compName = object['compName'];
    }
    if (object['teamName'] !== undefined) {
      this.teamName = object['teamName'];
    }
    if (object['teamSize'] !== undefined) {
      this.teamSize = object['teamSize'];
    }
    return this;
  }
}

export class BlogPostActivity {
  public id: number;
  public postTime: number;
  public subtitle: string;
  public title: string;

  constructor() {
    this.id = 0;
    this.title = '';
    this.subtitle = '';
    this.postTime = 0;
  }

  deserialize(object: any): BlogPostActivity {
    if (object['id'] !== undefined) {
      this.id = object['postTime'];
    }
    if (object['postTime'] !== undefined) {
      this.postTime = object['postTime'] * 1000;
    }
    if (object['subtitle'] !== undefined) {
      this.subtitle = object['subtitle'];
    }
    if (object['title'] !== undefined) {
      this.title = object['title'];
    }
    return this;
  }
}

export class UserProfile {
  public displayName: string;
  public username: string;
  public problemsSolved: number;
  public recentAttempts: SubmissionActivity[];
  public recentCompetitions: CompetitionActivity[];
  public recentBlogPosts: BlogPostActivity[];

  constructor() {
    this.displayName = '';
    this.username = '';
    this.problemsSolved = 0;
    this.recentAttempts = [];
    this.recentCompetitions = [];
    this.recentBlogPosts = [];
  }

  deserialize(object: any): UserProfile {
    if (object['displayName'] !== undefined) {
      this.displayName = object['displayName'];
    }
    if (object['username'] !== undefined) {
      this.username = object['username'];
    }
    if (object['problemsSolved'] !== undefined) {
      this.displayName = object['problemsSolved'];
    }
    if (object['recentAttempts'] !== undefined) {
      for (const temp of object['recentAttempts']) {
        this.recentAttempts.push(new SubmissionActivity().deserialize(temp));
      }
    }
    if (object['recentCompetitions'] !== undefined) {
      for (const temp of object['recentCompetitions']) {
        this.recentCompetitions.push(new CompetitionActivity().deserialize(temp));
      }
    }
    if (object['recentBlogPosts'] !== undefined) {
      for (const temp of object['recentBlogPosts']) {
        this.recentBlogPosts.push(new BlogPostActivity().deserialize(temp));
      }
    }
    return this;
  }
}
