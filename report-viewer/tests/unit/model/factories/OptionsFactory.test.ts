import { beforeEach, describe, it, expect } from 'vitest'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import { ParserLanguage } from '@/model/Language'
import { MetricTypes } from '@/model/MetricType'
import validOptions from './ValidOptions.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'

describe('Test JSON to Options', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    store().setLoadingType('zip')
  })

  it('Test Valid JSON', async () => {
    store().state.files['options.json'] = JSON.stringify(validOptions)
    const result = await OptionsFactory.getCliOptions()

    expect(result).toEqual({
      language: ParserLanguage.JAVA,
      minTokenMatch: 9,
      submissionDirectories: ['.\\files'],
      oldDirectories: ['old'],
      baseDirectory: '.',
      subDirectoryName: 'src/',
      fileSuffixes: ['.java', '.JAVA'],
      exclusionFileName: 'ex.txt',
      similarityMetric: MetricTypes.AVERAGE_SIMILARITY,
      similarityThreshold: 0.0,
      maxNumberComparisons: 500,
      clusterOptions: {
        enabled: true,
        similarityMetric: MetricTypes.AVERAGE_SIMILARITY,
        spectralBandwidth: 20.0,
        spectralGaussianProcessVariance: 0.0025000000000000005,
        spectralMinRuns: 5,
        spectralMaxRuns: 50,
        spectralMaxKMeansIterations: 200,
        agglomerativeThreshold: 0.2,
        preprocessor: 'Cumulative Distribution Function',
        algorithm: 'Spectral',
        interClusterSimilarity: 'Average',
        preprocessorThreshold: 0.2,
        preprocessorPercentile: 0.5
      },
      mergingOptions: {
        enabled: false,
        minNeighborLength: 0,
        maxGapSize: 0
      }
    })
  })
})
