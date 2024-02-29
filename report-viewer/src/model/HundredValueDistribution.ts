import { Distribution } from './Distribution'

/**
 * This class represents he JPlag Distribution of a metric.
 * It is composed of 100 values, each representing the sum of the values of the metric in the corresponding percentile.
 */
export class HundredValueDistribution extends Distribution {
  constructor(distribution: number[]) {
    super(distribution)
  }

  /**
   * Returns the distribution summed at every tenth percentile
   */
  public splitIntoTenBuckets(): number[] {
    const tenValueArray = new Array<number>(10).fill(0)
    for (let i = 99; i >= 0; i--) {
      tenValueArray[Math.floor(i / 10)] += this._distribution[i]
    }
    return tenValueArray
  }
}
