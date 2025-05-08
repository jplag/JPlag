import slash from 'slash'
import { BaseCodeMatch } from '../BaseCodeReport'
import { BaseFactory } from './BaseFactory'
import type { CodePosition } from '../Match'

export class BaseCodeReportFactory extends BaseFactory {
  private static readonly basePath = 'basecode'

  public static async getReport(submissionId: string): Promise<BaseCodeMatch[]> {
    return this.extractReport(
      JSON.parse(await this.getFile(slash(`${this.basePath}/${submissionId}.json`)))
    )
  }

  private static async extractReport(json: ReportFormatBaseCodeMatch[]): Promise<BaseCodeMatch[]> {
    return json.map((match) => {
      return new BaseCodeMatch(match.fileName, match.start, match.end, match.tokens)
    })
  }
}

interface ReportFormatBaseCodeMatch {
  fileName: string
  start: CodePosition
  end: CodePosition
  tokens: number
}
