import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import MetricSelector from '@/components/optionsSelectors/MetricSelector.vue'
import { MetricType } from '@/model/MetricType'

describe('OptionSelectorComponent', () => {
  it('renders all options', async () => {
    const wrapper = mount(MetricSelector, {
      props: {
        title: 'Test:'
      }
    })

    expect(wrapper.text()).toContain('Test:')
    expect(wrapper.text()).toContain('Average Similarity')
    expect(wrapper.text()).toContain('Maximum Similarity')
  })

  it('renders given metrics only', async () => {
    const wrapper = mount(MetricSelector, {
      props: {
        title: 'Test:',
        metrics: [MetricType.AVERAGE]
      }
    })

    expect(wrapper.text()).toContain('Test:')
    expect(wrapper.text()).toContain('Average Similarity')
    expect(wrapper.text()).not.toContain('Maximum Similarity')
  })

  it('switch selection', async () => {
    const wrapper = mount(MetricSelector, {
      props: {
        title: 'Test:'
      }
    })

    await wrapper.findAllComponents({ name: 'OptionComponent' })[1].trigger('click')

    expect(wrapper.emitted('selectionChanged')).toBeTruthy()
    expect(wrapper.emitted('selectionChanged')?.length).toBe(1)
    expect(wrapper.emitted('selectionChanged')?.[0]).toEqual([MetricType.MAXIMUM])
  })
})
