export enum MetricJsonIdentifier {
  AVERAGE_SIMILARITY = 'AVG',
  MAXIMUM_SIMILARITY = 'MAX',
  MINIMUM_SIMILARITY = 'MIN',
  INTERSECTION = 'INTERSECTION',
  LONGEST_MATCH = 'LONGEST_MATCH',
  OVERALL = 'OVERALL'
}

export abstract class MetricType {
  private readonly _shortName: string
  private readonly _longName: string
  private readonly _tooltip: string
  private readonly _identifier: MetricJsonIdentifier

  constructor(
    shortName: string,
    longName: string,
    tooltip: string,
    identifier: MetricJsonIdentifier
  ) {
    this._shortName = shortName
    this._longName = longName
    this._tooltip = tooltip
    this._identifier = identifier
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

  abstract format(value: number): string
}

class IdentityMetricType extends MetricType {
  constructor(
    shortName: string,
    longName: string,
    tooltip: string,
    identifier: MetricJsonIdentifier
  ) {
    super(shortName, longName, tooltip, identifier)
  }

  format(value: number): string {
    return value.toString()
  }
}

class PercentageMetricType extends MetricType {
  constructor(
    shortName: string,
    longName: string,
    tooltip: string,
    identifier: MetricJsonIdentifier
  ) {
    super(shortName, longName, tooltip, identifier)
  }

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
    MetricJsonIdentifier.AVERAGE_SIMILARITY
  )
  export const MAXIMUM_SIMILARITY = new PercentageMetricType(
    'MAX',
    'Maximum',
    'The maximum similarity of the two files.\nUseful if programs are very different in size.',
    MetricJsonIdentifier.MAXIMUM_SIMILARITY
  )
  export const MINIMUM_SIMILARITY = new PercentageMetricType(
    'MIN',
    'Minimum',
    'The minimum similarity of the two files.',
    MetricJsonIdentifier.MINIMUM_SIMILARITY
  )
  export const INTERSECTION = new IdentityMetricType(
    'COUNT',
    'Matched Tokens',
    'The number of tokens that are matched between the two files.',
    MetricJsonIdentifier.INTERSECTION
  )
  export const LONGEST_MATCH = new IdentityMetricType(
    'LONG',
    'Longest Match',
    'The number of tokens in the longest match.',
    MetricJsonIdentifier.LONGEST_MATCH
  )
  export const OVERALL = new IdentityMetricType(
    'LEN',
    'Overall Length',
    'Sum of both submission lengths.',
    MetricJsonIdentifier.OVERALL
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
