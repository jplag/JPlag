import {
  Distribution,
  type DistributionMap,
  DistributionMetrics,
  MetricJsonIdentifier
} from '@jplag/model'

export class DistributionFactory {
  public static getDistributions(distributionFile: string): DistributionMap {
    return this.extractDistributions(JSON.parse(distributionFile))
  }

  private static extractDistributions(json: ReportFormatDistributionMap): DistributionMap {
    const distributions = {} as DistributionMap
    for (const metric of [
      MetricJsonIdentifier.AVERAGE_SIMILARITY,
      MetricJsonIdentifier.MAXIMUM_SIMILARITY
    ] as DistributionMetrics[]) {
      distributions[metric] = new Distribution(json[metric])
    }
    return distributions
  }
}

type ReportFormatDistributionMap = Record<string, number[]>
