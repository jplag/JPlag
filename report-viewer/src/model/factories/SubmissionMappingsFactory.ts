import { store } from '@/stores/store'
import { BaseFactory } from './BaseFactory'

export class SubmissionMappingsFactory extends BaseFactory {
  public static async getSubmissionMappings(): Promise<void> {
    this.extractSubmissionMappings(JSON.parse(await this.getFile('submissionMappings.json')))
  }

  private static extractSubmissionMappings(json: ReportFormatSubmissionMappings): void {
    this.saveIdToDisplayNameMap(json.submissionIds)
    this.saveComparisonFilesLookup(json.submissionIdsToComparisonFileName)
  }

  private static saveIdToDisplayNameMap(json: SubmissionIdToDisplayName) {
    const map = new Map<string, string>(Object.entries(json))

    store().saveSubmissionNames(map)
  }

  private static saveComparisonFilesLookup(json: SubmissionIdsToComparisonFileName) {
    const entries: Array<Array<string | object>> = Object.entries(json)
    const comparisonMap = new Map<string, Map<string, string>>()
    for (const [key, value] of entries) {
      comparisonMap.set(key as string, new Map(Object.entries(value)))
    }

    store().saveComparisonFileLookup(comparisonMap)
  }
}

interface ReportFormatSubmissionMappings {
  submissionIds: SubmissionIdToDisplayName
  submissionIdsToComparisonFileName: SubmissionIdsToComparisonFileName
}

type SubmissionIdToDisplayName = Record<string, string>
type SubmissionIdsToComparisonFileName = Record<string, Record<string, string>>
