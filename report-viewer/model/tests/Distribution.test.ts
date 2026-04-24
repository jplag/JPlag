import { describe, expect, it } from 'vitest'
import { BucketOptions, Distribution } from '../src'

const distributionData = [
  0, 7, 15, 42, 109, 225, 470, 869, 1442, 2052, 3025, 4056, 5091, 6130, 7023, 7292, 7445, 7177,
  6343, 5373, 4309, 3163, 2244, 1544, 923, 493, 273, 168, 61, 31, 8, 12, 2, 1, 0, 1, 2, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
]
const distribution = new Distribution(distributionData)

describe('Distribution', () => {
  ;(it('get in 10 Buckets', () => {
    expect(distribution.splitIntoBuckets(10)).toEqual([0, 0, 0, 0, 0, 0, 26, 13209, 58955, 5231])
  }),
    it('get in 100 Buckets', () => {
      const reversedOriginal = Array.from(distributionData).reverse()
      expect(distribution.splitIntoBuckets(100)).toEqual(reversedOriginal)
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
      const options = [10, 20, 25, 50, 100] as BucketOptions[]
      options.forEach((option) => {
        const bucketSum = distribution.splitIntoBuckets(option).reduce((acc, val) => acc + val, 0)
        expect(bucketSum).toEqual(checkSum)
      })
    }))
})
