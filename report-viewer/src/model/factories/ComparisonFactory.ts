import { Comparison } from '../Comparison'
import type { Match } from '../Match'
import { store } from '@/stores/store'
import { generateColors } from '@/utils/ColorUtils'
import slash from 'slash'
import { BaseFactory } from './BaseFactory'
import { MetricType } from '../MetricType'

/**
 * Factory class for creating Comparison objects
 */
export class ComparisonFactory extends BaseFactory {
  public static async getComparison(fileName: string): Promise<Comparison> {
    return await this.extractComparison(JSON.parse(await this.getFile(fileName)))
  }

  /**
   * Creates a comparison object from a json object created by by JPlag
   * @param json the json object
   */
  private static async extractComparison(json: Record<string, unknown>): Promise<Comparison> {
    const firstSubmissionId = json.id1 as string
    const secondSubmissionId = json.id2 as string
    if (store().state.localModeUsed && !store().state.zipModeUsed) {
      await this.loadSubmissionFilesFromLocal(firstSubmissionId)
      await this.loadSubmissionFilesFromLocal(secondSubmissionId)
    }
    const filesOfFirstSubmission = store().filesOfSubmission(firstSubmissionId)
    const filesOfSecondSubmission = store().filesOfSubmission(secondSubmissionId)

    const matches = json.matches as Array<Record<string, unknown>>

    const matchSaturation = 0.8
    const matchLightness = 0.5
    const matchAlpha = 0.3
    const colors = generateColors(matches.length, matchSaturation, matchLightness, matchAlpha)
    const coloredMatches = matches.map((match, index) => this.mapMatch(match, colors[index]))

    return new Comparison(
      firstSubmissionId,
      secondSubmissionId,
      this.extractSimilarities(json),
      filesOfFirstSubmission,
      filesOfSecondSubmission,
      coloredMatches
    )
  }

  private static extractSimilarities(json: Record<string, unknown>): Record<MetricType, number> {
    if (json.similarities) {
      return this.extractSimilaritiesFromMap(json.similarities as Record<string, number>)
    } else if (json.similarity) {
      return this.extractSimilaritiesFromSingleValue(json.similarity as number)
    }
    throw new Error('No similarities found in comparison file')
  }

  /** @deprecated since 5.0.0. Use the new format with {@link extractSimilaritiesFromMap} */
  private static extractSimilaritiesFromSingleValue(
    avgSimilarity: number
  ): Record<MetricType, number> {
    return {
      [MetricType.AVERAGE]: avgSimilarity,
      [MetricType.MAXIMUM]: Number.NaN
    }
  }

  private static extractSimilaritiesFromMap(
    similarityMap: Record<string, number>
  ): Record<MetricType, number> {
    const similarities = {} as Record<MetricType, number>
    for (const [key, value] of Object.entries(similarityMap)) {
      similarities[key as MetricType] = value
    }
    return similarities
  }

  private static async getSubmissionFileListFromLocal(submissionId: string): Promise<string[]> {
    return JSON.parse(
      await this.getLocalFile(`files/submissionFileIndex.json`).then((file) => file.text())
    ).submission_file_indexes[submissionId].map((file: string) => slash(file))
  }

  private static async loadSubmissionFilesFromLocal(submissionId: string) {
    try {
      const fileList = await this.getSubmissionFileListFromLocal(submissionId)
      for (const filePath of fileList) {
        store().saveSubmissionFile({
          fileName: slash(filePath),
          submissionId: submissionId,
          data: await this.getLocalFile(`files/files/${filePath}`).then((file) => file.text())
        })
      }
    } catch (e) {
      console.log(e)
    }
  }

  private static mapMatch(match: Record<string, unknown>, color: string): Match {
    return {
      firstFile: slash(match.file1 as string),
      secondFile: slash(match.file2 as string),
      startInFirst: match.start1 as number,
      endInFirst: match.end1 as number,
      startInSecond: match.start2 as number,
      endInSecond: match.end2 as number,
      tokens: match.tokens as number,
      color: color
    }
  }
}
