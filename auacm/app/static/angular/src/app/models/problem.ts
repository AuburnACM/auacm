export class Problem {
	constructor() {
		this.pid = -1;
		this.name = "";
		this.shortName = "";
		this.appeared ="";
		this.difficulty = -1;
		this.added = -1;
		this.compRelease = -1;
		this.description = "";
		this.inputDesc = "";
		this.outputDesc = "";
		this.sampleCases = [];
		this.solved = false;
		this.url = "";
	};
	pid: number;
	name: string;
	shortName: string;
	appeared: string;
	difficulty: number;
	added: number;
	compRelease: number;
	description: string;
	inputDesc: string;
	outputDesc: string;
	sampleCases: SampleCase[];
	solved: boolean;
	url: string;
}

export class SampleCase {
	constructor() {
		this.pid = 0;
		this.caseNum = 0;
		this.input = "";
		this.output = "";
	}
	pid: number;
	caseNum: number;
	input: string;
	output: string;
}