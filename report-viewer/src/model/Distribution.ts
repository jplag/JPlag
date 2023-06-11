export default class Distribution {
  private readonly _distribution: Array<number>

  constructor(distribution: Array<number>) {
    this._distribution = distribution
  }

  public asLinearArray(): Array<number> {
    return this._distribution
  }
}
