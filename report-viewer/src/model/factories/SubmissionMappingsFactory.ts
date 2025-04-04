import { store } from '@/stores/store'
import { BaseFactory } from './BaseFactory'

export class SubmissionMappingsFactory extends BaseFactory {
  public static async getSubmissionMappings(): Promise<void> {
    this.extractSubmissionMappings(JSON.parse(await this.getFile('submissionMappings.json')))
  }

  private static extractSubmissionMappings(json: Record<string, string>): void {
    this.saveIdToDisplayNameMap(json)
    this.saveComparisonFilesLookup(json)
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
}
