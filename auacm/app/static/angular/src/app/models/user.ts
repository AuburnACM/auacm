export class UserData {
	constructor() {
		this.username = "";
		this.password = "";
		this.displayName = "Log In";
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