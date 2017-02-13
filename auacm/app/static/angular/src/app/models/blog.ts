export class BlogPost {
	constructor() {
		this.title = "";
		this.subtitle = "";
		this.body = "";
	}
	id: number;
	postTime: number;
	title: string;
	subtitle: string;
	body: string;
	author: BlogAuthor;
}

export class BlogAuthor {
	constructor() {
		this.username = "";
		this.display = "";
	}
	username: string;
	display: string;
}