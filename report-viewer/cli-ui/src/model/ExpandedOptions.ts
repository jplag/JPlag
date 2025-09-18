import { CliOptions, ParserLanguage } from '@jplag/model'

export type MinimumTokenMatch = number | 'default'

interface _ExpandedOptions {
  resultFileName: string
  minimumTokenMatch: MinimumTokenMatch
  language: ParserLanguage
  overwriteResultFile: boolean
  generateCsvFile: boolean
}

export type ExpandedOptions = Omit<CliOptions, 'minimumTokenMatch' | 'language'> & _ExpandedOptions
