import { store } from '@/stores/store'
import type { BaseCodeMatch } from '../BaseCodeReport'
import type { CliOptions } from '../CliOptions'
import type { Cluster } from '../Cluster'
import type { Comparison } from '../Comparison'
import type { DistributionMap } from '../Distribution'
import { RunInformation } from '../RunInformation'
import { minimalReportVersion, reportViewerVersion, Version } from '../Version'
import { SubmissionMappingsFactory } from './SubmissionMappingsFactory'
import { BaseCodeReportFactory } from './BaseCodeReportFactory'
import { ComparisonFactory } from './ComparisonFactory'
import { ClusterFactory } from './ClusterFactory'
import { DistributionFactory } from './DistributionFactory'
import { OptionsFactory } from './OptionsFactory'
import { RunInformationFactory } from './RunInformationFactory'
import { TopComparisonFactory } from './TopComparisonFactory'
import type { ComparisonListElement } from '../ComparisonListElement'
import { BaseFactory } from './BaseFactory'

/**
 * All the file types that can be requested without further information
 */
class RawFileContentTypes {
  public static readonly CLUSTER = 'cluster'
  public static readonly DISTRIBUTION = 'distribution'
  public static readonly OPTIONS = 'options'
  public static readonly RUN_INFORMATION = 'runInformation'
  public static readonly TOP_COMPARISON = 'topComparison'
}

/**
 * Names of all file types requestable
 */
export class FileContentTypes extends RawFileContentTypes {
  public static readonly BASE_CODE_REPORT = 'baseCodeReport'
  public static readonly COMPARISON = 'comparison'
}

interface Result {
  [FileContentTypes.BASE_CODE_REPORT]: BaseCodeMatch[][]
  [FileContentTypes.CLUSTER]: Cluster[]
  [FileContentTypes.COMPARISON]: Comparison
  [FileContentTypes.DISTRIBUTION]: DistributionMap
  [FileContentTypes.OPTIONS]: CliOptions
  [FileContentTypes.RUN_INFORMATION]: RunInformation
  [FileContentTypes.TOP_COMPARISON]: ComparisonListElement[]
}

/**
 * Result the data getter produces
 */
type PartialResult = Partial<Result>

/**
 * All possible requests to the data getter
 */
type FileRequest =
  | 'cluster'
  | 'distribution'
  | 'options'
  | 'runInformation'
  | 'topComparison'
  | { type: 'baseCodeReport'; submissionIds: string[] }
  | { type: 'comparison'; firstSubmission: string; secondSubmission: string }

type VersionResponse =
  | {
      valid: boolean
      version: Version
    }
  | undefined

/**
 * Complete response the data getter gives
 * @template T can be used to force certain types to not be undefined in the result if they match the fileRequests
 */
type Response<T extends PartialResult> =
  | {
      result: 'valid'
      data: PartialResult & T
    }
  | {
      result: 'versionError'
      reportVersion: Version
    }
  | {
      result: 'error'
      error: Error
    }

/**
 * The DataGetter is responsible for addressing the parsers and getting the data from the report
 * It is also responsible for checking the version of the report and the report viewer
 */
export class DataGetter extends BaseFactory {
  /**
   * The parameter T can be used to force certain types to not be undefined in the result if they match the fileRequests
   * @param fileRequests
   * @returns The result of the request. The result flag is valid if the request was successful and the data is valid, error if something went wrong and versionError if the version of the report is not supported
   */
  public static async getFiles<T extends PartialResult>(
    fileRequests: FileRequest[]
  ): Promise<Response<T>> {
    // perform version check to ensure we can even read data
    const versionResult = await this.verifyVersion()
    if (versionResult === undefined) {
      return {
        result: 'error',
        error: new Error('No data found')
      }
    }
    if (!versionResult.valid) {
      return {
        result: 'versionError',
        reportVersion: versionResult.version
      }
    }
    // First we need to get the mappings for names and comparison files if any are empty, so the parsers can use them
    if (
      store().state.fileIdToDisplayName.size === 0 ||
      store().state.submissionIdsToComparisonFileName.size === 0
    ) {
      await SubmissionMappingsFactory.getSubmissionMappings()
    }

    try {
      return await this.getData<T>(fileRequests)
    } catch (e) {
      return {
        result: 'error',
        error: e as Error
      }
    }
  }

