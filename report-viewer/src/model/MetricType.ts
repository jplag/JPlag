import { MetricJsonIdentifier } from './MetricJsonIdentifier'
import { Column, type ColumnSorting } from './ui/ComparisonSorting'

export abstract class MetricType {
  private readonly _shortName: string
  private readonly _longName: string
  private readonly _tooltip: string
  private readonly _identifier: MetricJsonIdentifier
  private readonly _sorting: ColumnSorting

  constructor(
    shortName: string,
    longName: string,
    tooltip: string,
    identifier: MetricJsonIdentifier,
    sorting: ColumnSorting
  ) {
    this._shortName = shortName
    this._longName = longName
    this._tooltip = tooltip
    this._identifier = identifier
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
    'Average',
    'The average similarity of the two files.\nA high similarity indicates that the programs work in a similar way.',
    MetricJsonIdentifier.AVERAGE_SIMILARITY,
    Column.averageSimilarity
  )
  export const MAXIMUM_SIMILARITY = new PercentageMetricType(
    'MAX',
    'Maximum',
    'The maximum similarity of the two files.\nUseful if programs are very different in size.',
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    Column.maximumSimilarity
  )
  export const MINIMUM_SIMILARITY = new PercentageMetricType(
    'MIN',
    'Minimum',
    'The minimum similarity of the two files.',
    MetricJsonIdentifier.MINIMUM_SIMILARITY,
    Column.minimumSimilarity
  )
  export const INTERSECTION = new IdentityMetricType(
    'COUNT',
    'Matched Tokens',
    'The number of tokens that are matched between the two files.',
    MetricJsonIdentifier.INTERSECTION,
    Column.intersection
  )
  export const LONGEST_MATCH = new IdentityMetricType(
    'LONG',
    'Longest Match',
    'The longest matching fragment between two programs measured in tokens.',
    MetricJsonIdentifier.LONGEST_MATCH,
    Column.longestMatch
  )
  export const OVERALL = new IdentityMetricType(
    'LEN',
    'Overall Length',
    'Sum of both submission lengths.',
    MetricJsonIdentifier.OVERALL,
    Column.overall
  )

  export const METRIC_LIST: MetricType[] = [
    AVERAGE_SIMILARITY,
    MAXIMUM_SIMILARITY,
    MINIMUM_SIMILARITY,
    INTERSECTION,
    LONGEST_MATCH,
    OVERALL
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
    MetricJsonIdentifier.MINIMUM_SIMILARITY,
    MetricJsonIdentifier.INTERSECTION,
    MetricJsonIdentifier.LONGEST_MATCH,
    MetricJsonIdentifier.OVERALL
  ]
}
