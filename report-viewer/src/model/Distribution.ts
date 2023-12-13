export abstract class Distribution {
  protected readonly _distribution: number[]

  constructor(distribution: number[]) {
    this._distribution = distribution
  }

  /**
   * Returns the distribution summed at every tenth percentile, the last percentile (90%-100%) should be at index 1
   */
  public abstract splitIntoTenBuckets(): number[]
}
