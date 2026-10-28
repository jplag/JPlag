import { CliClusterOptions, CliMergingOptions, ParserLanguage } from "@jplag/model"

export interface StartState {
  clickedButton: boolean
}

export interface SubmissionsState {
  newSubmissions: string[]
  oldSubmissions: string[]
  basecode?: string
}

export interface LanguageState {
  selectedLanguages: ParserLanguage[]
}

export interface ComparsionOptionsState {
  clustering: CliClusterOptions
  merging: CliMergingOptions
}

export interface ResultFileOptionsState {
  resultFileName: string
  shownComparisons: number
  similarityThreshold: number
  overwrite: boolean
  csvFile: boolean
}

interface Step {
  name: string
  description: string
}

export const STEPS: Step[] = [
  { name: 'Start', description: 'Start a new configuration or open a preexisting one' },
  { name: 'Submissions', description: 'Select the submission folders and basecode' },
  { name: 'Language', description: 'Select the languages and configure them' },
  { name: 'Comparison Options', description: 'Configure comparison options and add resilience to obfucscation' },
  { name: 'Result File Options', description: 'Configure the generated result file' },
  { name: 'Results', description: 'View the results of the comparison process' }
]
