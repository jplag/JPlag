import { MetricJsonIdentifier } from './MetricJsonIdentifier'
import { Column, type ColumnSorting } from './ui/ComparisonSorting'

/**
 * Contains information about a metric that JPlag computes.
 */
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

  /**
   * Shorter abbreviated name of the metric.
   */
  get shortName() {
    return this._shortName
  }

  /**
   * Longer name of the metric
   */
  get longName() {
    return this._longName
  }

  /**
   * Tooltip text that is shown when hovering over the metric.
   */
  get tooltip() {
    return this._tooltip
  }

  /**
   * Identifier of the metric as used in the JSON output.
   */
  get identifier() {
    return this._identifier
  }

  /**
   * Sorting information for the metric.
   */
  get sorting() {
    return this._sorting
  }

  /**
   * Formats the value of the metric for display.
   * @param value The value to format.
   * @returns The formatted string representation of the value.
   */
  abstract format(value: number): string
}

/**
 * Metric types where the value is displayed as-is, without any formatting.
 */
class IdentityMetricType extends MetricType {
  format(value: number): string {
    return value.toString()
  }
}

/**
 * Metric types where the value is displayed as a percentage.
 */
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
    Column.averageSimilarity
  )
  export const MAXIMUM_SIMILARITY = new PercentageMetricType(
    'MAX',
    'Maximum Similarity',
    'The maximum similarity of the two files.\nUseful if programs are very different in size.',
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    Column.maximumSimilarity
  )
  export const LONGEST_MATCH = new IdentityMetricType(
    'LONG',
    'Longest Match',
    'The longest matching fragment between two programs measured in tokens.',
    MetricJsonIdentifier.LONGEST_MATCH,
    Column.longestMatch
  )
  export const MAXIMUM_LENGTH = new IdentityMetricType(
    'LEN',
    'Maximum Length',
    'The length of the longer programm in tokens.',
    MetricJsonIdentifier.MAXIMUM_LENGTH,
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
