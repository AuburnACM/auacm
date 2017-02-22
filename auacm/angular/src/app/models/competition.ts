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
		this.teams = [];
		this.registered = false;
		this.timeRemaining = 0;
	}
	cid: number;
	name: string;
	startTime: number;
	stop: number;
	closed: boolean;
	length: number;
	compProblems: Map<string, CompetitionProblem>;
	teams: CompetitionTeam[];
	registered: boolean;
	timeRemaining: number;
}

export class CompetitionProblem {
	constructor() {
		this.pid = 0;
		this.name = "";
		this.shortName = "";
	}
	pid: number;
	name: string;
	shortName: string;
}

export class TeamProblemData {
	constructor() {
		this.label = "";
		this.status = "";
		this.submitCount = 0;
		this.submitTime = 0;
		this.penaltyTime = 0;
	}
	label: string;
	status: string;
	submitCount: number;
	submitTime: number;
	penaltyTime: number;
}

export class CompetitionTeam {
	constructor() {
		this.name = "";
		this.displayNames = [];
		this.users = [];
		this.problemData = new Map<string, TeamProblemData>();
		this.rank = 0;
		this.solved = 0;
		this.time = 0;
	}
	name: string;
	displayNames: string[];
	users: string[];
	rank: number;
	solved: number;
	time: number;
	problemData: Map<string, TeamProblemData>;
}
