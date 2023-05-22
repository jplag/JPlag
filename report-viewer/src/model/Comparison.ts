import type { Match } from './Match'
import type { SubmissionFile } from './SubmissionFile'
import type { MatchInSingleFile } from './MatchInSingleFile'

/**
 * Comparison model used by the ComparisonView
 */
export class Comparison {
  private readonly _firstSubmissionId: string
  private readonly _secondSubmissionId: string
  private readonly _similarity: number

  constructor(firstSubmissionId: string, secondSubmissionId: string, similarity: number) {
    this._firstSubmissionId = firstSubmissionId
    this._secondSubmissionId = secondSubmissionId
    this._similarity = similarity
    this._filesOfFirstSubmission = new Map()
    this._filesOfSecondSubmission = new Map()
    this._colors = []
    this._allMatches = []
    this._matchesInFirstSubmission = new Map()
    this._matchesInSecondSubmissions = new Map()
  }

  private _filesOfFirstSubmission: Map<string, SubmissionFile>

  /**
   * @return Map of all files of the first submission
   */
  get filesOfFirstSubmission(): Map<string, SubmissionFile> {
    return this._filesOfFirstSubmission
  }

  /**
   * Set the files of the first submission
   * @param value Map to set to
   */
  set filesOfFirstSubmission(value: Map<string, SubmissionFile>) {
    this._filesOfFirstSubmission = value
  }

  private _filesOfSecondSubmission: Map<string, SubmissionFile>

  /**
   * @return Map of all files of the second submission
   */
  get filesOfSecondSubmission(): Map<string, SubmissionFile> {
    return this._filesOfSecondSubmission
  }

  /**
   * Set the files of the second submission
   * @param value Map to set to
   */
  set filesOfSecondSubmission(value: Map<string, SubmissionFile>) {
    this._filesOfSecondSubmission = value
  }

  private _colors: Array<string>

  /**
   * @return Array of all colors used to display the matches
   */
  get colors(): Array<string> {
    return this._colors
  }

  /**
   * Set the colors used to display the matches
   * @param value Colors to set to
   */
  set colors(value: Array<string>) {
    this._colors = value
  }

  private _allMatches: Array<Match>

  /**
   * @return Array of all matches
   */
  get allMatches(): Array<Match> {
    return this._allMatches
  }

  /**
   * Set the array of all matches
   * @param value Matches to set to
   */
  set allMatches(value: Array<Match>) {
    this._allMatches = value
  }

  private _matchesInFirstSubmission: Map<string, Array<MatchInSingleFile>>

  /**
   * @return Map of all matches in the first submission
   */
  get matchesInFirstSubmission(): Map<string, Array<MatchInSingleFile>> {
    return this._matchesInFirstSubmission
  }

  /**
   * Set the matches in the first submission
   * @param value Matches in the first submission to set to
   */
  set matchesInFirstSubmission(value: Map<string, Array<MatchInSingleFile>>) {
    this._matchesInFirstSubmission = value
  }

  private _matchesInSecondSubmissions: Map<string, Array<MatchInSingleFile>>

  /**
   * @return Map of all matches in the second submission
   */
  get matchesInSecondSubmissions(): Map<string, Array<MatchInSingleFile>> {
    return this._matchesInSecondSubmissions
  }

  /**
   * Set the matches in the second submission
   * @param value Matches in the first submission to set to
   */
  set matchesInSecondSubmissions(value: Map<string, Array<MatchInSingleFile>>) {
    this._matchesInSecondSubmissions = value
  }

  /**
   * @return Id of the first submission
   */
  get firstSubmissionId() {
    return this._firstSubmissionId
  }

  /**
   * @return Id of the second submission
   */
  get secondSubmissionId() {
    return this._secondSubmissionId
  }

  /**
   * @return Similarity of the two submissions
   */
  get similarity() {
    return this._similarity
  }
}
