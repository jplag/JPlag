import { beforeAll, describe, it, vi, expect } from 'vitest'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import { ParserLanguage } from '@/model/Language'
import { MetricType } from '@/model/MetricType'
import validOptions from './ValidOptions.json'

const store = {
  state: {
    localModeUsed: false,
    zipModeUsed: true,
    singleModeUsed: false,
    files: {
      'options.json': JSON.stringify(validOptions)
    }
  }
}

describe('Test JSON to Options', async () => {
  beforeAll(() => {
    vi.mock('@/stores/store', () => ({
      store: vi.fn(() => {
        return store
      })
    }))
  })

  it('Test Valid JSON', async () => {
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
      similarityMetric: MetricType.AVERAGE,
      similarityThreshold: 0.0,
      maxNumberComparisons: 500,
      clusterOptions: {
        enabled: true,
        similarityMetric: MetricType.AVERAGE,
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
