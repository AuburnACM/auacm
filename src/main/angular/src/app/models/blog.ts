export class BlogAuthor {
  public username: string;
  public display: string;

  constructor() {
    this.username = '';
    this.display = '';
  }
}

export class BlogPost {
  public id: number;
  public postTime: number;
  public title: string;
  public subtitle: string;
  public body: string;
  public author: BlogAuthor;
  public resized: boolean;
  public expanded: boolean;

  constructor() {
    this.title = '';
    this.subtitle = '';
    this.body = '';
    this.author = new BlogAuthor();
    this.resized = false;
    this.expanded = false;
  }
}
