import { MetricJsonIdentifier } from '../MetricJsonIdentifier'

export interface CliParsingOptions<L> {
  language: L
  minimumTokenMatch: number
  fileSuffixes: string[]
  similarityMetric: MetricJsonIdentifier
  similarityThreshold: number
  debugParser: boolean
  normalize: boolean
  analyzeComments: boolean
}
