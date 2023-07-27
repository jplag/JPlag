import type { Match } from './Match'

/**
 * Describes a match in a single file.
 * @property start - Starting line of the match.
 * @property end - Ending line of the match.
 * @property linked_panel - The files container containing the file of the second submission to which this is matched.
 * @property linked_file - The file name containing the same match in the second submission.
 * @property linked_line - The start of the match in the second file.
 * @property color - Color of the match.
 */
export class MatchInSingleFile {
  private readonly _match: Match
  private readonly _index: 1 | 2

  constructor(match: Match, index: 1 | 2) {
    this._match = match
    this._index = index
  }

  get match(): Match {
    return this._match
  }

  get start(): number {
    if (this._index === 1) {
      return this._match.startInFirst
    } else {
      return this._match.startInSecond
    }
  }

  get end(): number {
    if (this._index === 1) {
      return this._match.endInFirst
    } else {
      return this._match.endInSecond
    }
  }

  get file(): string {
    if (this._index === 1) {
      return this._match.firstFile
    } else {
      return this._match.secondFile
    }
  }
}
