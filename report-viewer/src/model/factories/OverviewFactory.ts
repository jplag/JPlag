import { Overview } from '../Overview'
import type { ComparisonListElement } from '../ComparisonListElement'
import type { Cluster } from '@/model/Cluster'
import { store } from '@/stores/store'
import { Version, minimalReportVersion, reportViewerVersion } from '../Version'
import { getLanguageParser } from '../Language'
import { Distribution } from '../Distribution'
import { MetricType } from '../MetricType'
import { BaseFactory } from './BaseFactory'
import { HundredValueDistribution } from '../HundredValueDistribution'
import { TenValueDistribution } from '../TenValueDistribution'

/**
 * Factory class for creating Overview objects
 */
export class OverviewFactory extends BaseFactory {
  /**
   * Gets the overview file based on the used mode (zip, local, single).
   */
  public static async getOverview(): Promise<Overview> {
    return this.extractOverview(JSON.parse(await this.getFile('overview.json')))
  }

  /**
   * Creates an overview object from a json object created by by JPlag
   * @param json the json object
   */
  private static extractOverview(json: Record<string, unknown>): Overview {
    const versionField = json.jplag_version as Record<string, number>
    const jplagVersion = Version.fromJsonField(versionField)

    OverviewFactory.compareVersions(jplagVersion, reportViewerVersion, minimalReportVersion)

    const submissionFolder = json.submission_folder_path as Array<string>
    const baseCodeFolder = json.base_code_folder_path as string
    const language = getLanguageParser(json.language as string)
    const fileExtensions = json.file_extensions as Array<string>
    const matchSensitivity = json.match_sensitivity as number
    const dateOfExecution = json.date_of_execution as string
    const duration = json.execution_time as number as number
    const totalComparisons = json.total_comparisons as number
    const clusters = this.extractClusters(json)

    this.saveIdToDisplayNameMap(json)
    this.saveComparisonFilesLookup(json)

    return new Overview(
      submissionFolder,
      baseCodeFolder,
      language,
      fileExtensions,
      matchSensitivity,
      dateOfExecution,
      duration,
      this.extractTopComparisons(json, clusters),
      this.extractDistributions(json),
      clusters,
      totalComparisons
    )
  }

  private static extractDistributions(
    json: Record<string, unknown>
  ): Record<MetricType, Distribution> {
    if (json.distributions) {
      return this.extractDistributionsFromMap(json.distributions as Record<string, Array<number>>)
    } else if (json.metrics) {
      return this.extractDistributionsFromMetrics(json.metrics as Array<Record<string, unknown>>)
    }
    throw new Error('No distributions found')
  }

  private static extractDistributionsFromMap(
    distributionsMap: Record<string, Array<number>>
  ): Record<MetricType, Distribution> {
    const distributions = {} as Record<MetricType, Distribution>
    for (const [key, value] of Object.entries(distributionsMap)) {
      distributions[key as MetricType] = new HundredValueDistribution(value as Array<number>)
    }
    return distributions
  }

  /** @deprecated since 5.0.0. Use the new format with {@link extractDistributionsFromMap} */
  private static extractDistributionsFromMetrics(
    metrics: Array<Record<string, unknown>>
  ): Record<MetricType, Distribution> {
    return {
      [MetricType.AVERAGE]: new TenValueDistribution(metrics[0].distribution as Array<number>),
      [MetricType.MAXIMUM]: new TenValueDistribution(metrics[1].distribution as Array<number>)
    }
  }

  private static extractTopComparisons(
    json: Record<string, unknown>,
    clusters: Cluster[]
  ): Array<ComparisonListElement> {
    if (json.top_comparisons) {
      return this.extractTopComparisonsFromMap(
        json.top_comparisons as Array<Record<string, unknown>>,
        clusters
      )
    } else if (json.metrics) {
      return this.extractTopComparisonsFromMetrics(
        json.metrics as Array<Record<string, unknown>>,
        clusters
      )
    }
    throw new Error('No top comparisons found')
  }

