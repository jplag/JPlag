import type { Match } from './Match'
import type { SubmissionFile } from './SubmissionFile'
import type { MatchInSingleFile } from './MatchInSingleFile'
import type MetricType from './MetricType'

/**
 * Comparison model used by the ComparisonView
 */
export class Comparison {
  private readonly _firstSubmissionId: string
  private readonly _secondSubmissionId: string
  private readonly _similarities: Record<MetricType, number>
  private _filesOfFirstSubmission: Map<string, SubmissionFile>
  private _filesOfSecondSubmission: Map<string, SubmissionFile>
  private _allMatches: Array<Match>
  private _matchesInFirstSubmission: Map<string, Array<MatchInSingleFile>>
  private _matchesInSecondSubmissions: Map<string, Array<MatchInSingleFile>>

  constructor(
    firstSubmissionId: string,
    secondSubmissionId: string,
    similarities: Record<MetricType, number>,
    filesOfFirstSubmission: Map<string, SubmissionFile>,
    filesOfSecondSubmission: Map<string, SubmissionFile>,
    allMatches: Array<Match>,
    matchesInFirstSubmission: Map<string, Array<MatchInSingleFile>>,
    matchesInSecondSubmissions: Map<string, Array<MatchInSingleFile>>
  ) {
    this._firstSubmissionId = firstSubmissionId
    this._secondSubmissionId = secondSubmissionId
    this._similarities = similarities
    this._filesOfFirstSubmission = filesOfFirstSubmission
    this._filesOfSecondSubmission = filesOfSecondSubmission
    this._allMatches = allMatches
    this._matchesInFirstSubmission = matchesInFirstSubmission
    this._matchesInSecondSubmissions = matchesInSecondSubmissions
  }

  /**
   * @return Map of all files of the first submission
   */
  get filesOfFirstSubmission(): Map<string, SubmissionFile> {
    return this._filesOfFirstSubmission
  }

  /**
   * @return Map of all files of the second submission
   */
  get filesOfSecondSubmission(): Map<string, SubmissionFile> {
    return this._filesOfSecondSubmission
  }

  /**
   * @return Array of all matches
   */
  get allMatches(): Array<Match> {
    return this._allMatches
  }

  /**
   * @return Map of all matches in the first submission
   */
  get matchesInFirstSubmission(): Map<string, Array<MatchInSingleFile>> {
    return this._matchesInFirstSubmission
  }

  /**
   * @return Map of all matches in the second submission
   */
  get matchesInSecondSubmissions(): Map<string, Array<MatchInSingleFile>> {
    return this._matchesInSecondSubmissions
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
  get similarities() {
    return this._similarities
  }
}
