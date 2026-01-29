import { ParserLanguage, SubmissionState } from '@jplag/model'

export interface LoadedSubmission {
  name: string
  languages?: ParserLanguage[]
  linesOfCode?: number
  encoding?: string
  tokenCount?: number
  state?: SubmissionState
}
