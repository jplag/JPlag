import type { SubmissionFile } from '@/model/File'
import type { ComparisonTableSorting } from '@/model/ui/ComparisonSorting'
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
   * Stored files. Stores the files as key - file name, value - file string
   */
  files: Record<string, string>
  submissions: Record<string, Map<string, SubmissionFile>>

  fileIdToDisplayName: Map<string, string>
  submissionIdsToComparisonFileName: Map<string, Map<string, string>>
  /**
   * Name of the file uploaded
   */
  uploadedFileName: string
}

export interface UIState {
  useDarkMode: boolean
  comparisonTableSorting: ComparisonTableSorting
  distributionChartConfig: DistributionChartConfig
  fileSorting: FileSortingOptions
}
