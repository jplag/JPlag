import { RunInformation, type FailedSubmission, Version } from '@jplag/model'

export class RunInformationFactory {
  public static getRunInformation(runInformationFile: string): RunInformation {
    return this.extractRunInformation(JSON.parse(runInformationFile))
  }

  private static extractRunInformation(json: ReportFormatRunInformation): RunInformation {
    const jplagVersion = Version.fromJsonField(json.version)
    return new RunInformation(
      jplagVersion,
      json.failedSubmissions,
      json.dateOfExecution,
      json.executionTime,
      json.totalComparisons
    )
  }
}

interface ReportFormatRunInformation {
  version: ReportFormatVersion
  failedSubmissions: FailedSubmission[]
  dateOfExecution: string
  executionTime: number
  totalComparisons: number
}

interface ReportFormatVersion {
  major: number
  minor: number
  patch: number
}
