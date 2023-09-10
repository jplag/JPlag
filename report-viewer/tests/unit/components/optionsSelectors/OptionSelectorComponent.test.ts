import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import OptionsSelectorComponent from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import OptionComponent from '@/components/optionsSelectors/OptionComponent.vue'

describe('OptionSelectorComponent', () => {
  it('renders all options', async () => {
    const wrapper = mount(OptionsSelectorComponent, {
      props: {
        title: 'Test:',
        labels: ['Option 1', 'Option 2', 'Option 3'],
        defaultSelected: 1
      }
    })

    expect(wrapper.text()).toContain('Test:')
    expect(wrapper.text()).toContain('Option 1')
    expect(wrapper.text()).toContain('Option 2')
    expect(wrapper.text()).toContain('Option 3')

    expect(
      wrapper
        .findAllComponents(OptionComponent)
        .find((e) => e.text() === 'Option 2')
        ?.classes()
    ).toContain('!bg-accent')
  })

  it('switch selection', async () => {
    const wrapper = mount(OptionsSelectorComponent, {
      props: {
        title: 'Test:',
        labels: ['Option 1', 'Option 2'],
        defaultSelected: 0
      }
    })

    expect(
      wrapper
        .findAllComponents(OptionComponent)
        .find((e) => e.text() === 'Option 1')
        ?.classes()
    ).toContain('!bg-accent')
    expect(
      wrapper
        .findAllComponents(OptionComponent)
        .find((e) => e.text() === 'Option 2')
        ?.classes()
    ).not.toContain('!bg-accent')

    await wrapper.findAllComponents({ name: 'OptionComponent' })[1].trigger('click')

    expect(wrapper.emitted('selectionChanged')).toBeTruthy()
    expect(wrapper.emitted('selectionChanged')?.length).toBe(1)
    expect(wrapper.emitted('selectionChanged')?.[0]).toEqual([1])

    expect(
      wrapper
        .findAllComponents(OptionComponent)
        .find((e) => e.text() === 'Option 1')
        ?.classes()
    ).not.toContain('!bg-accent')
    expect(
      wrapper
        .findAll('.cursor-pointer')
        .find((e) => e.text() === 'Option 2')
        ?.classes()
    ).toContain('!bg-accent')
  })
})
