import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import ComparisonTable from '../../../widget/comparisonTable/ComparisonTable.vue'
import { Column, Direction } from '../../../widget/comparisonTable/ComparisonSorting'
import { MetricJsonIdentifier, type ComparisonListElement } from '@jplag/model'

describe('ComparisonTable', () => {
  it('Resets a stale weighted sorting when frequency analysis is off', async () => {
    const comparisons: ComparisonListElement[] = [
      {
        id: 1,
        sortingPlace: 0,
        firstSubmissionId: 'test1',
        secondSubmissionId: 'test2',
        similarities: {
          [MetricJsonIdentifier.AVERAGE_SIMILARITY]: 0.5,
          [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: 0.6,
          [MetricJsonIdentifier.LONGEST_MATCH]: 10,
          [MetricJsonIdentifier.MAXIMUM_LENGTH]: 20
        } as ComparisonListElement['similarities']
      }
    ]

    let sorting = { column: Column.weightedSimilarity, direction: Direction.descending }
    mount(ComparisonTable, {
      props: {
        topComparisons: comparisons,
        sorting,
        'onUpdate:sorting': (s) => (sorting = s),
        secondaryMetrics: new Set([
          MetricJsonIdentifier.MAXIMUM_SIMILARITY,
          MetricJsonIdentifier.LONGEST_MATCH,
          MetricJsonIdentifier.MAXIMUM_LENGTH
        ])
      }
    })
    await flushPromises()

    expect(sorting.column.id).toBe('averageSimilarity')
  })

  it('Keeps a weighted sorting when frequency analysis is on', async () => {
    let sorting = { column: Column.weightedSimilarity, direction: Direction.descending }
    mount(ComparisonTable, {
      props: {
        topComparisons: [],
        sorting,
        'onUpdate:sorting': (s) => (sorting = s),
        secondaryMetrics: new Set([
          MetricJsonIdentifier.MAXIMUM_SIMILARITY,
          MetricJsonIdentifier.LONGEST_MATCH,
          MetricJsonIdentifier.MAXIMUM_LENGTH,
          MetricJsonIdentifier.WEIGHTED_SIMILARITY
        ])
      }
    })
    await flushPromises()

    expect(sorting.column.id).toBe('weightedSimilarity')
  })
})
