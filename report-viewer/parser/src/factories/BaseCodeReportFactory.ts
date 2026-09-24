import slash from 'slash'
import { BaseCodeMatch, CodePosition } from '@jplag/model'

export class BaseCodeReportFactory {
  private static readonly basePath = 'basecode'

  public static getReport(baseCodeReport: string): BaseCodeMatch[] {
    return this.extractReport(JSON.parse(baseCodeReport))
  }

  private static extractReport(json: ReportFormatBaseCodeMatch[]): BaseCodeMatch[] {
    return json.map((match) => {
      return new BaseCodeMatch(slash(match.fileName), match.start, match.end, match.tokens)
    })
  }
}

interface ReportFormatBaseCodeMatch {
  fileName: string
  start: CodePosition
  end: CodePosition
  tokens: number
}
