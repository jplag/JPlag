import { Overview } from '../Overview'
import type { ComparisonListElement } from '../ComparisonListElement'
import type { Cluster } from '@/model/Cluster'
import { store } from '@/stores/store'
import { Version, minimalReportVersion, reportViewerVersion } from '../Version'
import { getLanguageParser } from '../Language'
import { Distribution } from '../Distribution'
import { MetricType } from '../MetricType'
import { BaseFactory } from './BaseFactory'

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
   * Creates an overview object from a json object created by JPlag
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
      this.extractTopComparisons(json.top_comparisons as Array<Record<string, unknown>>, clusters),
      this.extractDistributions(json.distributions as Record<string, Array<number>>),
      clusters,
      totalComparisons
    )
  }

  private static extractDistributions(
    json: Record<string, Array<number>>
  ): Record<MetricType, Distribution> {
    const distributions = {} as Record<MetricType, Distribution>
    for (const [key, value] of Object.entries(json)) {
      distributions[key as MetricType] = new Distribution(value as Array<number>)
    }
    return distributions
  }

  private static extractTopComparisons(
    json: Array<Record<string, unknown>>,
    clusters: Cluster[]
  ): Array<ComparisonListElement> {
    const comparisons = [] as Array<ComparisonListElement>
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
