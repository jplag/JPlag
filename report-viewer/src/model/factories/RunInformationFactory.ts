import { RunInformation } from '../RunInformation'
import { Version } from '../Version'
import { BaseFactory } from './BaseFactory'

export class RunInformationFactory extends BaseFactory {
  public static async getRunInformation(): Promise<RunInformation> {
    return this.extractRunInformation(JSON.parse(await this.getFile('runInformation.json')))
  }

  private static extractRunInformation(json: ReportFormatRunInformation): RunInformation {
    const jplagVersion = Version.fromJsonField(json.version)

    return new RunInformation(
      jplagVersion,
      json.failedSubmissionNames,
      json.dateOfExecution,
      json.executionTime,
      json.totalComparisons
    )
  }
}

interface ReportFormatRunInformation {
  version: ReportFormatVersion
  failedSubmissionNames: string[]
  dateOfExecution: string
  executionTime: number
  totalComparisons: number
}

interface ReportFormatVersion {
  major: number
  minor: number
  patch: number
}
