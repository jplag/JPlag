/**
 * Version of the report viewer.
 */
export class Version {
  private readonly major: number
  private readonly minor: number
  private readonly patch: number

  public static readonly ERROR_VERSION = new Version(-1, -1, -1)

  constructor(major: number, minor: number, patch: number) {
    this.major = major
    this.minor = minor
    this.patch = patch
  }

  public toString(): string {
    if (this.isDevVersion()) {
      return 'dev'
    }
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

  public static fromJsonField(
    versionField:
      | Record<string, number>
      | { major: number; minor: number; patch: number }
      | undefined
  ): Version {
    if (versionField) {
      return new Version(versionField.major, versionField.minor, versionField.patch)
    }
    return Version.ERROR_VERSION
  }
}
