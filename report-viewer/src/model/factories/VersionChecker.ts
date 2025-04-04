import { store } from '@/stores/store'
import { minimalReportVersion, reportViewerVersion, Version } from '../Version'
import { BaseFactory } from './BaseFactory'

export class VersionChecker extends BaseFactory {
  public static async verifyVersion(): Promise<VersionResponse> {
    const hasLoadedFiles = store().state.zipModeUsed || store().state.localModeUsed
    if (!hasLoadedFiles) {
      return undefined
    }
    let version = Version.ERROR_VERSION
    try {
      version = this.extractVersion(JSON.parse(await this.getFile('runInformation.json')))
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {
      version = this.extractVersion(JSON.parse(await this.getFile('overview.json')))
    }
    return {
      valid: this.compareVersions(version, reportViewerVersion, minimalReportVersion),
      version: version
    }
  }

  private static extractVersion(json: Record<string, unknown>): Version {
    const versionField = json.jplag_version as Record<string, number>
    const jplagVersion = Version.fromJsonField(versionField)
    return jplagVersion
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

type VersionResponse =
  | {
      valid: boolean
      version: Version
    }
  | undefined
