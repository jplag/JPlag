/**
 * File in a submission. Used in the comparison view.
 * @property lines - Code lines of the file.
 * @property collapsed - Indicates whether the file is displayed or not.
 */
export type SubmissionFile = {
  lines: Array<string>
  collapsed: boolean
}
