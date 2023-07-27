import { Comparison } from '../Comparison'
import type { Match } from '../Match'
import type { SubmissionFile } from '../SubmissionFile'
import type { MatchInSingleFile } from '../MatchInSingleFile'
import store from '@/stores/store'
import { generateColors } from '@/utils/ColorUtils'
import slash from 'slash'
import { BaseFactory } from './BaseFactory'
import MetricType from '../MetricType'

/**
 * Factory class for creating Comparison objects
 */
export class ComparisonFactory extends BaseFactory {
  public static getComparison(id1: string, id2: string) {
    const filePath = store().getComparisonFileName(id1, id2)
    if (!filePath) {
      throw new Error('Comparison file not specified')
    }

    return this.extractComparison(JSON.parse(this.getFile(filePath)))
  }

  /**
   * Creates a comparison object from a json object created by by JPlag
   * @param json the json object
   */
  private static extractComparison(json: Record<string, unknown>): Comparison {
    const firstSubmissionId = json.id1 as string
    const secondSubmissionId = json.id2 as string
    if (store().state.localModeUsed) {
      this.loadSubmissionFilesFromLocal(firstSubmissionId)
      this.loadSubmissionFilesFromLocal(json.id2 as string)
    }
    const filesOfFirstSubmission = store().filesOfSubmission(firstSubmissionId)
    const filesOfSecondSubmission = store().filesOfSubmission(secondSubmissionId)

    const filesOfFirstConverted = this.convertToSubmissionFileList(filesOfFirstSubmission)
    const filesOfSecondConverted = this.convertToSubmissionFileList(filesOfSecondSubmission)

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
      filesOfFirstConverted,
      filesOfSecondConverted,
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

  private static convertToSubmissionFileList(
    files: Array<{ name: string; value: string }>
  ): Map<string, SubmissionFile> {
    const map = new Map<string, SubmissionFile>()
    files.forEach((val) => {
      if (!map.get(val.name)) {
        map.set(val.name as string, {
          lines: [],
          collapsed: false
        })
      }
      map.set(val.name as string, {
        lines: val.value.split(/\r?\n/),
        collapsed: false
      })
    })
    return map
  }

  private static getSubmissionFileListFromLocal(submissionId: string): string[] {
    return JSON.parse(this.getLocalFile(`submissionFileIndex.json`)).submission_file_indexes[
      submissionId
    ].map((file: string) => slash(file))
  }

  private static loadSubmissionFilesFromLocal(submissionId: string) {
    try {
      const fileList = this.getSubmissionFileListFromLocal(submissionId)
      for (const filePath of fileList) {
        store().saveSubmissionFile({
          name: submissionId,
          file: {
            fileName: slash(filePath),
            data: this.getLocalFile(`files/${filePath}`)
          }
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
