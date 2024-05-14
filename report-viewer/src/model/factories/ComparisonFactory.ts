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
    await this.getFile(`submissionFileIndex.json`)
      .then(async () => {
        await this.loadSubmissionFiles(firstSubmissionId)
        await this.loadSubmissionFiles(secondSubmissionId)
      })
      .catch(() => {})
    const filesOfFirstSubmission = store().filesOfSubmission(firstSubmissionId)
    const filesOfSecondSubmission = store().filesOfSubmission(secondSubmissionId)

    const matches = json.matches as Array<Record<string, unknown>>
    matches.forEach((match) => {
      const fileOfFirst = store().getSubmissionFile(firstSubmissionId, slash(match.file1 as string))
      const fileOfSecond = store().getSubmissionFile(
        secondSubmissionId,
        slash(match.file2 as string)
      )

      if (fileOfFirst == undefined || fileOfSecond == undefined) {
        throw new Error(
          `The report viewer expected to find the file ${fileOfFirst == undefined ? match.file1 : match.file2} in the submissions, but did not find it.`
        )
      }

      fileOfFirst.matchedTokenCount += match.tokens as number
      fileOfSecond.matchedTokenCount += match.tokens as number
    })

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

  private static async getSubmissionFileList(
    submissionId: string
  ): Promise<Record<string, { token_count: number }>> {
    return JSON.parse(await this.getFile(`submissionFileIndex.json`)).submission_file_indexes[
      submissionId
    ]
  }

  private static async loadSubmissionFiles(submissionId: string) {
    try {
      const fileList = await this.getSubmissionFileList(submissionId)
      const fileNames = Object.keys(fileList)
      for (const filePath of fileNames) {
        store().saveSubmissionFile({
          fileName: slash(filePath),
          submissionId: submissionId,
          data: await this.getSubmissionFileContent(submissionId, slash(filePath)),
          tokenCount: fileList[filePath].token_count,
          matchedTokenCount: 0
        })
      }
    } catch (e) {
      console.log(e)
    }
  }

  private static async getSubmissionFileContent(submissionId: string, fileName: string) {
    if (store().state.localModeUsed && !store().state.zipModeUsed) {
      return await this.getLocalFile('files/' + fileName).then((file) => file.text())
    }
    const file = store().getSubmissionFile(submissionId, fileName)
    if (file == undefined) {
      throw new Error(
        `The report viewer expected to find the file ${fileName} in the submissions, but did not find it.`
      )
    }
    return file.data
  }

  private static getMatch(match: Record<string, unknown>): Match {
    return {
      firstFile: slash(match.file1 as string),
      secondFile: slash(match.file2 as string),
      startInFirst: {
        line: match.start1 as number,
        column: ((match['start1_col'] as number) || 1) - 1,
        tokenListIndex: (match.startToken1 as number) ?? NaN
      },
      endInFirst: {
        line: match.end1 as number,
        column: ((match['end1_col'] as number) || Infinity) - 1,
        tokenListIndex: (match.endToken1 as number) ?? NaN
      },
      startInSecond: {
        line: match.start2 as number,
        column: ((match['start2_col'] as number) || 1) - 1,
        tokenListIndex: (match.startToken2 as number) ?? NaN
      },
      endInSecond: {
        line: match.end2 as number,
        column: ((match['end2_col'] as number) || Infinity) - 1,
        tokenListIndex: (match.endToken2 as number) ?? NaN
      },
      tokens: match.tokens as number
    }
  }

  private static colorMatches(matches: Match[]): Match[] {
    const maxColorCount = getMatchColorCount()
    let currentColorIndex = 0
    const matchesFirst = Array.from(matches)
      .sort((a, b) => a.startInFirst.line - b.startInFirst.line)
      .sort((a, b) => (a.firstFile > b.firstFile ? 1 : -1))
    const matchesSecond = Array.from(matches)
      .sort((a, b) => a.startInSecond.line - b.startInSecond.line)
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
