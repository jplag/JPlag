import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { OptionComponent, OptionsSelectorComponent } from '../../../widget'

const SELECTED_OPTION_CLASS = 'bg-accent-light/40!'

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
    ).toContain(SELECTED_OPTION_CLASS)
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
    ).toContain(SELECTED_OPTION_CLASS)
    expect(
      wrapper
        .findAllComponents(OptionComponent)
        .find((e) => e.text() === 'Option 2')
        ?.classes()
    ).not.toContain(SELECTED_OPTION_CLASS)

    await wrapper.findAllComponents({ name: 'OptionComponent' })[1].trigger('click')

    expect(wrapper.emitted('selectionChanged')).toBeTruthy()
    expect(wrapper.emitted('selectionChanged')?.length).toBe(1)
    expect(wrapper.emitted('selectionChanged')?.[0]).toEqual([1])

    expect(
      wrapper
        .findAllComponents(OptionComponent)
        .find((e) => e.text() === 'Option 1')
        ?.classes()
    ).not.toContain(SELECTED_OPTION_CLASS)
    expect(
      wrapper
        .findAll('.cursor-pointer')
        .find((e) => e.text() === 'Option 2')
        ?.classes()
    ).toContain(SELECTED_OPTION_CLASS)
  })
})
