import { reportStore } from '@/stores/reportStore'
import { ReportFileHandler } from '@jplag/parser'

export const REPORT_FILE_NAME = 'results.jplag'

export async function loadReport(): Promise<void> {
  let reportFile: Blob | null = null
  let reportName: string = REPORT_FILE_NAME
  if (await useLocalReportFileMode()) {
    reportFile = await getLocalFile(REPORT_FILE_NAME)
  } else if (import.meta.env.MODE == 'demo' || import.meta.env.MODE == 'dev-demo') {
    reportFile = await getLocalFile('example.jplag')
    reportName = 'progpedia.jplag'
  } else if (getQueryFileUrl() !== null) {
    const queryURL = new URL(getQueryFileUrl()!)
    const response = await fetch(queryURL)
    reportFile = await response.blob()

    const urlParts = queryURL.pathname.split('/')
    const lastUrlPart = urlParts[urlParts.length - 1]
    if (lastUrlPart?.endsWith('.jplag')) {
      reportName = lastUrlPart
    }
  }
  if (!reportFile) {
    throw new Error(`No report file found. Please provide a valid report file.`)
  }

  const report = await new ReportFileHandler().extractContent(reportFile)
  reportStore().loadReport(report.files, report.submissionFiles, reportName)
}

async function getLocalFile(path: string): Promise<Blob | null> {
  const request = await fetch(`${window.location.origin}${import.meta.env.BASE_URL}${path}`)
  if (request.status == 200) {
    const blob = await request.blob()
    // Check that file is not the index.html
    if (blob.type.includes('text/html')) {
      throw new Error(`Could not find ${path} in local files.`)
    }
    return blob
  } else {
    throw new Error(`Could not find ${path} in local files.`)
  }
}

async function useLocalReportFileMode() {
  try {
    await getLocalFile(REPORT_FILE_NAME)
    return true
    /* eslint-disable @typescript-eslint/no-unused-vars */
  } catch (e) {
    return false
  }
  /* eslint-enable @typescript-eslint/no-unused-vars */
}

function getQueryFileUrl() {
  const urlParameters = new URLSearchParams(document.location.search)
  return urlParameters.get('file')
}

/**
 * Checks whether the loadReport function is able to load a report
 */
export async function canLoadFile() {
  return (await useLocalReportFileMode()) || getQueryFileUrl() !== null
}
