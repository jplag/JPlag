import { beforeAll, describe, expect, it, vi, beforeEach } from 'vitest'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import { MetricJsonIdentifier } from '@/model/MetricType'
import { Distribution } from '@/model/Distribution'
import { ParserLanguage } from '@/model/Language'
import validNew from './ValidOverview.json'
import outdated from './OutdatedOverview.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'

describe('Test JSON to Overview', () => {
  beforeAll(() => {
    vi.spyOn(global.window, 'alert').mockImplementation(() => {})
  })

  beforeEach(() => {
    setActivePinia(createPinia())
    store().setLoadingType('zip')
  })

  it('Post 5.0', async () => {
    store().state.files['overview.json'] = JSON.stringify(validNew)

    expect(await OverviewFactory.getOverview()).toEqual({
      _submissionFolderPath: ['files'],
      _baseCodeFolderPath: '',
      _language: ParserLanguage.JAVA,
      _fileExtensions: ['.java', '.JAVA'],
      _matchSensitivity: 9,
      _dateOfExecution: '12/07/23',
      _durationOfExecution: 12,
      _topComparisons: [
        {
          firstSubmissionId: 'A',
          secondSubmissionId: 'C',
          similarities: {
            [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.9960435212660732,
            [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.9960435212660732,
            [MetricJsonIdentifier.MINIMUM_SIMILARITY]: 0.99,
            [MetricJsonIdentifier.LONGEST_MATCH]: 13,
            [MetricJsonIdentifier.INTERSECTION]: 32,
            [MetricJsonIdentifier.OVERALL]: 100
          },
          sortingPlace: 0,
          id: 1,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'D',
          secondSubmissionId: 'A',
          similarities: {
            [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.751044776119403,
            [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.947289156626506,
            [MetricJsonIdentifier.MINIMUM_SIMILARITY]: 0.5,
            [MetricJsonIdentifier.LONGEST_MATCH]: 4,
            [MetricJsonIdentifier.INTERSECTION]: 12,
            [MetricJsonIdentifier.OVERALL]: 133
          },
          sortingPlace: 1,
          id: 2,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'D',
          secondSubmissionId: 'C',
          similarities: {
            [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.751044776119403,
            [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.947289156626506,
            [MetricJsonIdentifier.MINIMUM_SIMILARITY]: 0.46,
            [MetricJsonIdentifier.LONGEST_MATCH]: 12,
            [MetricJsonIdentifier.INTERSECTION]: 12,
            [MetricJsonIdentifier.OVERALL]: 98
          },
          sortingPlace: 2,
          id: 3,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'B',
          secondSubmissionId: 'D',
          similarities: {
            [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.28322981366459626,
            [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.8085106382978723,
            [MetricJsonIdentifier.MINIMUM_SIMILARITY]: 0.1,
            [MetricJsonIdentifier.LONGEST_MATCH]: 5,
            [MetricJsonIdentifier.INTERSECTION]: 6,
            [MetricJsonIdentifier.OVERALL]: 32
          },
          sortingPlace: 3,
          id: 4,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'B',
          secondSubmissionId: 'A',
          similarities: {
            [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.2378472222222222,
            [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.9716312056737588,
            [MetricJsonIdentifier.MINIMUM_SIMILARITY]: 0.05,
            [MetricJsonIdentifier.LONGEST_MATCH]: 7,
            [MetricJsonIdentifier.INTERSECTION]: 9,
            [MetricJsonIdentifier.OVERALL]: 34
          },
          sortingPlace: 4,
          id: 5,
          clusterIndex: 0
        },
        {
          firstSubmissionId: 'B',
          secondSubmissionId: 'C',
          similarities: {
            [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.2378472222222222,
            [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.9716312056737588,
            [MetricJsonIdentifier.MINIMUM_SIMILARITY]: 0.06,
            [MetricJsonIdentifier.LONGEST_MATCH]: 3,
            [MetricJsonIdentifier.INTERSECTION]: 6,
            [MetricJsonIdentifier.OVERALL]: 134
          },
          sortingPlace: 5,
          id: 6,
          clusterIndex: 0
        }
      ],
      _distributions: {
        [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: new Distribution([
          1, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        ]),
        [MetricJsonIdentifier.AVERAGE_SIMILARITY]: new Distribution([
          1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        ]),
        [MetricJsonIdentifier.MINIMUM_SIMILARITY]: new Distribution([
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 2, 3
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
})

describe('Outdated JSON to Overview', () => {
  it('Outdated version', async () => {
    store().state.files['overview.json'] = JSON.stringify(outdated)
    expect(() => OverviewFactory.getOverview()).rejects.toThrowError()
  })
})
