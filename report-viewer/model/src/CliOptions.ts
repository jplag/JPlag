import type { Language } from './Language'
import { MetricJsonIdentifier } from './MetricJsonIdentifier'

export type CliOptions = AbstractOptions<Language>
export type ReportFormatCliOptions = AbstractOptions<string>

interface AbstractOptions<L> {
  language: L
  minimumTokenMatch: number
  submissionDirectories: string[]
  oldSubmissionDirectories: string[]
  baseCodeSubmissionDirectory: string
  subdirectoryName: string
  fileSuffixes: string[]
  exclusionFileName: string
  similarityMetric: MetricJsonIdentifier
  similarityThreshold: number
  maximumNumberOfComparisons: number
  clusteringOptions: CliClusterOptions
  debugParser: boolean
  mergingOptions: CliMergingOptions
  normalize: boolean
  analyzeComments: boolean
  frequencyAnalysisOptions: CliFrequencyOptions
}

export interface CliClusterOptions {
  similarityMetric: MetricJsonIdentifier
  spectralKernelBandwidth: number
  spectralGaussianProcessVariance: number
  spectralMinRuns: number
  spectralMaxRuns: number
  spectralMaxKMeansIterationPerRun: number
  agglomerativeThreshold: number
  preprocessor: string
  enabled: boolean
  algorithm: string
  agglomerativeInterClusterSimilarity: string
  preprocessorThreshold: number
  preprocessorPercentile: number
}

interface CliMergingOptions {
  enabled: boolean
  minimumNeighborLength: number
  maximumGapSize: number
  minimumRequiredMerges: number
}

interface CliFrequencyOptions {
  enabled: boolean
  analysisStrategy: string
  weightingFunction: string
  weightingFactor: number
}
