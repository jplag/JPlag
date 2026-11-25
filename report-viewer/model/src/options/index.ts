import type { Language } from '../Language'
import type { CliClusterOptions } from './Cluster'
import type { CliFrequencyOptions } from './Frequency'
import type { CliMergingOptions } from './Merging'
import type { CliParsingOptions } from './Parsing'
import type { CliReportingOptions } from './Reporting'
import type { CliSubmissionFileOptions } from './SubmissionFiles'

export { CliParsingOptions, CliClusterOptions }
export type CliOptions = AbstractOptions<Language>
export type ReportFormatCliOptions = AbstractOptions<string>

interface AbstractOptions<L> {
  submissionFileOptions: CliSubmissionFileOptions
  parsingOptions: CliParsingOptions<L>
  reportingOptions: CliReportingOptions
  clusteringOptions: CliClusterOptions
  mergingOptions: CliMergingOptions
  frequencyAnalysisOptions: CliFrequencyOptions
}
