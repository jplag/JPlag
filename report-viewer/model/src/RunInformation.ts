import type { Version } from './Version'

export enum SubmissionState {
  VALID = 'VALID',
  NOTHING_TO_PARSE = 'NOTHING_TO_PARSE',
  CANNOT_PARSE = 'CANNOT_PARSE',
  TOO_SMALL = 'TOO_SMALL',
  UNPARSED = 'UNPARSED'
}

export interface FailedSubmission {
  submissionId: string
  submissionState: SubmissionState
}

export class RunInformation {
  _version: Version
  _failedSubmissions: FailedSubmission[]
  _dateOfExecution: string
  _tokenizationTime: number
  _comparisonTime: number
  _totalComparisons: number

  constructor(
    version: Version,
    failedSubmissions: FailedSubmission[],
    dateOfExecution: string,
    tokenizationTime: number,
    comparisonTime: number,
    totalComparisons: number
  ) {
    this._version = version
    this._failedSubmissions = failedSubmissions
    this._dateOfExecution = dateOfExecution
    this._tokenizationTime = tokenizationTime
    this._comparisonTime = comparisonTime
    this._totalComparisons = totalComparisons
  }

  get version(): Version {
    return this._version
  }
  get failedSubmissions(): FailedSubmission[] {
    return this._failedSubmissions
  }
  get dateOfExecution(): string {
    return this._dateOfExecution
  }
  get tokenizationTime(): number {
    return this._tokenizationTime
  }
  get comparisonTime(): number {
    return this._comparisonTime
  }
  get totalComparisons(): number {
    return this._totalComparisons
  }
}
