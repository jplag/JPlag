import type { Cluster } from '../Cluster'
import type { MetricType } from '../MetricType'
import { BaseFactory } from './BaseFactory'
import type { ComparisonListElement } from '../ComparisonListElement'

export class TopComparisonFactory extends BaseFactory {
  public static async getTopComparisons(clusters: Cluster[]): Promise<ComparisonListElement[]> {
    return this.extractTopComparisons(
      JSON.parse(await this.getFile('topComparisons.json')),
      clusters
    )
  }

  private static extractTopComparisons(
    json: Array<Record<string, unknown>>,
    clusters: Cluster[]
  ): ComparisonListElement[] {
    const comparisons = [] as ComparisonListElement[]
    let counter = 0
    for (const topComparison of json) {
      const comparison = {
        sortingPlace: counter++,
        id: counter,
        firstSubmissionId: topComparison.first_submission as string,
        secondSubmissionId: topComparison.second_submission as string,
        similarities: topComparison.similarities as Record<MetricType, number>
      }
      comparisons.push({
        ...comparison,
        clusterIndex: this.getClusterIndex(
          clusters,
          comparison.firstSubmissionId,
          comparison.secondSubmissionId
        )
      })
    }
    return comparisons
  }

  private static getClusterIndex(
    clusters: Cluster[],
    firstSubmissionId: string,
    secondSubmissionId: string
  ) {
    let clusterIndex = -1
    clusters?.forEach((c: Cluster, index: number) => {
      if (
        c.members.includes(firstSubmissionId) &&
        c.members.includes(secondSubmissionId) &&
        c.members.length > 2
      ) {
        clusterIndex = index
      }
    })
    return clusterIndex
  }
}
