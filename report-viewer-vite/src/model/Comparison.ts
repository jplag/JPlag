import { Match } from "./Match";
import { SubmissionFile } from "./SubmissionFile";
import { MatchInSingleFile } from "./MatchInSingleFile";

/**
 * Comparison model used by the ComparisonView
 */
export class Comparison {
  private readonly _firstSubmissionId: string;
  private readonly _secondSubmissionId: string;
  private readonly _similarity: number;

  constructor(
    firstSubmissionId: string,
    secondSubmissionId: string,
    similarity: number
  ) {
    this._firstSubmissionId = firstSubmissionId;
    this._secondSubmissionId = secondSubmissionId;
    this._similarity = similarity;
    this._filesOfFirstSubmission = new Map();
    this._filesOfSecondSubmission = new Map();
    this._colors = [];
    this._allMatches = [];
    this._matchesInFirstSubmission = new Map();
    this._matchesInSecondSubmissions = new Map();
  }

  private _filesOfFirstSubmission: Map<string, SubmissionFile>;

  get filesOfFirstSubmission(): Map<string, SubmissionFile> {
    return this._filesOfFirstSubmission;
  }

  set filesOfFirstSubmission(value: Map<string, SubmissionFile>) {
    this._filesOfFirstSubmission = value;
  }

  private _filesOfSecondSubmission: Map<string, SubmissionFile>;

  get filesOfSecondSubmission(): Map<string, SubmissionFile> {
    return this._filesOfSecondSubmission;
  }

  set filesOfSecondSubmission(value: Map<string, SubmissionFile>) {
    this._filesOfSecondSubmission = value;
  }

  private _colors: Array<string>;

  get colors(): Array<string> {
    return this._colors;
  }

  set colors(value: Array<string>) {
    this._colors = value;
  }

  private _allMatches: Array<Match>;

  get allMatches(): Array<Match> {
    return this._allMatches;
  }

  set allMatches(value: Array<Match>) {
    this._allMatches = value;
  }

  private _matchesInFirstSubmission: Map<string, Array<MatchInSingleFile>>;

  get matchesInFirstSubmission(): Map<string, Array<MatchInSingleFile>> {
    return this._matchesInFirstSubmission;
  }

  set matchesInFirstSubmission(value: Map<string, Array<MatchInSingleFile>>) {
    this._matchesInFirstSubmission = value;
  }

  private _matchesInSecondSubmissions: Map<string, Array<MatchInSingleFile>>;

  get matchesInSecondSubmissions(): Map<string, Array<MatchInSingleFile>> {
    return this._matchesInSecondSubmissions;
  }

  set matchesInSecondSubmissions(value: Map<string, Array<MatchInSingleFile>>) {
    this._matchesInSecondSubmissions = value;
  }

  get firstSubmissionId(): string {
    return this._firstSubmissionId;
  }

  get secondSubmissionId(): string {
    return this._secondSubmissionId;
  }

  get similarity(): number {
    return this._similarity;
  }
}
