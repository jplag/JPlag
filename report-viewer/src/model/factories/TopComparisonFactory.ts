import type { Cluster } from '../Cluster'
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
    json: ReportFormatTopComparison[],
    clusters: Cluster[]
  ): ComparisonListElement[] {
    const comparisons = [] as ComparisonListElement[]
    let counter = 0
    for (const topComparison of json) {
      const comparison = {
        sortingPlace: counter++,
        id: counter,
        firstSubmissionId: topComparison.firstSubmission,
        secondSubmissionId: topComparison.secondSubmission,
        similarities: topComparison.similarities
      }
      comparisons.push({
        ...comparison,
        cluster: this.getCluster(
          clusters,
          comparison.firstSubmissionId,
          comparison.secondSubmissionId
        )
      })
    }
    return comparisons
  }

  private static getCluster(
    clusters: Cluster[],
    firstSubmissionId: string,
    secondSubmissionId: string
  ) {
    return clusters.find((c: Cluster) => {
      if (
        c.members.includes(firstSubmissionId) &&
        c.members.includes(secondSubmissionId) &&
        c.members.length > 2
      ) {
        return true
      }
    })
  }
}

interface ReportFormatTopComparison {
  firstSubmission: string
  secondSubmission: string
  similarities: Record<string, number>
}
