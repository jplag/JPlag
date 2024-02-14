import type { Match } from './Match'
import type { SubmissionFile } from '@/model/File'
import { MatchInSingleFile } from './MatchInSingleFile'
import type { MetricType } from './MetricType'

/**
 * Comparison model used by the ComparisonView
 */
export class Comparison {
  private readonly _firstSubmissionId: string
  private readonly _secondSubmissionId: string
  private readonly _similarities: Record<MetricType, number>
  private _filesOfFirstSubmission: SubmissionFile[]
  private _filesOfSecondSubmission: SubmissionFile[]
  private _allMatches: Array<Match>
  private readonly _firstSimilarity?: number
  private readonly _secondSimilarity?: number

  constructor(
    firstSubmissionId: string,
    secondSubmissionId: string,
    similarities: Record<MetricType, number>,
    filesOfFirstSubmission: SubmissionFile[],
    filesOfSecondSubmission: SubmissionFile[],
    allMatches: Array<Match>,
    firstSimilarity?: number,
    secondSimilarity?: number
  ) {
    this._firstSubmissionId = firstSubmissionId
    this._secondSubmissionId = secondSubmissionId
    this._similarities = similarities
    this._filesOfFirstSubmission = filesOfFirstSubmission
    this._filesOfSecondSubmission = filesOfSecondSubmission
    this._allMatches = allMatches
    this._firstSimilarity = firstSimilarity
    this._secondSimilarity = secondSimilarity
  }

  /**
   * @return Map of all files of the first submission
   */
  get filesOfFirstSubmission(): SubmissionFile[] {
    return this._filesOfFirstSubmission
  }

  /**
   * @return Map of all files of the second submission
   */
  get filesOfSecondSubmission(): SubmissionFile[] {
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
    return this.groupMatchesByFileName(1)
  }

  /**
   * @return Map of all matches in the second submission
   */
  get matchesInSecondSubmissions(): Map<string, Array<MatchInSingleFile>> {
    return this.groupMatchesByFileName(2)
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

  get firstSimilarity(): number | undefined {
    return this._firstSimilarity
  }

  get secondSimilarity(): number | undefined {
    return this._secondSimilarity
  }

  private groupMatchesByFileName(index: 1 | 2): Map<string, Array<MatchInSingleFile>> {
    const acc = new Map<string, Array<MatchInSingleFile>>()
    this._allMatches.forEach((val) => {
      const name = index === 1 ? (val.firstFile as string) : (val.secondFile as string)

      if (!acc.get(name)) {
        acc.set(name, [])
      }

      acc.get(name)?.push(new MatchInSingleFile(val, index))
    })
    return acc
  }
}
