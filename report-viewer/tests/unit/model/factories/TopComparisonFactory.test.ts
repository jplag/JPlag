import { beforeEach, describe, it, expect } from 'vitest'
import { TopComparisonFactory } from '@/model/factories/TopComparisonFactory'
import { MetricJsonIdentifier } from '@/model/MetricJsonIdentifier'
import validTopComparisons from './assets/ValidTopComparisons.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'

describe('Test JSON to Distribution', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('Test without clusters', async () => {
    store().state.files['topComparisons.json'] = JSON.stringify(validTopComparisons)
    const result = await TopComparisonFactory.getTopComparisons([])

    expect(result.length).toEqual(5)
    expect(result[0]).toEqual({
      sortingPlace: 0,
      id: 1,
      firstSubmissionId: 'Sub01',
      secondSubmissionId: 'Sub02',
      similarities: {
        [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.41,
        [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.81,
        [MetricJsonIdentifier.LONGEST_MATCH]: 29,
        [MetricJsonIdentifier.MAXIMUM_LENGTH]: 35
      },
      cluster: undefined
    })
    expect(result[1]).toEqual({
      sortingPlace: 1,
      id: 2,
      firstSubmissionId: 'Sub02',
      secondSubmissionId: 'Sub03',
      similarities: {
        [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.22,
        [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.34,
        [MetricJsonIdentifier.LONGEST_MATCH]: 15,
        [MetricJsonIdentifier.MAXIMUM_LENGTH]: 40
      },
      cluster: undefined
    })
    expect(result[4]).toEqual({
      sortingPlace: 4,
      id: 5,
      firstSubmissionId: 'Sub04',
      secondSubmissionId: 'Sub03',
      similarities: {
        [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.01,
        [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.46,
        [MetricJsonIdentifier.LONGEST_MATCH]: 3,
        [MetricJsonIdentifier.MAXIMUM_LENGTH]: 47
      },
      cluster: undefined
    })
  })

  it('Test with clusters', async () => {
    const cluster = {
      index: 0,
      averageSimilarity: 0.5,
      strength: 0.5,
      members: ['Sub02', 'Sub04', 'Sub01']
    }
    store().state.files['topComparisons.json'] = JSON.stringify(validTopComparisons)
    const result = await TopComparisonFactory.getTopComparisons([cluster])

    expect(result[0].cluster).toEqual(cluster)
    expect(result[1].cluster).toBeUndefined()
    expect(result[2].cluster).toBeUndefined()
    expect(result[3].cluster).toEqual(cluster)
    expect(result[4].cluster).toBeUndefined()
  })
})
