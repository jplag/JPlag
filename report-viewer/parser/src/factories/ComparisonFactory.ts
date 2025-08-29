import {
  Comparison,
  getMatchLength,
  type Match,
  type ReportFormatMatch,
  SubmissionFile,
  MetricJsonIdentifier,
  ComparisonSubmissionFile
} from '@jplag/model'
import slash from 'slash'

const MATCH_COLOR_COUNT = 7

/**
 * Factory class for creating Comparison objects
 */
export class ComparisonFactory {
  public static getComparison(
    comparisonFile: string,
    submissionFileIndex: string,
    filesOfFirstSubmission: SubmissionFile[],
    filesOfSecondSubmission: SubmissionFile[]
  ): Comparison {
    return this.extractComparison(
      JSON.parse(comparisonFile),
      JSON.parse(submissionFileIndex),
      filesOfFirstSubmission,
      filesOfSecondSubmission
    )
  }

  /**
   * Creates a comparison object from a json object created by JPlag
   * @param json the json object
   */
  private static extractComparison(
    comparisonJson: ReportFormatComparison,
    submissionFileIndexJson: ReportFormatSubmissionFileIndex,
    _filesOfFirstSubmission: SubmissionFile[],
    _filesOfSecondSubmission: SubmissionFile[]
  ): Comparison {
    const filesOfFirstSubmission = this.loadSubmissionFiles(
      _filesOfFirstSubmission,
      this.transfromIndexFormatting(
        submissionFileIndexJson.fileIndexes[comparisonJson.firstSubmissionId]
      )
    )
    const filesOfSecondSubmission = this.loadSubmissionFiles(
      _filesOfSecondSubmission,
      this.transfromIndexFormatting(
        submissionFileIndexJson.fileIndexes[comparisonJson.secondSubmissionId]
      )
    )

    const matches: Match[] = comparisonJson.matches.map((match) => {
      return {
        ...match,
        firstFileName: slash(match.firstFileName),
        secondFileName: slash(match.secondFileName),
        colorIndex: -1 // Will be set later
      }
    })
    const firstFileMap = new Map<string, ComparisonSubmissionFile>()
    const secondFileMap = new Map<string, ComparisonSubmissionFile>()
    filesOfFirstSubmission.forEach((file) => firstFileMap.set(file.fileName, file))
    filesOfSecondSubmission.forEach((file) => secondFileMap.set(file.fileName, file))
    matches.forEach((match) => {
      const fileOfFirst = firstFileMap.get(match.firstFileName)
      const fileOfSecond = secondFileMap.get(match.secondFileName)

      if (fileOfFirst == undefined || fileOfSecond == undefined) {
        throw new Error(
          `The report viewer expected to find the file ${fileOfFirst == undefined ? match.firstFileName : match.secondFileName} in the submissions, but did not find it.`
        )
      }

      fileOfFirst.matchedTokenCount += match.lengthOfFirst
      fileOfSecond.matchedTokenCount += match.lengthOfSecond
    })

    return new Comparison(
      comparisonJson.firstSubmissionId,
      comparisonJson.secondSubmissionId,
      this.extractSimilarities(comparisonJson.similarities),
      this.getFilesWithDisplayNames(filesOfFirstSubmission),
      this.getFilesWithDisplayNames(filesOfSecondSubmission),
      this.colorMatches(matches),
      comparisonJson.firstSimilarity,
      comparisonJson.secondSimilarity
    )
  }

  private static extractSimilarities(
    json: Record<string, number>
  ): Record<MetricJsonIdentifier, number> {
    const similarities = {} as Record<MetricJsonIdentifier, number>
    for (const [key, value] of Object.entries(json)) {
      similarities[key as MetricJsonIdentifier] = value
    }
    return similarities
  }

  private static transfromIndexFormatting(oldFileIndex: Record<string, ReportSubmissionFile>) {
    const newFileIndex: Record<string, ReportSubmissionFile> = {}
    for (const [filePath, fileList] of Object.entries(oldFileIndex)) {
      newFileIndex[slash(filePath)] = fileList
    }
    return newFileIndex
  }

  private static loadSubmissionFiles(
    files: SubmissionFile[],
    fileIndex: Record<string, ReportSubmissionFile>
  ) {
    const submissionFiles: ComparisonSubmissionFile[] = []
    for (const file of files) {
      submissionFiles.push({
        ...file,
        tokenCount: fileIndex[file.fileName].tokenCount,
        matchedTokenCount: 0,
        displayFileName: file.fileName
      })
    }
    return submissionFiles
  }

  private static colorMatches(matches: Match[]): Match[] {
    let currentColorIndex = 0
    const matchesFirst = Array.from(matches)
      .sort((a, b) => a.startInFirst.line - b.startInFirst.line)
      .sort((a, b) => (a.firstFileName > b.firstFileName ? 1 : -1))
    const matchesSecond = Array.from(matches)
      .sort((a, b) => a.startInSecond.line - b.startInSecond.line)
      .sort((a, b) => (a.secondFileName > b.secondFileName ? 1 : -1))
    const sortedSize = Array.from(matches).sort((a, b) => getMatchLength(b) - getMatchLength(a))

    function isColorAvailable(matchList: Match[], index: number) {
      return (
        (index === 0 || matchList[index - 1].colorIndex !== currentColorIndex) &&
        (index === matchList.length - 1 || matchList[index + 1].colorIndex !== currentColorIndex)
      )
    }

    for (const matchToColor of matches) {
      const firstIndex = matchesFirst.findIndex((match) => match === matchToColor)
      const secondIndex = matchesSecond.findIndex((match) => match === matchToColor)
      const sortedIndex = sortedSize.findIndex((match) => match === matchToColor)
      const startCounter = currentColorIndex
      while (
        !isColorAvailable(matchesFirst, firstIndex) ||
        !isColorAvailable(matchesSecond, secondIndex) ||
        !isColorAvailable(sortedSize, sortedIndex)
      ) {
        currentColorIndex = (currentColorIndex + 1) % MATCH_COLOR_COUNT

        if (currentColorIndex == startCounter) {
          // This case should never happen, this is just a safety measure
          throw currentColorIndex
        }
      }
      matchToColor.colorIndex = currentColorIndex
      currentColorIndex = (currentColorIndex + 1) % MATCH_COLOR_COUNT
    }
    return sortedSize
  }

  private static getFilesWithDisplayNames(
    files: ComparisonSubmissionFile[]
  ): ComparisonSubmissionFile[] {
    if (files.length == 1) {
      return files
    }
    let longestPrefix = files[0].fileName
    for (let i = 1; i < files.length; i++) {
      if (longestPrefix == '') {
        break
      }

      while (!files[i].fileName.startsWith(longestPrefix)) {
        longestPrefix = longestPrefix.substring(0, longestPrefix.length - 1)
      }
    }

    return files.map((f) => {
      return {
        ...f,
        displayFileName: f.fileName.substring(longestPrefix.length)
      }
    })
  }
}

interface ReportFormatComparison {
  firstSubmissionId: string
  secondSubmissionId: string
  similarities: Record<string, number>
  matches: ReportFormatMatch[]
  firstSimilarity: number
  secondSimilarity: number
}

interface ReportFormatSubmissionFileIndex {
  fileIndexes: Record<string, Record<string, ReportSubmissionFile>>
}

interface ReportSubmissionFile {
  tokenCount: number
}
