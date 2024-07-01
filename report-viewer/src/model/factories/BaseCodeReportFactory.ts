import slash from 'slash'
import { BaseCodeMatch } from '../BaseCodeReport'
import { BaseFactory } from './BaseFactory'
import type { CodePosition } from '../Match'

export class BaseCodeReportFactory extends BaseFactory {
  private static readonly basePath = 'basecode'

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
        match['start'] as CodePosition,
        match['end'] as CodePosition,
        match['tokens'] as number
      )
    })
  }
}
