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
      return this._match.startInFirst.line
    } else {
      return this._match.startInSecond.line
    }
  }

  /**
   * The end line of the match.
   */
  get end(): number {
    if (this._index === 1) {
      return this._match.endInFirst.line
    } else {
      return this._match.endInSecond.line
    }
  }

  get startColumn(): number {
    if (this._index === 1) {
      return this._match.startInFirst.column
    } else {
      return this._match.startInSecond.column
    }
  }

  get endColumn(): number {
    if (this._index === 1) {
      return this._match.endInFirst.column
    } else {
      return this._match.endInSecond.column
    }
  }
}
