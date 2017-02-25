export class Problem {
  public pid: number;
  public name: string;
  public shortName: string;
  public appeared: string;
  public difficulty: number;
  public added: number;
  public compRelease: number;
  public description: string;
  public inputDesc: string;
  public outputDesc: string;
  public sampleCases: SampleCase[];
  public solved: boolean;
  public url: string;

  constructor() {
    this.pid = -1;
    this.name = '';
    this.shortName = '';
    this.appeared = '';
    this.difficulty = 0;
    this.added = -1;
    this.compRelease = 0;
    this.description = '';
    this.inputDesc = '';
    this.outputDesc = '';
    this.sampleCases = [];
    this.solved = false;
    this.url = '';
  }
}

export class SampleCase {
  public pid: number;
  public caseNum: number;
  public input: string;
  public output: string;

  constructor() {
    this.pid = 0;
    this.caseNum = 0;
    this.input = '';
    this.output = '';
  }
}
