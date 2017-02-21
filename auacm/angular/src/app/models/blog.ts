export class BlogPost {
	constructor() {
		this.title = "";
		this.subtitle = "";
		this.body = "";
		this.author = new BlogAuthor();
		this.resized = false;
	}
	id: number;
	postTime: number;
	title: string;
	subtitle: string;
	body: string;
	author: BlogAuthor;
	resized: boolean;
}

export class BlogAuthor {
	constructor() {
		this.username = "";
		this.display = "";
	}
	username: string;
	display: string;
}