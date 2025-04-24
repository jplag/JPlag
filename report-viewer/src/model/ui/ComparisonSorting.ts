import { faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons'
import type { FontAwesomeIconProps } from '@fortawesome/vue-fontawesome'
import { MetricType } from '@/model/MetricType'
import type { ComparisonListElement } from '../ComparisonListElement'

interface Direction {
  next: Direction
  icon: FontAwesomeIconProps
  comparator: (a: number, b: number) => number
}

export type ColumnId = 'averageSimilarity' | 'maximumSimilarity' | 'cluster'

interface Column {
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
  export const averageSimilarity: Column = {
    id: 'averageSimilarity',
    value: (c: ComparisonListElement) => [
      c.similarities[MetricType.AVERAGE],
      c.similarities[MetricType.MAXIMUM]
    ]
  }
  export const maximumSimilarity: Column = {
    id: 'maximumSimilarity',
    value: (c: ComparisonListElement) => [
      c.similarities[MetricType.MAXIMUM],
      c.similarities[MetricType.AVERAGE]
    ]
  }
  export const cluster: Column = {
    id: 'cluster',
    value: (c: ComparisonListElement) => {
      if (c.cluster) {
        return [
          c.cluster.averageSimilarity,
          c.cluster.index,
          c.similarities[MetricType.AVERAGE],
          c.similarities[MetricType.MAXIMUM]
        ]
      }
      return [-1, -1, c.similarities[MetricType.AVERAGE], c.similarities[MetricType.MAXIMUM]]
    }
  }
  export const columns: Record<ColumnId, Column> = {
    averageSimilarity,
    maximumSimilarity,
    cluster
  }
}

export interface ComparisonTableSorting {
  column: Column
  direction: Direction
}
