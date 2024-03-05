import slash from 'slash'
import type { BaseCodeMatch } from '../BaseCodeReport'
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
    return json
      .map((match: Record<string, unknown>) => {
        return {
          fileName: match['file_name'] as string,
          start: match['start'] as number,
          end: match['end'] as number,
          tokens: match['tokens'] as number
        }
      })
      .sort((a, b) => a.start - b.start)
  }
}
