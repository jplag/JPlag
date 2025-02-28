import type { SubmissionFile } from '@/model/File'
import type { MetricJsonIdentifier } from '@/model/MetricType'
import type { DistributionChartConfig } from '@/model/ui/DistributionChartConfig'
import type { FileSortingOptions } from '@/model/ui/FileSortingOptions'

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

  fileIdToDisplayName: Map<string, string>
  submissionIdsToComparisonFileName: Map<string, Map<string, string>>
  /**
   * Name of the file uploaded
   */
  uploadedFileName: string
}

export interface UIState {
  useDarkMode: boolean
  comparisonTableSortingMetric: MetricJsonIdentifier
  comparisonTableSecondaryMetric: MetricJsonIdentifier
  comparisonTableClusterSorting: boolean
  distributionChartConfig: DistributionChartConfig
  fileSorting: FileSortingOptions
}
