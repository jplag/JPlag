export default class Distribution {
  private readonly _distribution: Array<number>

  constructor(distribution: Array<number>) {
    this._distribution = distribution
  }

  get distribution(): Array<number> {
    return this._distribution
  }
}
