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
        topComparisons: [
          {
            sortingPlace: 0,
            id: 1,
            firstSubmissionId: 'A',
            secondSubmissionId: 'B',
            similarities: {
              [MetricType.AVERAGE]: 1,
              [MetricType.MAXIMUM]: 0.5
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'C',
            secondSubmissionId: 'D',
            similarities: {
              [MetricType.AVERAGE]: 0.5,
              [MetricType.MAXIMUM]: 1
            },
            clusterIndex: -1
          }
        ],
        clusters: []
      },
      global: {
        plugins: [getStore(), router]
      }
    })

    // check that filtering works with one name
    wrapper.find('input').setValue('A')
    await flushPromises()
    const displayedComparisonsSingleName = wrapper.vm.displayedComparisons
    expect(displayedComparisonsSingleName.length).toBe(1)
    expect(displayedComparisonsSingleName[0].firstSubmissionId).toBe('A')
    expect(displayedComparisonsSingleName[0].secondSubmissionId).toBe('B')

    // check that filtering works with two names
    wrapper.find('input').setValue('A D')
    await flushPromises()
    const displayedComparisonsTwoNames = wrapper.vm.displayedComparisons
    expect(displayedComparisonsTwoNames.length).toBe(2)
  })

  it('Test search bar filtering by index', async () => {
    const wrapper = mount(ComparisonTable, {
      props: {
        topComparisons: [
          {
            sortingPlace: 0,
            id: 1,
            firstSubmissionId: 'A',
            secondSubmissionId: 'B',
            similarities: {
              [MetricType.AVERAGE]: 0.3,
              [MetricType.MAXIMUM]: 0.5
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'C',
            secondSubmissionId: 'D',
            similarities: {
              [MetricType.AVERAGE]: 0.5,
              [MetricType.MAXIMUM]: 1
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'E',
            secondSubmissionId: 'F',
            similarities: {
              [MetricType.AVERAGE]: 0.3,
              [MetricType.MAXIMUM]: 0.1
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'H',
            secondSubmissionId: 'G',
            similarities: {
              [MetricType.AVERAGE]: 0.9,
              [MetricType.MAXIMUM]: 0.2
            },
            clusterIndex: -1
          }
        ],
        clusters: []
      },
      global: {
        plugins: [getStore(), router]
      }
    })

    wrapper.find('input').setValue('2')
    await flushPromises()
    const displayedComparisonsIndex1 = wrapper.vm.displayedComparisons
    expect(displayedComparisonsIndex1.length).toBe(1)
    expect(displayedComparisonsIndex1[0].firstSubmissionId).toBe('C')

    wrapper.find('input').setValue('2 3')
    await flushPromises()
    const displayedComparisonsIndex2 = wrapper.vm.displayedComparisons
    expect(displayedComparisonsIndex2.length).toBe(2)
    expect(displayedComparisonsIndex2[0].firstSubmissionId).toBe('C')
    expect(displayedComparisonsIndex2[1].firstSubmissionId).toBe('A')

    wrapper.find('input').setValue('index:1')
    await flushPromises()
    const displayedComparisonsIndex3 = wrapper.vm.displayedComparisons
    expect(displayedComparisonsIndex3.length).toBe(1)
    expect(displayedComparisonsIndex3[0].firstSubmissionId).toBe('H')

    const metricOptions = wrapper.get('.font-bold').findAll('.cursor-pointer')
    await metricOptions[1].trigger('click')
    wrapper.find('input').setValue('index:2')
    await flushPromises()
    const displayedComparisonsIndex4 = wrapper.vm.displayedComparisons
    expect(displayedComparisonsIndex4.length).toBe(1)
    expect(displayedComparisonsIndex4[0].firstSubmissionId).toBe('A')
  })

  it('Test search bar filtering by metric', async () => {
    const wrapper = mount(ComparisonTable, {
      props: {
        topComparisons: [
          {
            sortingPlace: 0,
            id: 1,
            firstSubmissionId: 'A',
            secondSubmissionId: 'B',
            similarities: {
              [MetricType.AVERAGE]: 0.3,
              [MetricType.MAXIMUM]: 0.5
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'C',
            secondSubmissionId: 'D',
            similarities: {
              [MetricType.AVERAGE]: 0.4,
              [MetricType.MAXIMUM]: 1
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'E',
            secondSubmissionId: 'F',
            similarities: {
              [MetricType.AVERAGE]: 0.3,
              [MetricType.MAXIMUM]: 0.1
            },
            clusterIndex: -1
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'H',
            secondSubmissionId: 'G',
            similarities: {
              [MetricType.AVERAGE]: 0.9,
              [MetricType.MAXIMUM]: 0.2
            },
            clusterIndex: -1
          }
        ],
        clusters: []
      },
      global: {
        plugins: [getStore(), router]
      }
    })

    // check that filtering works over all metrics when no metric is specified
    wrapper.find('input').setValue('>45')
    await flushPromises()
    const displayedComparisonsMetricNoPercentage = wrapper.vm.displayedComparisons
    expect(displayedComparisonsMetricNoPercentage.length).toBe(3)

    // check that filtering works with and without percentage
    wrapper.find('input').setValue('>45%')
    await flushPromises()
    const displayedComparisonsMetricWithPercentage = wrapper.vm.displayedComparisons
    expect(displayedComparisonsMetricWithPercentage.length).toBe(3)
    expect(displayedComparisonsMetricWithPercentage).toEqual(displayedComparisonsMetricNoPercentage)

    // check that filtering works on max metric percentage
    wrapper.find('input').setValue('max:>45')
    await flushPromises()
    expect(wrapper.vm.displayedComparisons.length).toBe(2)

    // check that filtering works on average metric percentage
    wrapper.find('input').setValue('avg:>45')
    await flushPromises()
    expect(wrapper.vm.displayedComparisons.length).toBe(1)

    // check that filtering works correctly on greater, greater or equal, less and less or equal
    wrapper.find('input').setValue('max:>50')
    await flushPromises()
    expect(wrapper.vm.displayedComparisons.length).toBe(1)

    wrapper.find('input').setValue('max:>=50')
    await flushPromises()
    expect(wrapper.vm.displayedComparisons.length).toBe(2)

    wrapper.find('input').setValue('max:<50%')
    await flushPromises()
    expect(wrapper.vm.displayedComparisons.length).toBe(2)

    wrapper.find('input').setValue('max:<=50')
    await flushPromises()
    expect(wrapper.vm.displayedComparisons.length).toBe(3)
  })

  it('Test sorting working', async () => {
    const wrapper = mount(ComparisonTable, {
      props: {
        topComparisons: [
          {
            sortingPlace: 0,
            id: 1,
            firstSubmissionId: 'A',
            secondSubmissionId: 'B',
            similarities: {
              [MetricType.AVERAGE]: 0.3,
              [MetricType.MAXIMUM]: 0.5
            },
            cluster: {
              index: 0,
              averageSimilarity: 0.5,
              strength: 0.5,
              members: ['A', 'B']
            }
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'C',
            secondSubmissionId: 'D',
            similarities: {
              [MetricType.AVERAGE]: 0.5,
              [MetricType.MAXIMUM]: 1
            },
            cluster: {
              index: 1,
              averageSimilarity: 0.6,
              strength: 0.5,
              members: ['C', 'D']
            }
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'E',
            secondSubmissionId: 'F',
            similarities: {
              [MetricType.AVERAGE]: 0.3,
              [MetricType.MAXIMUM]: 0.1
            },
            cluster: {
              index: 2,
              averageSimilarity: 0.9,
              strength: 0.5,
              members: ['E', 'F']
            }
          },
          {
            sortingPlace: 1,
            id: 2,
            firstSubmissionId: 'H',
            secondSubmissionId: 'G',
            similarities: {
              [MetricType.AVERAGE]: 0.9,
              [MetricType.MAXIMUM]: 0.2
            },
            cluster: undefined
          }
        ],
        clusters: [
          {
            index: 0,
            averageSimilarity: 0.5,
            strength: 0.5,
            members: ['A', 'B']
          },
          {
            index: 1,
            averageSimilarity: 0.6,
            strength: 0.5,
            members: ['C', 'D']
          },
          {
            index: 2,
            averageSimilarity: 0.9,
            strength: 0.5,
            members: ['E', 'F']
          }
        ]
      },
      global: {
        plugins: [getStore(), router]
      }
    })

    // Test sorting by average
    const displayedComparisonsAverageSorted = wrapper.vm.displayedComparisons
    expect(displayedComparisonsAverageSorted[0].firstSubmissionId).toBe('H')
    expect(displayedComparisonsAverageSorted[1].firstSubmissionId).toBe('C')
    expect(displayedComparisonsAverageSorted[2].firstSubmissionId).toBe('A')
    expect(displayedComparisonsAverageSorted[3].firstSubmissionId).toBe('E')

    const metricOptions = wrapper.get('.font-bold').findAll('.cursor-pointer')
    await metricOptions[1].trigger('click')
    await flushPromises()

    // Test sorting by max
    const displayedComparisonsMaxSorted = wrapper.vm.displayedComparisons
    expect(displayedComparisonsMaxSorted[0].firstSubmissionId).toBe('C')
    expect(displayedComparisonsMaxSorted[1].firstSubmissionId).toBe('A')
    expect(displayedComparisonsMaxSorted[2].firstSubmissionId).toBe('H')
    expect(displayedComparisonsMaxSorted[3].firstSubmissionId).toBe('E')

    await metricOptions[2].trigger('click')
    await flushPromises()

    // Test sorting by cluster
    const displayedComparisonsClusterSorted = wrapper.vm.displayedComparisons
    expect(displayedComparisonsClusterSorted[0].firstSubmissionId).toBe('E')
    expect(displayedComparisonsClusterSorted[1].firstSubmissionId).toBe('C')
    expect(displayedComparisonsClusterSorted[2].firstSubmissionId).toBe('A')
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
        plugins: [createTestingPinia({ createSpy: vi.fn }), router]
      }
    })

    expect(wrapper.text()).toContain(headerText)
  })
})

function getStore() {
  const testStore = createTestingPinia({ createSpy: vi.fn })
  store().state.submissionIdsToComparisonFileName.set('A', new Map([['B', 'file1']]))
  store().state.submissionIdsToComparisonFileName.set('B', new Map([['A', 'file1']]))
  store().state.submissionIdsToComparisonFileName.set('C', new Map([['D', 'file2']]))
  store().state.submissionIdsToComparisonFileName.set('D', new Map([['C', 'file2']]))
  store().state.submissionIdsToComparisonFileName.set('E', new Map([['F', 'file3']]))
  store().state.submissionIdsToComparisonFileName.set('F', new Map([['E', 'file3']]))
  store().state.submissionIdsToComparisonFileName.set('G', new Map([['H', 'file4']]))
  store().state.submissionIdsToComparisonFileName.set('H', new Map([['G', 'file4']]))
  return testStore
}
