import { File, SubmissionFile } from '@jplag/model'
import jszip from 'jszip'
import slash from 'slash'

/**
 * Class for handling report files.
 */
export class ReportFileHandler {
  public async extractContent(file: Blob) {
    const files: File[] = []
    const submissionFiles: SubmissionFile[] = []
    console.info('Start handling report file and storing necessary data...')
    await jszip.loadAsync(file).then(async (zip) => {
      for (const originalFileName of Object.keys(zip.files)) {
        const unixFileName = slash(originalFileName)
        if (unixFileName.endsWith('/')) {
          continue
        }
        if (/\/?(files)\//.test(unixFileName) && !/\/?__MACOSX\//.test(unixFileName)) {
          const directoryPath = unixFileName.substring(0, unixFileName.lastIndexOf('/'))
          const fileBase = unixFileName.substring(unixFileName.lastIndexOf('/') + 1)

          const submissionFileName = this.extractSubmissionFileName(directoryPath)
          const fullPathFileName = this.extractFileNameWithFullPath(
            directoryPath,
            fileBase,
            originalFileName
          )
          await zip.files[originalFileName].async('string').then((data) => {
            submissionFiles.push({
              submissionId: slash(submissionFileName),
              fileName: slash(fullPathFileName),
              data: data
            })
          })
        } else {
          await zip.files[originalFileName].async('string').then((data) => {
            files.push({ fileName: unixFileName, data: data })
          })
        }
      }
    })
    return { files, submissionFiles }
  }

  /**
   * Extracts the submission file name from the given directory path.
   * @param directoryPath Path to extract the submission file name from.
   */
  private extractSubmissionFileName(directoryPath: string) {
    const folders = directoryPath.split('/')
    const rootName = folders[0]
    let submissionFolderIndex: number
    if (rootName === 'files') {
      submissionFolderIndex = folders.findIndex((folder) => folder === 'files')
    } else {
      submissionFolderIndex = folders.findIndex((folder) => folder === 'submissions')
    }
    return folders[submissionFolderIndex + 1]
  }

  /**
   * Gets the full path of the file
   * @param directoryPath Path to the file
   * @param fileBase Name of the file
   * @param originalFileName Original name of the file
   */
  private extractFileNameWithFullPath(
    directoryPath: string,
    fileBase: string,
    originalFileName: string
  ) {
    let fullPath: string
    const rootName = this.extractRootName(directoryPath)
    const filesOrSubmissionsIndex_filePath = directoryPath.indexOf(
      rootName === 'files' ? 'files' : 'submissions'
    )
    const filesOrSubmissionsIndex_originalFileName = originalFileName.indexOf(
      rootName === 'files' ? 'files' : 'submissions'
    )
    const unixSubfolderPathAfterSubmissions = directoryPath.substring(
      filesOrSubmissionsIndex_filePath +
        (rootName === 'files' ? 'files'.length : 'submissions'.length) +
        1
    )
    const originalPathWithoutSubmissions = originalFileName.substring(
      filesOrSubmissionsIndex_originalFileName +
        (rootName === 'files' ? 'files'.length : 'submissions'.length)
    )
    if (originalPathWithoutSubmissions.startsWith('\\')) {
      fullPath = unixSubfolderPathAfterSubmissions + '\\' + fileBase
      while (fullPath.includes('/')) {
        fullPath = fullPath.replace('/', '\\')
      }
    } else {
      fullPath = unixSubfolderPathAfterSubmissions + '/' + fileBase
    }
    return fullPath
  }

  /**
   * Gets the root name of the given directory path.
   * @param directoryPath Path to extract the root name from.
   */
  private extractRootName(directoryPath: string) {
    const folders = directoryPath.split('/')
    return folders[0]
  }
}
