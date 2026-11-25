import { MetricJsonIdentifier } from '../MetricJsonIdentifier'

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
