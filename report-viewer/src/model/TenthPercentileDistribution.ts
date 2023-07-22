import Distribution from './Distribution'

export default class TenValueDistribution extends Distribution {
  constructor(distribution: number[]) {
    super(distribution)
  }

  public getTenthPercentileFormattedValues(): number[] {
    return this._distribution
  }
}
