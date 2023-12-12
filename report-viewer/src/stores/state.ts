import type { MetricType } from '@/model/MetricType'

/**
 * Local store. Stores the state of the application.
 */
export interface State {
  /**
   * The set of ids to be hidden.
   */
  anonymous: Set<string>
  /**
   * Maps the submission id to the number of anonymous ids.
   */
  anonymousIds: Record<string, number>
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
  comparisonTableSortingMetric: MetricType
  comparisonTableClusterSorting: boolean
  distributionChartConfig: DistributionChartConfig
}

/**
 * Configuration for the distribution chart.
 */
export interface DistributionChartConfig {
  metric: MetricType
  xScale: 'linear' | 'logarithmic'
}
