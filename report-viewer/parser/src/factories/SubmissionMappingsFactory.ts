export class SubmissionMappingsFactory {
  public static getSubmissionMappings(submissionMappingsFile: string): SubmissionMappings {
    return this.extractSubmissionMappings(JSON.parse(submissionMappingsFile))
  }

  private static extractSubmissionMappings(
    json: ReportFormatSubmissionMappings
  ): SubmissionMappings {
    return {
      idToDisplayNameMap: this.convertIdToDisplayNameMap(json.submissionIds),
      comparisonFilesLookup: this.convertComparisonFilesLookup(
        json.submissionIdsToComparisonFileName
      )
    }
  }

  private static convertIdToDisplayNameMap(json: Record<string, string>): Map<string, string> {
    return new Map<string, string>(Object.entries(json))
  }

  private static convertComparisonFilesLookup(
    json: Record<string, Record<string, string>>
  ): Map<string, Map<string, string>> {
    const entries: Array<Array<string | object>> = Object.entries(json)
    const comparisonMap = new Map<string, Map<string, string>>()
    for (const [key, value] of entries) {
      comparisonMap.set(key as string, new Map(Object.entries(value)))
    }
    return comparisonMap
  }
}

interface SubmissionMappings {
  idToDisplayNameMap: Map<string, string>
  comparisonFilesLookup: Map<string, Map<string, string>>
}

interface ReportFormatSubmissionMappings {
  submissionIds: Record<string, string>
  submissionIdsToComparisonFileName: Record<string, Record<string, string>>
}
