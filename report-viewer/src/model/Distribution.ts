export default abstract class Distribution {
  protected readonly _distribution: Array<number>

  constructor(distribution: Array<number>) {
    this._distribution = distribution
  }

  /**
   * Returns the distribution summed at every tenth percentile
   */
  public abstract getTenthPercentileFormattedValues(): Array<number>
}
