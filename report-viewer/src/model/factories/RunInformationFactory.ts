import { RunInformation } from '../RunInformation'
import { Version } from '../Version'
import { BaseFactory } from './BaseFactory'

export class RunInformationFactory extends BaseFactory {
  public static async getRunInformation(): Promise<RunInformation> {
    return this.extractRunInformation(JSON.parse(await this.getFile('runInformation.json')))
  }

  private static extractRunInformation(json: Record<string, unknown>): RunInformation {
    const versionField = json.jplag_version as Record<string, number>
    const jplagVersion = Version.fromJsonField(versionField)

    return new RunInformation(
      jplagVersion,
      json.failed_submission_names as string[],
      json.date_of_execution as string,
      json.execution_time as number,
      json.total_comparisons as number
    )
  }
}
