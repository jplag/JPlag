export type MatchColorIndex = number | undefined | 'base'

/**
 * Match between two files of two submissions.
 * @property firstFile - Path to the file of the first submission.
 * @property secondFile - Path to the file of the second submission.
 * @property startInFirst - Starting line of the match in the first file.
 * @property endInFirst - Ending line of the match in the first file.
 * @property startInSecond - Starting line of the match in the second file.
 * @property endInSecond - Ending line of the match in the second file.
 * @property tokens - Number of tokens in the match.
 * @property colorIndex - Index of the color to use for the match.
 */
export interface Match extends ReportFormatMatch {
  colorIndex: MatchColorIndex
}

export interface ReportFormatMatch {
  firstFileName: string
  secondFileName: string
  startInFirst: CodePosition
  endInFirst: CodePosition
  startInSecond: CodePosition
  endInSecond: CodePosition
  lengthOfFirst: number
  lengthOfSecond: number
}

export interface CodePosition {
  // 1-based
  line: number
  // 0-based
  column: number
  // 0-based
  tokenListIndex: number
}

export function getMatchLength(match: Match) {
  return Math.min(match.lengthOfFirst, match.lengthOfSecond)
}
