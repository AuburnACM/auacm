export class UserData {
	constructor() {
		this.username = "";
		this.password = "";
		this.displayName = "";
		this.isAdmin = false;
		this.loggedIn = false;
	};
	username: string;
	password: string;
	displayName: string;
	isAdmin: boolean;
	loggedIn: boolean;
};

export class RankData {
	constructor() {
		this.displayName = "";
		this.rank = -1;
		this.solved = 0;
		this.username = "";
	};
	displayName: string;
	rank: number;
	solved: number;
	username: string;
};

export class SimpleUser {
	constructor() {
		this.display = '';
		this.username = '';
	}
	display: string;
	username: string;
}

export class WebsocketRegisteredUser {
	constructor() {
		this.display = '';
		this.username = '';
		this.cid = 0;
	}
	display: string;
	username: string;
	cid: number;
}