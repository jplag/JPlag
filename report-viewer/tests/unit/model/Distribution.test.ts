import { describe, expect, it } from 'vitest'
import { Distribution } from '@/model/Distribution'
import { TenValueDistribution } from '@/model/TenValueDistribution'
import { HundredValueDistribution } from '@/model/HundredValueDistribution'

describe('Distribution', () => {
  it.each([
    new TenValueDistribution([0, 0, 0, 0, 0, 0, 26, 13209, 58955, 5231]),
    new HundredValueDistribution([
      0, 7, 15, 42, 109, 225, 470, 869, 1442, 2052, 3025, 4056, 5091, 6130, 7023, 7292, 7445, 7177,
      6343, 5373, 4309, 3163, 2244, 1544, 923, 493, 273, 168, 61, 31, 8, 12, 2, 1, 0, 1, 2, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    ])
  ])('get in 10 Buckets', (distribution: Distribution) => {
    expect(distribution.splitIntoTenBuckets()).toEqual([0, 0, 0, 0, 0, 0, 26, 13209, 58955, 5231])
  })
})
