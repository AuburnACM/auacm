export class BlogAuthor {
  public username: string;
  public display: string;

  constructor() {
    this.username = '';
    this.display = '';
  }

  deserialize(object: any): BlogAuthor {
    if (object['username'] !== undefined) {
      this.username = object['username'];
    }
    if (object['display'] !== undefined) {
      this.display = object['display'];
    }
    return this;
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

  deserialize(object: any): BlogPost {
    if (object['id'] !== undefined) {
      this.id = object['id'];
    }
    if (object['postTime'] !== undefined) {
      this.postTime = object['postTime'] * 1000;
    }
    if (object['title'] !== undefined) {
      this.title = object['title'];
    }
    if (object['subtitle'] !== undefined) {
      this.subtitle = object['subtitle']
    }
    if (object['body'] !== undefined) {
      this.body = object['body']
    }
    if (object['author'] !== undefined) {
      this.author = new BlogAuthor().deserialize(object['author']);
    }
    return this;
  }
}
