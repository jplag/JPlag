import { Comparison } from '../Comparison'
import type { Match } from '../Match'
import type { SubmissionFile } from '../SubmissionFile'
import type { MatchInSingleFile } from '../MatchInSingleFile'
import store from '@/stores/store'
import { generateColorsForInterval } from '@/utils/ColorUtils'
import slash from 'slash'

/**
 * Factory class for creating Comparison objects
 */
export class ComparisonFactory {
  public static getComparison(id1: string, id2: string) {
    console.log('Generating comparison {%s} - {%s}...', id1, id2)
    let comparison = new Comparison('', '', 0)

    //getting the comparison file based on the used mode (zip, local, single)
    if (store().state.local) {
      const request = new XMLHttpRequest()
      request.open('GET', `/files/${store().getComparisonFileName(id1, id2)}`, false)
      request.send()

      if (request.status == 200) {
        ComparisonFactory.loadSubmissionFilesFromLocal(id1)
        ComparisonFactory.loadSubmissionFilesFromLocal(id2)
        try {
          comparison = ComparisonFactory.extractComparison(JSON.parse(request.response))
        } catch (e) {
          throw new Error('Comparison file not found')
        }
      } else {
        throw new Error('Comparison file not found')
      }
    } else if (store().state.zip) {
      const comparisonFile = store().getComparisonFileForSubmissions(id1, id2)
      if (comparisonFile) {
        comparison = ComparisonFactory.extractComparison(JSON.parse(comparisonFile))
      } else {
        throw new Error('Comparison file not found')
      }
    } else if (store().state.single) {
      try {
        comparison = ComparisonFactory.extractComparison(JSON.parse(store().state.fileString))
      } catch (e) {
        store().clearStore()
        throw new Error('No Comparison files given!')
      }
    }
    return comparison
  }

  /**
   * Creates a comparison object from a json object created by by JPlag
   * @param json the json object
   */
  private static extractComparison(json: Record<string, unknown>): Comparison {
    const filesOfFirstSubmission = store().filesOfSubmission(json.id1 as string)
    const filesOfSecondSubmission = store().filesOfSubmission(json.id2 as string)

    const filesOfFirstConverted = this.convertToFilesByName(filesOfFirstSubmission)
    const filesOfSecondConverted = this.convertToFilesByName(filesOfSecondSubmission)

    const matches = json.matches as Array<Record<string, unknown>>

    const colors = this.generateColorsForMatches(matches.length)
    const coloredMatches = matches.map((match, index) => this.mapMatch(match, colors[index]))

    const matchesInFirst = this.groupMatchesByFileName(coloredMatches, 1)
    const matchesInSecond = this.groupMatchesByFileName(coloredMatches, 2)

    const comparison = new Comparison(
      json.id1 as string,
      json.id2 as string,
      json.similarity as number
    )
    comparison.filesOfFirstSubmission = filesOfFirstConverted
    comparison.filesOfSecondSubmission = filesOfSecondConverted
    comparison.colors = colors
    comparison.allMatches = coloredMatches
    comparison.matchesInFirstSubmission = matchesInFirst
    comparison.matchesInSecondSubmissions = matchesInSecond

    return comparison
  }

  private static convertToFilesByName(
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

  private static groupMatchesByFileName(
    matches: Array<Match>,
    index: number
  ): Map<string, Array<MatchInSingleFile>> {
    const acc = new Map<string, Array<MatchInSingleFile>>()
    matches.forEach((val) => {
      const name = index === 1 ? (val.firstFile as string) : (val.secondFile as string)

      if (!acc.get(name)) {
        acc.set(name, [])
      }

      if (index === 1) {
        const newVal: MatchInSingleFile = {
          start: val.startInFirst as number,
          end: val.endInFirst as number,
          linked_panel: 2,
          linked_file: val.secondFile as string,
          linked_line: val.startInSecond as number,
          color: val.color as string
        }
        acc.get(name)?.push(newVal)
      } else {
        const newVal: MatchInSingleFile = {
          start: val.startInSecond as number,
          end: val.endInSecond as number,
          linked_panel: 1,
          linked_file: val.firstFile as string,
          linked_line: val.startInFirst as number,
          color: val.color as string
        }
        acc.get(name)?.push(newVal)
      }
    })
    return acc
  }

  private static getSubmissionFileListFromLocal(submissionId: string): string[] {
    const request = new XMLHttpRequest()
    request.open('GET', `/files/submissionFileIndex.json`, false)
    request.send()
    if (request.status == 200) {
      return JSON.parse(request.response).submission_file_indexes[submissionId].map(
        (file: string) => slash(file)
      )
    } else {
      return []
    }
  }

  private static loadSubmissionFilesFromLocal(submissionId: string) {
    const request = new XMLHttpRequest()
    const fileList = ComparisonFactory.getSubmissionFileListFromLocal(submissionId)
    for (const file of fileList) {
      request.open('GET', `/files/files/${file}`, false)
      request.overrideMimeType('text/plain')
      request.send()
      if (request.status == 200) {
        store().saveSubmissionFile({
          name: submissionId,
          file: {
            fileName: slash(file),
            data: request.response
          }
        })
      } else {
        console.log('Error loading file: ' + file)
      }
    }
  }

  private static generateColorsForMatches(num: number): Array<string> {
    const numberOfColorsInFirstInterval = Math.round(((80 - 20) / (80 - 20 + (340 - 160))) * num) // number of colors from the first interval
    const numberOfColorsInSecondInterval = num - numberOfColorsInFirstInterval // number of colors from the second interval

    const colors: Array<string> = generateColorsForInterval(
      20,
      80,
      numberOfColorsInFirstInterval,
      0.8,
      0.5,
      0.3
    )
    colors.push(
      ...generateColorsForInterval(160, 340, numberOfColorsInSecondInterval, 0.8, 0.5, 0.3)
    )
    return colors
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
