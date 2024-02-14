import type { SubmissionFile } from '@/model/File'
import type { MetricType } from '@/model/MetricType'
import type { DistributionChartConfig } from '@/model/ui/DistributionChartConfig'

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
  submissions: Record<string, Map<string, SubmissionFile>>
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
  /**
   * Name of the file uploaded
   */
  uploadedFileName: string
}

export interface UIState {
  useDarkMode: boolean
  comparisonTableSortingMetric: MetricType
  comparisonTableClusterSorting: boolean
  distributionChartConfig: DistributionChartConfig
}

/**
 * Load configuration is used to indicate which mode is used.
 */
export interface LoadConfiguration {
  local: boolean
  zip: boolean
  single: boolean
}
