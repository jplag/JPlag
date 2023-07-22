import Distribution from './Distribution'

export default class HundredValueDistribution extends Distribution {
  constructor(distribution: number[]) {
    super(distribution)
  }

  /**
   * Returns the distribution summed at every tenth percentile
   */
  public getTenthPercentileFormattedValues(): number[] {
    const tenValueArray = new Array<number>(10).fill(0)
    for (let i = 0; i < 100; i++) {
      tenValueArray[Math.floor(i / 10)] += this._distribution[i]
    }
    return tenValueArray
  }
}
