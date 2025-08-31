import slash from 'slash'
import { BaseCodeMatch, CodePosition } from '@jplag/model'
import { Logger } from '@jplag/logger'

export class BaseCodeReportFactory {
  private static readonly basePath = 'basecode'

  public static getReport(baseCodeReport: string): BaseCodeMatch[] {
    Logger.label('BaseCodeReportFactory').info('Parse base code report')
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