  private static async getData<T extends PartialResult>(
    fileRequests: FileRequest[]
  ): Promise<Response<T>> {
    const data: PartialResult = {}

    for (const request of fileRequests) {
      const type = typeof request === 'string' ? request : request.type
      switch (type) {
        case RawFileContentTypes.CLUSTER:
          data[FileContentTypes.CLUSTER] = await ClusterFactory.getClusters()
          break
        case RawFileContentTypes.DISTRIBUTION:
          data[FileContentTypes.DISTRIBUTION] = await DistributionFactory.getDistributions()
          break
        case RawFileContentTypes.OPTIONS:
          data[FileContentTypes.OPTIONS] = await OptionsFactory.getCliOptions()
          break
        case RawFileContentTypes.RUN_INFORMATION:
          data[FileContentTypes.RUN_INFORMATION] = await RunInformationFactory.getRunInformation()
          break
        case RawFileContentTypes.TOP_COMPARISON:
          data[FileContentTypes.TOP_COMPARISON] = await this.getTopComparisons(
            data[FileContentTypes.CLUSTER]
          )
          break
        case FileContentTypes.BASE_CODE_REPORT:
          data[FileContentTypes.BASE_CODE_REPORT] = await this.getBaseCodeReport(
            (request as { submissionIds: string[] }).submissionIds
          )
          break
        case FileContentTypes.COMPARISON: {
          const fileName = store().getComparisonFileName(
            (request as { firstSubmission: string }).firstSubmission,
            (request as { secondSubmission: string }).secondSubmission
          )
          if (!fileName) {
            return {
              result: 'error',
              error: new Error('No comparison file found')
            }
          }
          data[FileContentTypes.COMPARISON] = await ComparisonFactory.getComparison(fileName)
          break
        }
      }
    }
    return {
      result: 'valid',
      data: data as PartialResult & T
    }
  }

  private static async getBaseCodeReport(submissionIds: string[]): Promise<BaseCodeMatch[][]> {
    const baseCodeMatches: BaseCodeMatch[][] = []
    for (const submissionId of submissionIds) {
      const baseCodeMatch = await BaseCodeReportFactory.getReport(submissionId)
      baseCodeMatches.push(baseCodeMatch)
    }
    return baseCodeMatches
  }

  private static async getTopComparisons(clusters?: Cluster[]): Promise<ComparisonListElement[]> {
    if (clusters === undefined) {
      clusters = await ClusterFactory.getClusters()
    }
    return TopComparisonFactory.getTopComparisons(clusters)
  }

  /**
   * Verifies the version of the report compared to the version of the report viewer
   * If the found version is newer then the report viewers version, it displays a warning
   * @returns If no information on versions was found, it returns undefined. Otherwise the valid field is true if the version is valid and false if it is not
   */
  private static async verifyVersion(): Promise<VersionResponse> {
    let version = Version.ERROR_VERSION
    try {
      const runInformation = JSON.parse(await this.getFile('runInformation.json'))
      version = Version.fromJsonField(runInformation.version)
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {
      try {
        const oldOverview = JSON.parse(await this.getFile('overview.json'))
        version = Version.fromJsonField(oldOverview.jplag_version)
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (e) {
        return undefined
      }
    }
    return {
      valid: this.compareVersions(version, reportViewerVersion, minimalReportVersion),
      version: version
    }
  }

  /**
   * Compares the two versions and shows an alert if they are not equal and puts out a warning if they are not
   * @param jsonVersion the version of the json file
   * @param reportViewerVersion the version of the report viewer
   * @param minimalVersion the minimal report version expected
   * @return true if the version is supported, false if the version is old
   */
  private static compareVersions(
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
    return jsonVersion.compareTo(minimalVersion) >= 0
  }
}
