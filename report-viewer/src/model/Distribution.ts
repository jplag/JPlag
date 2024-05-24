export class Distribution {
  protected readonly _distribution: number[]

  constructor(distribution: number[]) {
    this._distribution = distribution
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
