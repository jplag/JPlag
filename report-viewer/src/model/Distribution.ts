export abstract class Distribution {
  protected readonly _distribution: number[]

  constructor(distribution: number[]) {
    this._distribution = distribution
  }

  /**
   * Returns the distribution summed at every tenth percentile
   */
  public abstract splitIntoTenBuckets(): number[]
}
