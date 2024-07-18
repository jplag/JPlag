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
  tokenCount: number
  /**
   * Number of tokens in the file that are matched.
   */
  matchedTokenCount: number
  /**
   * The name to be displayed in the report viewer. If not defined, the file name should be chosen
   */
  displayFileName?: string
}
