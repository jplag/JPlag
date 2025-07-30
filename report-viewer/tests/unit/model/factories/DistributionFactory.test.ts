import { beforeEach, describe, it, expect } from 'vitest'
import { DistributionFactory } from '@/model/factories/DistributionFactory'
import { MetricJsonIdentifier } from '@/model/MetricJsonIdentifier'
import validDistribution from './assets/ValidDistribution.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'

describe('Test JSON to Distribution', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('Test Valid JSON', async () => {
    store().state.files['distribution.json'] = JSON.stringify(validDistribution)
    const result = await DistributionFactory.getDistributions()

    expect(result).toEqual({
      [MetricJsonIdentifier.AVERAGE_SIMILARITY]: {
        _distribution: [
          272, 0, 0, 15, 31, 90, 192, 124, 94, 101, 95, 82, 129, 77, 96, 75, 34, 84, 39, 53, 43, 20,
          43, 28, 9, 22, 11, 43, 11, 7, 7, 21, 3, 3, 24, 2, 4, 0, 4, 0, 25, 5, 5, 0, 0, 4, 0, 0, 1,
          2, 1, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 10, 2, 0, 0,
          0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 10, 0, 1, 2, 0, 0, 0, 2, 7, 1, 63
        ]
      },
      [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: {
        _distribution: [
          272, 0, 0, 0, 5, 33, 151, 146, 99, 87, 115, 66, 90, 110, 73, 80, 56, 54, 40, 78, 34, 66,
          38, 43, 36, 11, 7, 30, 31, 54, 3, 18, 9, 6, 5, 8, 11, 3, 1, 5, 10, 6, 3, 3, 0, 22, 2, 4,
          0, 3, 1, 2, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 2, 0, 1, 0, 1, 0, 1, 1, 1, 0,
          0, 0, 10, 1, 1, 0, 0, 1, 1, 0, 0, 0, 10, 1, 2, 0, 0, 0, 2, 1, 0, 70
        ]
      }
    })
  })
})
