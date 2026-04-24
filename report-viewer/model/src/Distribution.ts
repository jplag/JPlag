import type { MetricJsonIdentifier } from './MetricJsonIdentifier'

export class Distribution {
  protected readonly _distribution: number[]

  constructor(distribution: number[]) {
    this._distribution = distribution
  }

  /**
   * Splits the distribution into the given number of buckets
   */
  public splitIntoBuckets(bucketCount: BucketOptions): number[] {
    const bucketArray = new Array<number>(bucketCount).fill(0)
    const divisor = 100 / bucketCount
    const reversedDistribution = Array.from(this._distribution).reverse()
    for (let i = 99; i >= 0; i--) {
      bucketArray[Math.floor(i / divisor)] += reversedDistribution[i]
    }
    return bucketArray
  }
}

type BucketOptions = 10 | 20 | 25 | 50 | 100
export type DistributionMetrics =
  | MetricJsonIdentifier.AVERAGE_SIMILARITY
  | MetricJsonIdentifier.MAXIMUM_SIMILARITY
type DistributionMap = Record<DistributionMetrics, Distribution>
export type { BucketOptions, DistributionMap }
