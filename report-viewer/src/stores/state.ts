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
  localModeUsed: boolean
  /**
   * Indicates whether zip mode is used.
   */
  zipModeUsed: boolean
  /**
   * Indicates whether single file mode is used.
   */
  singleModeUsed: boolean
  /**
   * Files string if single mode is used.
   */
  singleFillRawContent: string

  fileIdToDisplayName: Map<string, string>
  submissionIdsToComparisonFileName: Map<string, Map<string, string>>
}

/**
 * Internal representation of a file.
 * @property fileName - The name of the file.
 * @property data - The content of the file.
 */
export interface File {
  fileName: string
  data: string
}

/**
 * Internal representation of a single file from a submission.
 * @property name - The name of the file.
 * @property file - The file.
 */
export interface SubmissionFile {
  name: string
  file: File
}

/**
 * Load configuration is used to indicate which mode is used.
 */
export interface LoadConfiguration {
  local: boolean
  zip: boolean
  single: boolean
}

export interface UIState {
  useDarkMode: boolean
}
