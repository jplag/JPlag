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
export interface Match {
  firstFile: string
  secondFile: string
  startInFirst: number
  endInFirst: number
  startInSecond: number
  endInSecond: number
  tokens: number
  colorIndex?: number
}
