import type { ParserLanguage } from './Language'
import type { MetricType } from './MetricType'

export interface CliOptions {
  language: ParserLanguage
  minTokenMatch: number
  submissionDirectories: string[]
  oldDirectories: string[]
  baseDirectory: string
  subDirectoryName: string
  fileSuffixes: string[]
  exclusionFileName: string
  similarityMetric: MetricType
  similarityTreshold: number
  maxNumberComparisons: number
  clusterOptions: CliClusterOptions
  mergingOptions: CliMergingOptions
}

export interface CliClusterOptions {
  enabled: boolean
  similarityMetric: MetricType
  spectralBandwidth: number
  spectralGaussianProcessVariance: number
  spectralMinRuns: number
  spectralMaxRuns: number
  spectralMaxKMeansIterations: number
  agglomerativeThreshold: number
  preprocessor: string
  algorithm: string
  interClusterSimilarity: string
  preprocessorTreshold: number
  preprocessorPercentile: number
}

export interface CliMergingOptions {
  enabled: boolean
  minNeighbourLength: number
  maxGapSize: number
}
