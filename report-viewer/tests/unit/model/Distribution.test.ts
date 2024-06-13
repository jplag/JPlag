import { describe, expect, it } from 'vitest'
import { Distribution } from '@/model/Distribution'

const distribution = new Distribution([
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0,
  1, 2, 12, 8, 31, 61, 168, 273, 493, 923, 1544, 2244, 3163, 4309, 5373, 6343, 7177, 7445, 7292,
  7023, 6130, 5091, 4056, 3025, 2052, 1442, 869, 470, 225, 109, 42, 15, 7, 0
])

describe('Distribution', () => {
  it('get in 10 Buckets', () => {
    expect(distribution.splitIntoTenBuckets()).toEqual([0, 0, 0, 0, 0, 0, 26, 13209, 58955, 5231])
  })
})
