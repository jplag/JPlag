import type { Version } from './Version'

export class RunInformation {
  _version: Version
  _failedSubmissions: string[]
  _dateOfExecution: string
  _executionTime: number
  _totalComparisons: number

  constructor(
    version: Version,
    failedSubmissions: string[],
    dateOfExecution: string,
    executionTime: number,
    totalComparisons: number
  ) {
    this._version = version
    this._failedSubmissions = failedSubmissions
    this._dateOfExecution = dateOfExecution
    this._executionTime = executionTime
    this._totalComparisons = totalComparisons
  }

  get version(): Version {
    return this._version
  }
  get failedSubmissions(): string[] {
    return this._failedSubmissions
  }
  get dateOfExecution(): string {
    return this._dateOfExecution
  }
  get executionTime(): number {
    return this._executionTime
  }
  get totalComparisons(): number {
    return this._totalComparisons
  }
}
