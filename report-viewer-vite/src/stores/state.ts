/**
 * Local store. Stores the state of the application.
 */
export interface State {
  /**
   * The set of ids to be hidden.
   */
  anonymous: Set<string>
  /**
   * Stored files if zip mode is used. Stores the files as key - file name, value - file string
   */
  files: Record<string, string>
  submissions: Record<string, Map<string, string>>
  /**
   * Indicates whether local mode is used.
   */
  local: boolean
  /**
   * Indicates whether zip mode is used.
   */
  zip: boolean
  /**
   * Indicates whether single file mode is used.
   */
  single: boolean
  /**
   * Files string if single mode is used.
   */
  fileString: string

  fileIdToDisplayName: Map<string, string>
  submissionIdsToComparisonFileName: Map<string, Map<string, string>>
}

export interface File {
  fileName: string
  data: string
}

export interface SubmissionFile {
  name: string
  file: File
}

export interface LoadConfiguration {
  local: boolean
  zip: boolean
  single: boolean
  fileString: string
}
