import { beforeEach, describe, it, expect } from 'vitest'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import { ParserLanguage } from '@/model/Language'
import { MetricType } from '@/model/MetricType'
import validOptions from './assets/ValidOptions.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'

describe('Test JSON to Options', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('Test Valid JSON', async () => {
    store().state.files['options.json'] = JSON.stringify(validOptions)
    const result = await OptionsFactory.getCliOptions()

    expect(result).toEqual({
      language: ParserLanguage.JAVA,
      minimumTokenMatch: 9,
      submissionDirectories: ['.\\files'],
      oldSubmissionDirectories: ['old'],
      baseCodeSubmissionDirectory: '.',
      subdirectoryName: 'src/',
      fileSuffixes: ['.java', '.JAVA'],
      exclusionFileName: 'ex.txt',
      similarityMetric: MetricType.AVERAGE,
      similarityThreshold: 0.0,
      maximumNumberOfComparisons: 500,
      clusteringOptions: {
        enabled: true,
        metric: MetricType.AVERAGE,
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
