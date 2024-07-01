import type { CodePosition } from './Match'
import { MatchInSingleFile } from './MatchInSingleFile'

export class BaseCodeMatch extends MatchInSingleFile {
  constructor(fileName: string, start: CodePosition, end: CodePosition, tokens: number) {
    super(
      {
        firstFile: fileName,
        secondFile: fileName,
        startInFirst: start,
        endInFirst: end,
        startInSecond: start,
        endInSecond: end,
        colorIndex: 'base',
        tokens
      },
      1
    )
  }
}
