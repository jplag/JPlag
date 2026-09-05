import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import ComparisonTableFilter from '../../../widget/comparisonTable/ComparisonTableFilter.vue'
import { ButtonComponent } from '../../../base'
import { MetricJsonIdentifier } from '@jplag/model'

describe('ComparisonTableFilter', async () => {
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

  it('Test anonymous button', async () => {
    const wrapper = mount(ComparisonTableFilter)

    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('Anonymize All')

    await wrapper.getComponent(ButtonComponent).trigger('click')

    expect(wrapper.emitted('changeAnonymousForAll')).toBeTruthy()
  })

  it('Hides the weighted similarity option without frequency analysis', () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        secondaryMetrics: new Set([
          MetricJsonIdentifier.MAXIMUM_SIMILARITY,
          MetricJsonIdentifier.LONGEST_MATCH,
          MetricJsonIdentifier.MAXIMUM_LENGTH
        ])
      }
    })

    expect(wrapper.text()).toContain('Maximum Similarity')
    expect(wrapper.text()).not.toContain('Weighted Avg Similarity')
  })

  it('Offers the weighted similarity option with frequency analysis', () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        secondaryMetrics: new Set([
          MetricJsonIdentifier.MAXIMUM_SIMILARITY,
          MetricJsonIdentifier.LONGEST_MATCH,
          MetricJsonIdentifier.MAXIMUM_LENGTH,
          MetricJsonIdentifier.WEIGHTED_SIMILARITY
        ])
      }
    })

    expect(wrapper.text()).toContain('Weighted Avg Similarity')
  })

  it('Resets a stale weighted selection when frequency analysis is off', async () => {
    let secondaryMetric: MetricJsonIdentifier = MetricJsonIdentifier.WEIGHTED_SIMILARITY
    mount(ComparisonTableFilter, {
      props: {
        secondaryMetric,
        'onUpdate:secondaryMetric': (m) => (secondaryMetric = m),
        secondaryMetrics: new Set([
          MetricJsonIdentifier.MAXIMUM_SIMILARITY,
          MetricJsonIdentifier.LONGEST_MATCH,
          MetricJsonIdentifier.MAXIMUM_LENGTH
        ])
      }
    })
    await flushPromises()

    expect(secondaryMetric).toBe(MetricJsonIdentifier.MAXIMUM_SIMILARITY)
  })
})
