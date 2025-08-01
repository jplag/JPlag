import { Distribution, type DistributionMap } from '../Distribution'
import type { MetricJsonIdentifier } from '../MetricJsonIdentifier'
import { BaseFactory } from './BaseFactory'

export class DistributionFactory extends BaseFactory {
  public static async getDistributions(): Promise<DistributionMap> {
    return this.extractDistributions(JSON.parse(await this.getFile('distribution.json')))
  }

  private static extractDistributions(json: ReportFormatDistributionMap): DistributionMap {
    const distributions = {} as DistributionMap
    for (const [key, value] of Object.entries(json)) {
      distributions[key as MetricJsonIdentifier] = new Distribution(value)
    }
    return distributions
  }
}

type ReportFormatDistributionMap = Record<string, number[]>
