import ComparisonTableFilter from '@/components/ComparisonTableFilter.vue'
import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, vi, expect } from 'vitest'
import { createTestingPinia } from '@pinia/testing'
import { store } from '@/stores/store'
import { MetricJsonIdentifier } from '@/model/MetricType.ts'
import ButtonComponent from '@/components/ButtonComponent.vue'
import OptionsSelector from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import OptionComponent from '@/components/optionsSelectors/OptionComponent.vue'

describe('ComparisonTableFilter', async () => {
  it('Test search string updating', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })
    setUpStore()

    const searchValue = 'JPlag'

    wrapper.find('input').setValue(searchValue)
    await flushPromises()
    expect(wrapper.props('searchString')).toBe(searchValue)
  })

  it('Test metric changes', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })
    setUpStore()

    expect(wrapper.text()).toContain('Average')
    expect(wrapper.text()).toContain('Maximum')
    expect(wrapper.text()).toContain('Cluster')

    const options = wrapper.getComponent(OptionsSelector).findAllComponents(OptionComponent)
    expectHighlighting(0)

    await options[1].trigger('click')
    expect(store().uiState.comparisonTableSortingMetric).toBe(
      MetricJsonIdentifier.MAXIMUM_SIMILARITY
    )
    expect(store().uiState.comparisonTableClusterSorting).toBeFalsy()
    expectHighlighting(1)

    await options[4].trigger('click')
    expect(store().uiState.comparisonTableSortingMetric).toBe(MetricJsonIdentifier.SYMMETRIC)
    expect(store().uiState.comparisonTableClusterSorting).toBeFalsy()
    expectHighlighting(4)

    await options[7].trigger('click')
    expect(store().uiState.comparisonTableSortingMetric).toBe(
      MetricJsonIdentifier.AVERAGE_SIMILARITY
    )
    expect(store().uiState.comparisonTableClusterSorting).toBeTruthy()
    expectHighlighting(7)

    await options[0].trigger('click')
    expect(store().uiState.comparisonTableSortingMetric).toBe(
      MetricJsonIdentifier.AVERAGE_SIMILARITY
    )
    expect(store().uiState.comparisonTableClusterSorting).toBeFalsy()
    expectHighlighting(0)

    function expectHighlighting(index: number) {
      for (let i = 0; i < options.length; i++) {
        if (i == index) {
          expect(options[i].classes()).toContain('!bg-accent')
        } else {
          expect(options[i].classes()).not.toContain('!bg-accent')
        }
      }
    }
  })

  it('Test anonymous button', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })
    setUpStore()

    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('Hide All')

    await wrapper.getComponent(ButtonComponent).trigger('click')

    expect(store().state.anonymous.size).toBe(store().getSubmissionIds.length)
    for (const id of store().getSubmissionIds) {
      expect(store().state.anonymous).toContain(id)
    }

    // Vue does not actually rerender the component, so this is commented out
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('Show All')
    expect(wrapper.text()).not.toContain('Hide All')

    await wrapper.getComponent(ButtonComponent).trigger('click')
    expect(store().state.anonymous.size).toBe(0)
  })

  it('Test deanoymization', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })
    setUpStore(true)

    wrapper.find('input').setValue('C')
    expect(store().state.anonymous.size).toBe(store().getSubmissionIds.length - 1)
    expect(store().state.anonymous).not.toContain('C')
  })

  it('Test deanoymization - case insensitive', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })
    setUpStore(true)

    wrapper.find('input').setValue('c')
    expect(store().state.anonymous.size).toBe(store().getSubmissionIds.length - 1)
    expect(store().state.anonymous).not.toContain('C')
  })

  it('Test deanoymization - multiple', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })
    setUpStore(true)

    wrapper.find('input').setValue('c A')
    expect(store().state.anonymous.size).toBe(store().getSubmissionIds.length - 2)
    expect(store().state.anonymous).not.toContain('C')
    expect(store().state.anonymous).not.toContain('A')
  })

  it('Test deanoymization - name with spaces', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn })]
      }
    })
    setUpStore(true)

    wrapper.find('input').setValue('test')
    expect(store().state.anonymous.size).toBe(store().getSubmissionIds.length)
    expect(store().state.anonymous).toContain('test_User')

    wrapper.find('input').setValue('User')
    expect(store().state.anonymous.size).toBe(store().getSubmissionIds.length)
    expect(store().state.anonymous).toContain('test_User')

    wrapper.find('input').setValue('test User')
    expect(store().state.anonymous.size).toBe(store().getSubmissionIds.length - 1)
    expect(store().state.anonymous).not.toContain('test_User')
  })
})

function setUpStore(fillAnonymous = false) {
  const submissionsToDisplayNames = new Map<string, string>()
  submissionsToDisplayNames.set('A', 'A')
  submissionsToDisplayNames.set('B', 'B')
  submissionsToDisplayNames.set('C', 'C')
  submissionsToDisplayNames.set('test_User', 'test User')
  store().state.fileIdToDisplayName = submissionsToDisplayNames
  store().state.anonymous.clear()
  if (fillAnonymous) {
    store().state.anonymous = new Set(submissionsToDisplayNames.keys())
  }
}
