import slash from 'slash'
import { BaseCodeMatch } from '../BaseCodeReport'
import { BaseFactory } from './BaseFactory'

export class BaseCodeReportFactory extends BaseFactory {
  static basePath = 'basecode'

  public static async getReport(submissionId: string): Promise<BaseCodeMatch[]> {
    const a = await this.extractReport(
      JSON.parse(await this.getFile(slash(`${this.basePath}/${submissionId}.json`)))
    )
    return a
  }

  private static async extractReport(json: Record<string, unknown>[]): Promise<BaseCodeMatch[]> {
    return json.map((match: Record<string, unknown>) => {
      return new BaseCodeMatch(
        match['file_name'] as string,
        {
          line: match['startLine'] as number,
          column: (match['startCol'] as number) - 1,
          tokenListIndex: match['startIndex'] as number
        },
        {
          line: match['endLine'] as number,
          column: (match['endCol'] as number) - 1,
          tokenListIndex: match['endIndex'] as number
        },
        match['tokens'] as number
      )
    })
  }
}
