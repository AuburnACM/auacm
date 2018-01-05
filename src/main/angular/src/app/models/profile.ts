export class SubmissionActivity {
  public correct: boolean;
  public name: string;
  public pid: number;
  public shortname: string;
  public submissionCount: number;
  public submissionIds: number[];

  constructor() {
    this.correct = false;
    this.submissionCount = 0;
    this.shortname = '';
    this.name = '';
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
}
