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

  deserialize(pid: number, object: any): SampleCase {
    this.pid = pid;
    this.caseNum = object.caseNum;
    this.input = object.caseNum;
    this.output = object.output;
    return this;
  }
}

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

  deserialize(object: any): Problem {
    this.pid = object.pid;
    this.name = object.name;
    this.shortName = object.shortName;
    this.appeared = object.appeared;
    this.difficulty = object.difficulty !== undefined ? object.difficulty : 0;
    this.added = object.added !== undefined ? object.added : 0;
    this.compRelease = object.compRelease !== undefined ? object.compRelease : 0;
    this.description = object.description !== undefined ? object.description : '';
    this.inputDesc = object.inputDesc !== undefined ? object.inputDesc : '';
    this.outputDesc = object.outputDesc !== undefined ? object.outputDesc : '';
    this.solved = object.solved !== undefined;
    if (object.sampleCases !== undefined) {
      for (const sampleCase of object.sampleCases) {
        this.sampleCases.push(new SampleCase().deserialize(this.pid, sampleCase));
      }
    }
    this.url = object.url !== undefined ? object.url : '';
    return this;
  }
}
