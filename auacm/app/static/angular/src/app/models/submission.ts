export class SubmissionData {
	problem: string;
	fileName: string;
	status: string;
	statusDescription: string;
}

export class RecentSubmission {
	constructor() {
		this.fileType = '';
		this.jobId = -1;
		this.pid = -1;
		this.status = '';
		this.submitTime = -1;
		this.username = '';
		this.statusDescription = '';
		this.fileName = '';
	}
	fileType: string;
	jobId: number;
	pid: number;
	status: string;
	submitTime: number;
	username: string;
	statusDescription: string;
	fileName: string;
}