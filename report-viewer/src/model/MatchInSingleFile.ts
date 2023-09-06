import type { Match } from './Match'

/**
 * Describes a match in a single file.
 */
export class MatchInSingleFile {
  private readonly _match: Match
  private readonly _index: 1 | 2

  constructor(match: Match, index: 1 | 2) {
    this._match = match
    this._index = index
  }

  /**
   * The match object.
   */
  get match(): Match {
    return this._match
  }

  /**
   * The start line of the match.
   */
  get start(): number {
    if (this._index === 1) {
      return this._match.startInFirst
    } else {
      return this._match.startInSecond
    }
  }

  /**
   * The end line of the match.
   */
  get end(): number {
    if (this._index === 1) {
      return this._match.endInFirst
    } else {
      return this._match.endInSecond
    }
  }
}
