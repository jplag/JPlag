import { Logger } from '@jplag/logger'
import { Distribution, type DistributionMap, MetricJsonIdentifier } from '@jplag/model'

export class DistributionFactory {
  public static getDistributions(distributionFile: string): DistributionMap {
    Logger.label('DistributionFactory').info('Parse distributions')
    return this.extractDistributions(JSON.parse(distributionFile))
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
