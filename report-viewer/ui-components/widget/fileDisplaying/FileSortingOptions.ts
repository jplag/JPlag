import type { ToolTipLabel } from '../../base'

export enum FileSortingOptions {
  ALPHABETICAL,
  MATCH_COVERAGE,
  MATCH_COUNT,
  MATCH_SIZE
}

export const fileSortingTooltips: Record<FileSortingOptions, ToolTipLabel> = {
  [FileSortingOptions.ALPHABETICAL]: {
    displayValue: 'Alphabetical',
    tooltip: 'Sort files alphabetically, similar to package structure.'
  },
  [FileSortingOptions.MATCH_COVERAGE]: {
    displayValue: 'Match Coverage',
    tooltip: 'Sort files by the percentage of tokens included in a match.'
  },
  [FileSortingOptions.MATCH_COUNT]: {
    displayValue: 'Match Count',
    tooltip: 'Sort files by the number of matches found.'
  },
  [FileSortingOptions.MATCH_SIZE]: {
    displayValue: 'Match Size',
    tooltip: 'Sort files by match size, with the largest matches at the top.'
  }
}
