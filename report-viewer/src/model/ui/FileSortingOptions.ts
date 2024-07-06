import type { ToolTipLabel } from './ToolTip'

export enum FileSortingOptions {
  ALPHABETICAL,
  MATCH_COVERAGE,
  MATCH_COUNT,
  MATCH_SIZE
}

export const fileSortingTooltips: Record<FileSortingOptions, ToolTipLabel> = {
  [FileSortingOptions.ALPHABETICAL]: {
    displayValue: 'Alphabetical',
    tooltip: 'Sort files alphabetically. This will result in a package like sorting.'
  },
  [FileSortingOptions.MATCH_COVERAGE]: {
    displayValue: 'Match Coverage',
    tooltip: 'Sort files by the percentage of tokens that are part of a match.'
  },
  [FileSortingOptions.MATCH_COUNT]: {
    displayValue: 'Match Count',
    tooltip: 'Sort files by the number of matches found in the file.'
  },
  [FileSortingOptions.MATCH_SIZE]: {
    displayValue: 'Match Size',
    tooltip: 'Sort files, so that the largest matches are at the top.'
  }
}
