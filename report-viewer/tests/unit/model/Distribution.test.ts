import { describe, expect, it } from 'vitest'
import { Distribution } from '@/model/Distribution'

const distributionData = [
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0,
  1, 2, 12, 8, 31, 61, 168, 273, 493, 923, 1544, 2244, 3163, 4309, 5373, 6343, 7177, 7445, 7292,
  7023, 6130, 5091, 4056, 3025, 2052, 1442, 869, 470, 225, 109, 42, 15, 7, 0
]
const distribution = new Distribution(distributionData)

describe('Distribution', () => {
  it('get in 10 Buckets', () => {
    expect(distribution.splitIntoBuckets(10)).toEqual([0, 0, 0, 0, 0, 0, 26, 13209, 58955, 5231])
  }),
    it('get in 100 Buckets', () => {
      expect(distribution.splitIntoBuckets(100)).toEqual(distributionData)
    }),
    it('get in 25 Buckets', () => {
      expect(distribution.splitIntoBuckets(25)).toEqual([
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 112, 1857, 11260, 26338, 25536, 10575,
        1673, 64
      ])
    }),
    it('get in 20 Buckets', () => {
      expect(distribution.splitIntoBuckets(20)).toEqual([
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 23, 1026, 12183, 33630, 25325, 5058, 173
      ])
    }),
    it('get in 50 Buckets', () => {
      expect(distribution.splitIntoBuckets(50)).toEqual([
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        2, 1, 3, 20, 92, 441, 1416, 3788, 7472, 11716, 14622, 14315, 11221, 7081, 3494, 1339, 334,
        57, 7
      ])
    }),
    it('all Bucket sums equal', () => {
      const checkSum = distributionData.reduce((acc, val) => acc + val, 0)
      const options = [10, 20, 25, 50, 100]
      options.forEach((option) => {
        const bucketSum = distribution.splitIntoBuckets(option).reduce((acc, val) => acc + val, 0)
        expect(bucketSum).toEqual(checkSum)
      })
    })
})
