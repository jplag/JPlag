import { describe, it, expect } from 'vitest'
import validOptions from './assets/ValidOptions.json?raw'
import { OptionsFactory } from '../../src'
import { MetricJsonIdentifier, ParserLanguage } from '@jplag/model'

describe('Test JSON to Options', () => {
  it('Test Valid JSON', () => {
    const result = OptionsFactory.getCliOptions(validOptions)

    expect(result).toEqual({
      language: ParserLanguage.JAVA,
      minimumTokenMatch: 9,
      submissionDirectories: ['.\\files'],
      oldSubmissionDirectories: ['old'],
      baseCodeSubmissionDirectory: '.',
      subdirectoryName: 'src/',
      fileSuffixes: ['.java', '.JAVA'],
      exclusionFileName: 'ex.txt',
      similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
      similarityThreshold: 0.0,
      maximumNumberOfComparisons: 500,
      clusteringOptions: {
        enabled: true,
        metric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
        spectralKernelBandwidth: 20.0,
        spectralGaussianProcessVariance: 0.0025000000000000005,
        spectralMinRuns: 5,
        spectralMaxRuns: 50,
        spectralMaxKMeansIterationPerRun: 200,
        agglomerativeThreshold: 0.2,
        preprocessor: 'Cumulative Distribution Function',
        algorithm: 'Spectral',
        agglomerativeInterClusterSimilarity: 'Average',
        preprocessorThreshold: 0.2,
        preprocessorPercentile: 0.5
      },
      mergingOptions: {
        enabled: false,
        minimumNeighborLength: 0,
        maximumGapSize: 0,
        minimumRequiredMerges: 3
      }
    })
  })
})