  private static extractTopComparisonsFromMap(
    jsonComparisons: Array<Record<string, unknown>>,
    clusters: Cluster[]
  ) {
    const comparisons = [] as Array<ComparisonListElement>
    let counter = 0
    for (const topComparison of jsonComparisons) {
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

  /** @deprecated since 5.0.0. Use the new format with {@link extractTopComparisonsFromMap} */
  private static extractTopComparisonsFromMetrics(
    metrics: Array<Record<string, unknown>>,
    clusters: Cluster[]
  ) {
    const averageSimilarities: Map<string, number> = new Map<string, number>()
    const comparisons = [] as Array<ComparisonListElement>

    // Save the average similarities in a temporary map to combine them with the max similarities later
    for (const comparison of metrics[0].topComparisons as Array<Record<string, unknown>>) {
      averageSimilarities.set(
        (comparison.first_submission as string) + '-' + (comparison.second_submission as string),
        comparison.similarity as number
      )
    }

    // Extract the max similarities and combine them with the average similarities
    let counter = 0
    for (const topComparison of metrics[1].topComparisons as Array<Record<string, unknown>>) {
      const avg = averageSimilarities.get(
        (topComparison.first_submission as string) +
          '-' +
          (topComparison.second_submission as string)
      )
      const comparison = {
        sortingPlace: counter++,
        id: counter,
        firstSubmissionId: topComparison.first_submission as string,
        secondSubmissionId: topComparison.second_submission as string,
        similarities: {
          [MetricType.AVERAGE]: avg as number,
          [MetricType.MAXIMUM]: topComparison.similarity as number
        }
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

  private static extractClusters(json: Record<string, unknown>): Array<Cluster> {
    if (!json.clusters) {
      return []
    }

    const clusters = [] as Array<Cluster>
    for (const jsonCluster of json.clusters as Array<Record<string, unknown>>) {
      clusters.push({
        averageSimilarity: jsonCluster.average_similarity as number,
        strength: jsonCluster.strength as number,
        members: jsonCluster.members as Array<string>
      })
    }
    return clusters
  }

  private static saveIdToDisplayNameMap(json: Record<string, unknown>) {
    const jsonSubmissions = json.submission_id_to_display_name as Map<string, string>
    const map = new Map<string, string>(Object.entries(jsonSubmissions))

    store().saveSubmissionNames(map)
  }

  private static saveComparisonFilesLookup(json: Record<string, unknown>) {
    const submissionIdsToComparisonName = json.submission_ids_to_comparison_file_name as Map<
      string,
      Map<string, string>
    >
    const test: Array<Array<string | object>> = Object.entries(submissionIdsToComparisonName)
    const comparisonMap = new Map<string, Map<string, string>>()
    for (const [key, value] of test) {
      comparisonMap.set(key as string, new Map(Object.entries(value as object)))
    }

    store().saveComparisonFileLookup(comparisonMap)
  }

  /**
   * Compares the two versions and shows an alert if they are not equal and puts out a warning if they are not
   * @param jsonVersion the version of the json file
   * @param reportViewerVersion the version of the report viewer
   */
  static compareVersions(
    jsonVersion: Version,
    reportViewerVersion: Version,
    minimalVersion: Version = new Version(0, 0, 0)
  ) {
    if (sessionStorage.getItem('versionAlert') === null) {
      if (reportViewerVersion.isInvalid()) {
        console.warn(
          "The report viewer's version cannot be read from version.json file. Please configure it correctly."
        )
      } else if (
        !reportViewerVersion.isDevVersion() &&
        jsonVersion.compareTo(reportViewerVersion) > 0
      ) {
        alert(
          "The result's version(" +
            jsonVersion.toString() +
            ") is newer than the report viewer's version(" +
            reportViewerVersion.toString() +
            '). ' +
            'Trying to read it anyhow but be careful.'
        )
      }
      sessionStorage.setItem('versionAlert', 'true')
    }
    if (jsonVersion.compareTo(minimalVersion) < 0) {
      throw (
        "The result's version(" +
        jsonVersion.toString() +
        ') is older than the minimal support version of the report viewer(' +
        minimalVersion.toString() +
        '). ' +
        'Can not read the report.'
      )
    }
  }
}
