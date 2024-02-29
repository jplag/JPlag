import versionJson from '@/version.json'

/**
 * Version of the report viewer.
 */
export class Version {
  private major: number
  private minor: number
  private patch: number

  constructor(major: number, minor: number, patch: number) {
    this.major = major
    this.minor = minor
    this.patch = patch
  }

  public toString(): string {
    return this.major + '.' + this.minor + '.' + this.patch
  }

  public compareTo(other: Version): number {
    if (this.major !== other.major) {
      return this.major - other.major
    }
    if (this.minor !== other.minor) {
      return this.minor - other.minor
    }
    return this.patch - other.patch
  }

  public isDevVersion(): boolean {
    return this.major === 0 && this.minor === 0 && this.patch === 0
  }

  public isInvalid(): boolean {
    return this.major < 0 || this.minor < 0 || this.patch < 0
  }

  public static fromJsonField(versionField: Record<string, number> | undefined): Version {
    if (versionField) {
      return new Version(versionField.major, versionField.minor, versionField.patch)
    }
    return new Version(-1, -1, -1)
  }
}

export const reportViewerVersion = Version.fromJsonField(versionJson['report_viewer_version'])
export const minimalReportVersion = Version.fromJsonField(versionJson['minimal_report_version'])
