import { BlogAuthor } from './blog';

export class Competition {
	constructor() {
		this.cid = 0;
		this.name = "";
		this.startTime = 0;
		this.stop = 0;
		this.length = 0;
		this.closed = false;
		this.compProblems = new Map<string, CompetitionProblem>();
		this.registered = false;
	}
	cid: number;
	name: string;
	startTime: number;
	stop: number;
	closed: boolean;
	length: number;
	compProblems: Map<string, CompetitionProblem>;
	registered: boolean;
}

export class CompetitionProblem {
	constructor() {
		this.cid = 0;
		this.pid = 0;
		this.label = "";
		this.name = "";
		this.shortName = "";
	}
	cid: number;
	pid: number;
	label: string;
	name: string;
	shortName: string;
}

export class CompetitionTeam {
	constructor() {
		this.members = [];
		this.name = "";
	}
	name: string;
	members: BlogAuthor[];
}

