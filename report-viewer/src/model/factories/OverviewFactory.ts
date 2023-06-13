import { Overview } from '../Overview'
import type { ComparisonListElement } from '../ComparisonListElement'
import type { Cluster } from '@/model/Cluster'
import store from '@/stores/store'
import type { Version } from '../Version'
import versionJson from '@/version.json'
import Distribution from '../Distribution'

export class OverviewFactory {
  static reportViewerVersion: Version =
    versionJson['report_viewer_version'] !== undefined
      ? versionJson['report_viewer_version']
      : { major: -1, minor: -1, patch: -1 }

  /**
   * Creates an overview object from a json object created by by JPlag
   * @param json the json object
   */
  static extractOverview(json: Record<string, unknown>): Overview {
    const versionField = json.jplag_version as Record<string, number>
    const jplagVersion: Version = {
      major: versionField.major,
      minor: versionField.minor,
      patch: versionField.patch
    }

    OverviewFactory.compareVersions(jplagVersion, this.reportViewerVersion)

    const submissionFolder = json.submission_folder_path as Array<string>
    const baseCodeFolder = ''
    const language = json.language as string
    const fileExtensions = json.file_extensions as Array<string>
    const matchSensitivity = json.match_sensitivity as number
    const jsonSubmissions = json.submission_id_to_display_name as Map<string, string>
    const map = new Map<string, string>(Object.entries(jsonSubmissions))
    const dateOfExecution = json.date_of_execution as string
    const duration = json.execution_time as number as number
    const clusters = [] as Array<Cluster>
    const totalComparisons = json.total_comparisons as number

    const distributions = [] as Array<Distribution>
    const averageSimilarities: Map<string, number> = new Map<string, number>()
    const comparisons = [] as Array<ComparisonListElement>

    const metrics = json.metrics as Array<unknown> as Array<Record<string, unknown>>
    // Average
    distributions.push(new Distribution(metrics[0].distribution as Array<number>))
    for (const comparison of metrics[0].topComparisons as Array<Record<string, unknown>>) {
      averageSimilarities.set(
        (comparison.first_submission as string) + '-' + (comparison.second_submission as string),
        comparison.similarity as number
      )
    }

    // Max
    distributions.push(new Distribution(metrics[1].distribution as Array<number>))
    let counter = 1
    for (const comparison of metrics[1].topComparisons as Array<Record<string, unknown>>) {
      const avg = averageSimilarities.get(
        (comparison.first_submission as string) + '-' + (comparison.second_submission as string)
      )
      comparisons.push({
        id: counter++,
        firstSubmissionId: comparison.first_submission as string,
        secondSubmissionId: comparison.second_submission as string,
        averageSimilarity: avg as number,
        maximumSimilarity: comparison.similarity as number
      })
    }

    store().saveSubmissionNames(map)

    if (json.clusters) {
      ;(json.clusters as Array<unknown>).forEach((jsonCluster) => {
        const cluster = jsonCluster as Record<string, unknown>
        const newCluster: Cluster = {
          averageSimilarity: cluster.average_similarity as number,
          strength: cluster.strength as number,
          members: cluster.members as Array<string>
        }
        clusters.push(newCluster)
      })
    }

    OverviewFactory.saveSubmissionsToComparisonNameMap(json)
    return new Overview(
      submissionFolder,
      baseCodeFolder,
      language,
      fileExtensions,
      matchSensitivity,
      dateOfExecution,
      duration,
      comparisons,
      distributions,
      clusters,
      totalComparisons,
      new Map()
    )
  }

  /**
   * Gets the overview file based on the used mode (zip, local, single).
   */
  static getOverview(): Overview {
    console.log('Generating overview...')
    let temp!: Overview
    //Gets the overview file based on the used mode (zip, local, single).
    if (store().state.local) {
      const request = new XMLHttpRequest()
      request.open('GET', '/files/overview.json', false)
      request.send()

      if (request.status == 200) {
        temp = OverviewFactory.extractOverview(JSON.parse(request.response))
      } else {
        throw 'Could not find overview.json in folder.'
      }
    } else if (store().state.zip) {
      console.log('Start finding overview.json in state...')
      const index = Object.keys(store().state.files).find((name) => name.endsWith('overview.json'))
      const overviewFile =
        index != undefined
          ? store().state.files[index]
          : console.log('Could not find overview.json')

      if (overviewFile === undefined) {
        return new Overview(
          [],
          '',
          '',
          [],
          0,
          '',
          0,
          [],
          [],
          [],
          0,
          new Map<string, Map<string, string>>()
        )
      }
      const overviewJson = JSON.parse(overviewFile)
      temp = OverviewFactory.extractOverview(overviewJson)
    } else if (store().state.single) {
      temp = OverviewFactory.extractOverview(JSON.parse(store().state.fileString))
    }
    return temp
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

  private static saveSubmissionsToComparisonNameMap(json: Record<string, unknown>) {
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
}
