import Distribution from './Distribution'

/**
 * This class represents he JPlag Distribution of a metric.
 * It is composed of 10 values, each representing the sum of the values of the metric in the corresponding percentiles.
 */
export default class TenValueDistribution extends Distribution {
  constructor(distribution: number[]) {
    super(distribution)
  }

  public splitIntoTenBuckets(): number[] {
    return this._distribution
  }
}
