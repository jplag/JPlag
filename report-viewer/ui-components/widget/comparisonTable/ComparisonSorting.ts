import { faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons'
import type { FontAwesomeIconProps } from '@fortawesome/vue-fontawesome'
import { MetricJsonIdentifier, type ComparisonListElement } from '@jplag/model'

interface Direction {
  next: Direction
  icon: FontAwesomeIconProps
  comparator: (a: number, b: number) => number
}

export type ColumnId =
  | 'averageSimilarity'
  | 'maximumSimilarity'
  | 'cluster'
  | 'longestMatch'
  | 'maximumLength'

export interface ColumnSorting {
  id: ColumnId
  /**
   * Compile a list of values to sort by
   * The first value is the one to sort by, the next ones are used to break ties in order of priority
   * @param c the comparison list element to get the value from
   * @returns List of values to take into account for sorting
   */
  value: (comp: ComparisonListElement) => number[]
}
// eslint-disable-next-line @typescript-eslint/no-namespace
export namespace Direction {
  export const ascending: Direction = {
    next: undefined as unknown as Direction, // to be set later
    icon: faSortUp,
    comparator: (a: number, b: number) => a - b
  }
  export const descending: Direction = {
    next: ascending,
    icon: faSortDown,
    comparator: (a: number, b: number) => b - a
  }
  ascending.next = descending
}

// eslint-disable-next-line @typescript-eslint/no-namespace
export namespace Column {
  export const averageSimilarity: ColumnSorting = {
    id: 'averageSimilarity',
    value: (c: ComparisonListElement) =>
      buildComparisonValues(c, MetricJsonIdentifier.AVERAGE_SIMILARITY)
  }
  export const maximumSimilarity: ColumnSorting = {
    id: 'maximumSimilarity',
    value: (c: ComparisonListElement) =>
      buildComparisonValues(c, MetricJsonIdentifier.MAXIMUM_SIMILARITY)
  }
  export const longestMatch: ColumnSorting = {
    id: 'longestMatch',
    value: (c: ComparisonListElement) =>
      buildComparisonValues(c, MetricJsonIdentifier.LONGEST_MATCH)
  }
  export const maximumLength: ColumnSorting = {
    id: 'maximumLength',
    value: (c: ComparisonListElement) =>
      buildComparisonValues(c, MetricJsonIdentifier.MAXIMUM_LENGTH)
  }

  export const cluster: ColumnSorting = {
    id: 'cluster',
    value: (c: ComparisonListElement) => {
      if (c.cluster) {
        return [
          c.cluster.averageSimilarity,
          c.cluster.index,
          ...buildComparisonValues(c, MetricJsonIdentifier.AVERAGE_SIMILARITY)
        ]
      }
      return [-1, -1, ...buildComparisonValues(c, MetricJsonIdentifier.AVERAGE_SIMILARITY)]
    }
  }
  export const columns: Record<ColumnId, ColumnSorting> = {
    averageSimilarity,
    maximumSimilarity,
    longestMatch,
    maximumLength,
    cluster
  }

  const defaultMetricOrder = [
    MetricJsonIdentifier.AVERAGE_SIMILARITY,
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    MetricJsonIdentifier.LONGEST_MATCH,
    MetricJsonIdentifier.MAXIMUM_LENGTH
  ]

  function buildComparisonValues(c: ComparisonListElement, metric: MetricJsonIdentifier): number[] {
    const metrics = getSimilarityOrder(metric)
    return metrics.map((m) => c.similarities[m])
  }

  function getSimilarityOrder(main: MetricJsonIdentifier) {
    const list: MetricJsonIdentifier[] = [main]
    for (const metric of defaultMetricOrder) {
      if (metric !== main) {
        list.push(metric)
      }
    }
    return list
  }

  export function getSortingFromMetric(metric: MetricJsonIdentifier): ColumnSorting {
    switch (metric) {
      case MetricJsonIdentifier.AVERAGE_SIMILARITY:
        return Column.averageSimilarity
      case MetricJsonIdentifier.MAXIMUM_SIMILARITY:
        return Column.maximumSimilarity
      case MetricJsonIdentifier.LONGEST_MATCH:
        return Column.longestMatch
      case MetricJsonIdentifier.MAXIMUM_LENGTH:
        return Column.maximumLength
      default:
        throw new Error(`Unknown metric: ${metric}`)
    }
  }
}

export interface ComparisonTableSorting {
  column: ColumnSorting
  direction: Direction
}
