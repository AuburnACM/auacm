export class SubmissionData {
  public problem: string;
  public fileName: string;
  public status: string;
  public statusDescription: string;
}

export class RecentSubmission {
  public fileType: string;
  public jobId: number;
  public pid: number;
  public status: string;
  public submitTime: number;
  public username: string;
  public statusDescription: string;
  public fileName: string;

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
}
