import ComparisonTableFilter from '@/components/ComparisonTableFilter.vue'
import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, vi, expect, beforeAll } from 'vitest'
import { store } from '@/stores/store'
import { MetricType } from '@/model/MetricType.ts'
import ButtonComponent from '@/components/ButtonComponent.vue'
import OptionsSelector from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import OptionComponent from '@/components/optionsSelectors/OptionComponent.vue'

const store = {
  state: {
    anonymous: new Set<string>()
  },
  uiState: {
    comparisonTableSortingMetric: MetricType.AVERAGE,
    comparisonTableClusterSorting: false
  },
  getSubmissionIds: ['A', 'B', 'C', 'test User']
}

describe('ComparisonTableFilter', async () => {
  beforeAll(() => {
    vi.mock('@/stores/store', () => ({
      store: vi.fn(() => {
        return store
      })
    }))
  })

  it('Test search string updating', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      }
    })

    const searchValue = 'JPlag'

    wrapper.find('input').setValue(searchValue)
    await flushPromises()
    expect(wrapper.props('searchString')).toBe(searchValue)
  })

  it('Test metric changes', async () => {
    const wrapper = mount(ComparisonTableFilter)

    expect(wrapper.text()).toContain('Average')
    expect(wrapper.text()).toContain('Maximum')
    expect(wrapper.text()).toContain('Cluster')

    const options = wrapper.getComponent(OptionsSelector).findAllComponents(OptionComponent)

    expectHighlighting(0)

    await options[1].trigger('click')
    expect(store.uiState.comparisonTableSortingMetric).toBe(MetricType.MAXIMUM)
    expect(store.uiState.comparisonTableClusterSorting).toBeFalsy()
    expectHighlighting(1)

    await options[2].trigger('click')
    expect(store.uiState.comparisonTableSortingMetric).toBe(MetricType.AVERAGE)
    expect(store.uiState.comparisonTableClusterSorting).toBeTruthy()
    expectHighlighting(2)

    await options[0].trigger('click')
    expect(store.uiState.comparisonTableSortingMetric).toBe(MetricType.AVERAGE)
    expect(store.uiState.comparisonTableClusterSorting).toBeFalsy()
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
    const wrapper = mount(ComparisonTableFilter)

    expect(wrapper.text()).toContain('Hide All')

    await wrapper.getComponent(ButtonComponent).trigger('click')
    expect(store.state.anonymous.size).toBe(store.getSubmissionIds.length)
    for (const id of store.getSubmissionIds) {
      expect(store.state.anonymous).toContain(id)
    }

    // Vue does not actually rerender the component, so this is commented out
    //expect(wrapper.text()).toContain('Show All')
    //expect(wrapper.text()).not.toContain('Hide All')

    await wrapper.getComponent(ButtonComponent).trigger('click')
    expect(store.state.anonymous.size).toBe(0)
  })

  it('Test deanoymization', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      }
    })
    store.state.anonymous = new Set(store.getSubmissionIds)

    wrapper.find('input').setValue('C')
    expect(store.state.anonymous.size).toBe(store.getSubmissionIds.length - 1)
    expect(store.state.anonymous).not.toContain('C')
  })

  it('Test deanoymization - case insensitive', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      }
    })
    store.state.anonymous = new Set(store.getSubmissionIds)

    wrapper.find('input').setValue('c')
    expect(store.state.anonymous.size).toBe(store.getSubmissionIds.length - 1)
    expect(store.state.anonymous).not.toContain('C')
  })

  it('Test deanoymization - multiple', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      }
    })
    store.state.anonymous = new Set(store.getSubmissionIds)

    wrapper.find('input').setValue('c A')
    expect(store.state.anonymous.size).toBe(store.getSubmissionIds.length - 2)
    expect(store.state.anonymous).not.toContain('C')
    expect(store.state.anonymous).not.toContain('A')
  })

  it('Test deanoymization - name with spaces', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      }
    })
    store.state.anonymous = new Set(store.getSubmissionIds)

    wrapper.find('input').setValue('test')
    expect(store.state.anonymous.size).toBe(store.getSubmissionIds.length)
    expect(store.state.anonymous).toContain('test User')

    wrapper.find('input').setValue('User')
    expect(store.state.anonymous.size).toBe(store.getSubmissionIds.length)
    expect(store.state.anonymous).toContain('test User')

    wrapper.find('input').setValue('test User')
    expect(store.state.anonymous.size).toBe(store.getSubmissionIds.length - 1)
    expect(store.state.anonymous).not.toContain('test User')
  })
})
