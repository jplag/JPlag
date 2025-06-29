import type { FontAwesomeIconProps } from '@fortawesome/vue-fontawesome'
import { MetricJsonIdentifier } from './MetricJsonIdentifier'
import { Column, type ColumnSorting } from './ui/ComparisonSorting'
import {
  faArrowUpRightDots,
  faFileLines,
  faRulerHorizontal
} from '@fortawesome/free-solid-svg-icons'

export type MetricIcon =
  | { type: 'FontAwesome'; icon: FontAwesomeIconProps }
  | { type: 'manual'; name: string }

export abstract class MetricType {
  private readonly _shortName: string
  private readonly _longName: string
  private readonly _tooltip: string
  private readonly _identifier: MetricJsonIdentifier
  private readonly _icon: MetricIcon
  private readonly _sorting: ColumnSorting

  constructor(
    shortName: string,
    longName: string,
    tooltip: string,
    identifier: MetricJsonIdentifier,
    icon: MetricIcon,
    sorting: ColumnSorting
  ) {
    this._shortName = shortName
    this._longName = longName
    this._tooltip = tooltip
    this._identifier = identifier
    this._icon = icon
    this._sorting = sorting
  }

  get shortName() {
    return this._shortName
  }

  get longName() {
    return this._longName
  }

  get tooltip() {
    return this._tooltip
  }

  get identifier() {
    return this._identifier
  }

  get icon() {
    return this._icon
  }

  get sorting() {
    return this._sorting
  }

  abstract format(value: number): string
}

class IdentityMetricType extends MetricType {
  format(value: number): string {
    return value.toString()
  }
}

class PercentageMetricType extends MetricType {
  format(value: number): string {
    return `${(value * 100).toFixed(2)}%`
  }
}

// eslint-disable-next-line @typescript-eslint/no-namespace
export namespace MetricTypes {
  export const AVERAGE_SIMILARITY = new PercentageMetricType(
    'AVG',
    'Average Similarity',
    'The average similarity of the two files.\nA high similarity indicates that the programs work in a similar way.',
    MetricJsonIdentifier.AVERAGE_SIMILARITY,
    { type: 'manual', name: 'average' },
    Column.averageSimilarity
  )
  export const MAXIMUM_SIMILARITY = new PercentageMetricType(
    'MAX',
    'Maximum Similarity',
    'The maximum similarity of the two files.\nUseful if programs are very different in size.',
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    { type: 'FontAwesome', icon: faArrowUpRightDots },
    Column.maximumSimilarity
  )
  export const LONGEST_MATCH = new IdentityMetricType(
    'LONG',
    'Longest Match',
    'The longest matching fragment between two programs measured in tokens.',
    MetricJsonIdentifier.LONGEST_MATCH,
    { type: 'FontAwesome', icon: faRulerHorizontal },
    Column.longestMatch
  )
  export const MAXIMUM_LENGTH = new IdentityMetricType(
    'LEN',
    'Maximum Length',
    'The length of the longer programm in tokens.',
    MetricJsonIdentifier.MAXIMUM_LENGTH,
    { type: 'FontAwesome', icon: faFileLines },
    Column.maximumLength
  )

  export const METRIC_LIST: MetricType[] = [
    AVERAGE_SIMILARITY,
    MAXIMUM_SIMILARITY,
    LONGEST_MATCH,
    MAXIMUM_LENGTH
  ]

  export const METRIC_MAP: Record<MetricJsonIdentifier, MetricType> = {} as Record<
    MetricJsonIdentifier,
    MetricType
  >
  for (const metric of METRIC_LIST) {
    METRIC_MAP[metric.identifier] = metric
  }
  export const METRIC_JSON_IDENTIFIERS = [
    MetricJsonIdentifier.AVERAGE_SIMILARITY,
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    MetricJsonIdentifier.LONGEST_MATCH,
    MetricJsonIdentifier.MAXIMUM_LENGTH
  ]
}
