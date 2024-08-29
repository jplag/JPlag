import ComparisonTable from '@/components/ComparisonsTable.vue'
import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, vi, expect } from 'vitest'
import { createTestingPinia } from '@pinia/testing'
import { store } from '@/stores/store'
import { MetricType } from '@/model/MetricType.ts'
import { router } from '@/router'

describe('ComparisonTable', async () => {
  it('Test search string filtering', async () => {
    const wrapper = mount(ComparisonTable, {
      props: {
        topComparisons: [],
        clusters: []
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })

    const searchValue = 'JPlag'

    wrapper.find('input').setValue(searchValue)
    await flushPromises()
  })

  it('Test header prop', async () => {
    const headerText = 'Custom Header'

    const wrapper = mount(ComparisonTable, {
      props: {
        topComparisons: [],
        clusters: [],
        header: headerText
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })

    expect(wrapper.text()).toContain(headerText)
  })

  it('Test row highlighting', async () => {
    const testStore = createTestingPinia({ createSpy: vi.fn })
    store().state.submissionIdsToComparisonFileName.set('A', new Map([['B', 'file1']]))
    store().state.submissionIdsToComparisonFileName.set('B', new Map([['A', 'file1']]))
    store().state.submissionIdsToComparisonFileName.set('C', new Map([['D', 'file2']]))
    store().state.submissionIdsToComparisonFileName.set('D', new Map([['C', 'file2']]))
    const wrapper = mount(ComparisonTable, {
      props: {
        topComparisons: [
          {
            sortingPlace: 0,
            id: 1,
            firstSubmissionId: 'A',
            secondSubmissionId: 'B',
            similarities: {
              [MetricType.AVERAGE]: 1,
              [MetricType.MAXIMUM]: 1
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'C',
            secondSubmissionId: 'D',
            similarities: {
              [MetricType.AVERAGE]: 1,
              [MetricType.MAXIMUM]: 1
            },
            clusterIndex: -1
          }
        ],
        clusters: []
      },
      global: {
        plugins: [testStore, router]
      }
    })

    const rows = wrapper.findAll('div.tableRow')
    console.log(wrapper.text())
    expect(rows[0].classes()).not.toContain('!bg-accent')
    expect(rows[1].classes()).not.toContain('!bg-accent')

    wrapper.setProps({
      highlightedRowIds: { firstID: 'A', secondID: 'B' }
    })
    await flushPromises()
    expect(rows[0].classes()).toContain('!bg-accent')
    expect(rows[1].classes()).not.toContain('!bg-accent')

    wrapper.setProps({
      highlightedRowIds: { firstID: 'C', secondID: 'D' }
    })
    await flushPromises()
    expect(rows[0].classes()).not.toContain('!bg-accent')
    expect(rows[1].classes()).toContain('!bg-accent')

    wrapper.setProps({
      highlightedRowIds: undefined
    })
    await flushPromises()
    expect(rows[0].classes()).not.toContain('!bg-accent')
    expect(rows[1].classes()).not.toContain('!bg-accent')
  })
})
