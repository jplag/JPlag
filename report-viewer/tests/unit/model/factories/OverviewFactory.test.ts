import { beforeAll, describe, expect, it, vi } from 'vitest'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import { MetricType } from '@/model/MetricType'
import { HundredValueDistribution } from '@/model/HundredValueDistribution'
import { TenValueDistribution } from '@/model/TenValueDistribution'
import validNew from './ValidNewOverview.json'
import validOld from './ValidOldOverview.json'
import outdated from './OutdatedOverview.json'

const store = {
  state: {
    localModeUsed: false,
    zipModeUsed: true,
    singleModeUsed: false,
    files: {}
  },
  saveSubmissionNames: (map) => {
    expect(map.has('A')).toBeTruthy()
    expect(map.has('B')).toBeTruthy()
    expect(map.has('C')).toBeTruthy()
    expect(map.has('D')).toBeTruthy()
  },
  saveComparisonFileLookup: (map) => {
    expect(map.has('A')).toBeTruthy()
    expect(map.has('B')).toBeTruthy()
  }
}

describe('Test JSON to Overview', () => {
  beforeAll(() => {
    vi.mock('@/stores/store', () => ({
      store: vi.fn(() => {
        return store
      })
    }))

    vi.spyOn(global.window, 'alert').mockImplementation(() => {})
  })

  it('Post 5.0', async () => {
    store.state.files['overview.json'] = JSON.stringify(validNew)

    expect(await OverviewFactory.getOverview()).toEqual({
      _submissionFolderPath: ['files'],
      _baseCodeFolderPath: '',
      _language: 'Javac based AST plugin',
      _fileExtensions: ['.java', '.JAVA'],
      _matchSensitivity: 9,
      _dateOfExecution: '12/07/23',
      _durationOfExecution: 12,
      _topComparisons: [
        {
          firstSubmissionId: 'A',
          secondSubmissionId: 'C',
          similarities: {
            [MetricType.AVERAGE]: 0.9960435212660732,
            [MetricType.MAXIMUM]: 0.9960435212660732
          },
          sortingPlace: 0,
          id: 1,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'D',
          secondSubmissionId: 'A',
          similarities: {
            [MetricType.AVERAGE]: 0.751044776119403,
            [MetricType.MAXIMUM]: 0.947289156626506
          },
          sortingPlace: 1,
          id: 2,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'D',
          secondSubmissionId: 'C',
          similarities: {
            [MetricType.AVERAGE]: 0.751044776119403,
            [MetricType.MAXIMUM]: 0.947289156626506
          },
          sortingPlace: 2,
          id: 3,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'B',
          secondSubmissionId: 'D',
          similarities: {
            [MetricType.AVERAGE]: 0.28322981366459626,
            [MetricType.MAXIMUM]: 0.8085106382978723
          },
          sortingPlace: 3,
          id: 4,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'B',
          secondSubmissionId: 'A',
          similarities: {
            [MetricType.AVERAGE]: 0.2378472222222222,
            [MetricType.MAXIMUM]: 0.9716312056737588
          },
          sortingPlace: 4,
          id: 5,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'B',
          secondSubmissionId: 'C',
          similarities: {
            [MetricType.AVERAGE]: 0.2378472222222222,
            [MetricType.MAXIMUM]: 0.9716312056737588
          },
          sortingPlace: 5,
          id: 6,
          clusterIndex: 0
        }
      ],
      _distributions: {
        [MetricType.MAXIMUM]: new HundredValueDistribution([
          1, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        ]),
        [MetricType.AVERAGE]: new HundredValueDistribution([
          1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        ])
      },
      _clusters: [
        {
          averageSimilarity: 94.746956,
          strength: 0.0,
          members: ['C', 'A', 'B', 'D']
        }
      ],
      _totalComparisons: 6
    })
  })

  it('Pre 5.0', async () => {
    store.state.files['overview.json'] = JSON.stringify(validOld)
    expect(await OverviewFactory.getOverview()).toEqual({
      _submissionFolderPath: ['test'],
      _baseCodeFolderPath: '',
      _language: 'Javac based AST plugin',
      _fileExtensions: ['.java', '.JAVA'],
      _matchSensitivity: 9,
      _dateOfExecution: '12/07/23',
      _durationOfExecution: 34,
      _topComparisons: [
        {
          firstSubmissionId: 'A',
          secondSubmissionId: 'B',
          similarities: {
            [MetricType.AVERAGE]: 0.6900452488687783,
            [MetricType.MAXIMUM]: 0.9457364341085271
          },
          sortingPlace: 0,
          id: 1,
          clusterIndex: -1
        },
        {
          firstSubmissionId: 'C',
          secondSubmissionId: 'D',
          similarities: {
            [MetricType.AVERAGE]: 0.6954045248868778,
            [MetricType.MAXIMUM]: 0.83500530023
          },
          sortingPlace: 1,
          id: 2,
          clusterIndex: -1
        }
      ],
      _distributions: {
        [MetricType.AVERAGE]: new TenValueDistribution([0, 0, 0, 1, 0, 0, 0, 0, 0, 0]),
        [MetricType.MAXIMUM]: new TenValueDistribution([1, 0, 0, 0, 0, 0, 0, 0, 0, 0])
      },
      _clusters: [],
      _totalComparisons: 6
    })
  })
})

describe('Outdated JSON to Overview', () => {
  it('Outdated version', async () => {
    store.state.files['overview.json'] = JSON.stringify(outdated)
    expect(() => OverviewFactory.getOverview()).rejects.toThrowError()
  })
})
