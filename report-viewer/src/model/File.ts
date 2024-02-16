/**
 * Internal representation of a single file.
 */
export interface File {
  /**
   * The name of the file.
   */
  fileName: string
  /**
   * The files content.
   */
  data: string
}

/**
 * Internal representation of a single file from a submission.
 */
export interface SubmissionFile extends File {
  /**
   * The id of the submission.
   */
  submissionId: string
  /**
   * Number of total tokens in the file.
   */
  tokenCount?: number
  /**
   * Number of tokens in the file that are matched.
   */
  matchedTokenCount: number
}
