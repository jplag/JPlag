import { Comparison } from '../Comparison'
import type { Match } from '../Match'
import { store } from '@/stores/store'
import { getMatchColorCount } from '@/utils/ColorUtils'
import slash from 'slash'
import { BaseFactory } from './BaseFactory'
import { MetricType } from '../MetricType'

/**
 * Factory class for creating Comparison objects
 */
export class ComparisonFactory extends BaseFactory {
  public static async getComparison(id1: string, id2: string): Promise<Comparison> {
    const filePath = store().getComparisonFileName(id1, id2)
    if (!filePath) {
      throw new Error('Comparison file not specified')
    }

    return await this.extractComparison(JSON.parse(await this.getFile(filePath)))
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

    const unColoredMatches = matches.map((match) => this.getMatch(match))

    return new Comparison(
      firstSubmissionId,
      secondSubmissionId,
      this.extractSimilarities(json),
      filesOfFirstSubmission,
      filesOfSecondSubmission,
      this.colorMatches(unColoredMatches),
      json.first_similarity as number | undefined,
      json.second_similarity as number | undefined
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

  private static getMatch(match: Record<string, unknown>): Match {
    return {
      firstFile: slash(match.file1 as string),
      secondFile: slash(match.file2 as string),
      startInFirst: match.start1 as number,
      endInFirst: match.end1 as number,
      startInSecond: match.start2 as number,
      endInSecond: match.end2 as number,
      tokens: match.tokens as number
    }
  }

  private static colorMatches(matches: Match[]): Match[] {
    const maxColorCount = getMatchColorCount()
    let currentColorIndex = 0
    const matchesFirst = Array.from(matches)
      .sort((a, b) => a.startInFirst - b.startInFirst)
      .sort((a, b) => (a.firstFile > b.firstFile ? 1 : -1))
    const matchesSecond = Array.from(matches)
      .sort((a, b) => a.startInSecond - b.startInSecond)
      .sort((a, b) => (a.secondFile > b.secondFile ? 1 : -1))
    const sortedSize = Array.from(matches).sort((a, b) => b.tokens - a.tokens)

    function isColorAvailable(matchList: Match[], index: number) {
      return (
        (index === 0 || matchList[index - 1].colorIndex !== currentColorIndex) &&
        (index === matchList.length - 1 || matchList[index + 1].colorIndex !== currentColorIndex)
      )
    }

    for (let i = 0; i < matches.length; i++) {
      const firstIndex = matchesFirst.findIndex((match) => match === matches[i])
      const secondIndex = matchesSecond.findIndex((match) => match === matches[i])
      const sortedIndex = sortedSize.findIndex((match) => match === matches[i])
      const startCounter = currentColorIndex
      while (
        !isColorAvailable(matchesFirst, firstIndex) ||
        !isColorAvailable(matchesSecond, secondIndex) ||
        !isColorAvailable(sortedSize, sortedIndex)
      ) {
        currentColorIndex = (currentColorIndex + 1) % maxColorCount

        if (currentColorIndex == startCounter) {
          // This case should never happen, this is just a safety measure
          throw currentColorIndex
        }
      }
      matches[i].colorIndex = currentColorIndex
      currentColorIndex = (currentColorIndex + 1) % maxColorCount
    }
    return sortedSize
  }
}
