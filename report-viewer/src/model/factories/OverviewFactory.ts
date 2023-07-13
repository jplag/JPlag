import { Overview } from '../Overview'
import type { ComparisonListElement } from '../ComparisonListElement'
import type { Cluster } from '@/model/Cluster'
import store from '@/stores/store'
import type { Version } from '../Version'
import versionJson from '@/version.json'
import Distribution from '../Distribution'
import MetricType from '../MetricType'
import { BaseFactory } from './BaseFactory'

export class OverviewFactory extends BaseFactory {
  static reportViewerVersion: Version =
    versionJson['report_viewer_version'] !== undefined
      ? versionJson['report_viewer_version']
      : { major: -1, minor: -1, patch: -1 }

  /**
   * Gets the overview file based on the used mode (zip, local, single).
   */
  public static getOverview(): Overview {
    return this.extractOverview(JSON.parse(this.getFile('overview.json')))
  }

  /**
   * Creates an overview object from a json object created by by JPlag
   * @param json the json object
   */
  private static extractOverview(json: Record<string, unknown>): Overview {
    const jplagVersion = this.extractVersion(json)
    OverviewFactory.compareVersions(jplagVersion, this.reportViewerVersion)

    const submissionFolder = json.submission_folder_path as Array<string>
    const baseCodeFolder = json.base_code_folder_path as string
    const language = json.language as string
    const fileExtensions = json.file_extensions as Array<string>
    const matchSensitivity = json.match_sensitivity as number
    const dateOfExecution = json.date_of_execution as string
    const duration = json.execution_time as number as number
    const totalComparisons = json.total_comparisons as number

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
      this.extractTopComparisons(json),
      this.extractDistributions(json),
      this.extractClusters(json),
      totalComparisons
    )
  }

  private static extractVersion(json: Record<string, unknown>): Version {
    const versionField = json.jplag_version as Record<string, number>
    return {
      major: versionField.major,
      minor: versionField.minor,
      patch: versionField.patch
    }
  }

  private static extractDistributions(
    json: Record<string, unknown>
  ): Record<MetricType, Distribution> {
    const distributionsMap = json.distributions as Record<string, Array<number>>
    const distributions = {} as Record<MetricType, Distribution>
    for (const [key, value] of Object.entries(distributionsMap)) {
      distributions[key as MetricType] = new Distribution(value as Array<number>)
    }
    return distributions
  }

  private static extractTopComparisons(
    json: Record<string, unknown>
  ): Array<ComparisonListElement> {
    const jsonComparisons = json.top_comparisons as Array<Record<string, unknown>>
    const comparisons = [] as Array<ComparisonListElement>
    let counter = 0
    for (const topComparison of jsonComparisons) {
      comparisons.push({
        sortingPlace: counter++,
        id: counter,
        firstSubmissionId: topComparison.first_submission as string,
        secondSubmissionId: topComparison.second_submission as string,
        similarities: topComparison.similarities as Record<MetricType, number>
      })
    }
    return comparisons
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
  static compareVersions(jsonVersion: Version, reportViewerVersion: Version) {
    if (sessionStorage.getItem('versionAlert') === null) {
      if (
        reportViewerVersion.major === 0 &&
        reportViewerVersion.minor === 0 &&
        reportViewerVersion.patch === 0
      ) {
        alert('The development version (0.0.0) of JPlag is used.')
      }

      if (
        jsonVersion.major !== reportViewerVersion.major ||
        jsonVersion.minor !== reportViewerVersion.minor ||
        jsonVersion.patch !== reportViewerVersion.patch
      ) {
        if (
          reportViewerVersion.major === -1 &&
          reportViewerVersion.minor === -1 &&
          reportViewerVersion.patch === -1
        ) {
          console.warn(
            "The report viewer's version cannot be read from version.json file. Please configure it correctly."
          )
        } else {
          console.warn(
            "The result's version tag does not fit the report viewer's version. Trying to read it anyhow but be careful."
          )
          alert(
            "The result's version(" +
              jsonVersion.major +
              '.' +
              jsonVersion.minor +
              '.' +
              jsonVersion.patch +
              ") tag does not fit the report viewer's version(" +
              reportViewerVersion.major +
              '.' +
              reportViewerVersion.minor +
              '.' +
              reportViewerVersion.patch +
              '). ' +
              'Trying to read it anyhow but be careful.'
          )
        }
      }

      sessionStorage.setItem('versionAlert', 'true')
    }
  }
}
