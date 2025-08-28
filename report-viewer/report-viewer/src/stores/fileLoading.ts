import { reportStore } from '@/stores/reportStore'
import { ReportFileHandler } from '@jplag/parser'

export const REPORT_FILE_NAME = 'results.jplag'

export async function loadReport(): Promise<void> {
  let reportFile: Blob | null = null
  let reportName: string | null = null
  if (await useLocalReportFileMode()) {
    reportFile = await getLocalFile(REPORT_FILE_NAME)
    reportName = REPORT_FILE_NAME
  } else if (import.meta.env.MODE == 'demo' || import.meta.env.MODE == 'dev-demo') {
    reportFile = await getLocalFile('example.jplag')
    reportName = 'progpedia.jplag'
  }
  if (!reportFile) {
    throw new Error(`No report file found. Please provide a valid report file.`)
  }

  const report = await new ReportFileHandler().extractContent(reportFile)
  reportStore().loadReport(report.files, report.submissionFiles, reportName ?? REPORT_FILE_NAME)
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

export async function useLocalReportFileMode() {
  try {
    await getLocalFile(REPORT_FILE_NAME)
    return true
    /* eslint-disable @typescript-eslint/no-unused-vars */
  } catch (e) {
    return false
  }
  /* eslint-enable @typescript-eslint/no-unused-vars */
}
