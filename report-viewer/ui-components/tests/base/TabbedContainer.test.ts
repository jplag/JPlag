import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { TabbedContainer, ToolTipComponent } from '../../base'

describe('TabbedContainer', async () => {
  it('Switch Tabs', async () => {
    const wrapper = mount(TabbedContainer, {
      props: {
        tabs: ['Tab 1', 'Tab 2', 'Tab 3']
      },
      slots: {
        'Tab-1': '<div>Content for Tab 1</div>',
        'Tab-2': '<div>Content for Tab 2</div>',
        'Tab-3': '<div>Content for Tab 3</div>'
      }
    })

    expect(wrapper.text()).toContain('Content for Tab 1')
    expect(wrapper.text()).not.toContain('Content for Tab 2')
    expect(wrapper.text()).not.toContain('Content for Tab 3')

    const headers = wrapper.findAll('p').filter((header) => header.text().includes('Tab'))

    await headers[1].trigger('click')
    expect(wrapper.text()).toContain('Content for Tab 2')
    expect(wrapper.text()).not.toContain('Content for Tab 1')
    expect(wrapper.text()).not.toContain('Content for Tab 3')

    await headers[2].trigger('click')
    expect(wrapper.text()).toContain('Content for Tab 3')
    expect(wrapper.text()).not.toContain('Content for Tab 1')
    expect(wrapper.text()).not.toContain('Content for Tab 2')
  })

  it('test loads Tooltip', () => {
    const wrapper = mount(TabbedContainer, {
      props: {
        tabs: [
          'Tab 1',
          { displayValue: 'Tab 2', tooltip: 'This is Tab 2' },
          'Tab 3',
          'Tab 4',
          { displayValue: 'Tab 5', tooltip: 'This is Tab 5' }
        ]
      },
      slots: {
        'Tab-1': '<div>Content for Tab 1</div>',
        'Tab-2': '<div>Content for Tab 2</div>',
        'Tab-3': '<div>Content for Tab 3</div>',
        'Tab-4': '<div>Content for Tab 4</div>',
        'Tab-5': '<div>Content for Tab 5</div>'
      }
    })

    expect(wrapper.findAllComponents(ToolTipComponent)).toHaveLength(2)
    expect(wrapper.text()).toContain('Tab 1')
    expect(wrapper.text()).toContain('Tab 2')
    expect(wrapper.text()).toContain('This is Tab 2')
  })

  it('Change Tab with tooltip', async () => {
    const wrapper = mount(TabbedContainer, {
      props: {
        tabs: ['Tab 1', { displayValue: 'Tab 2', tooltip: 'Nice Tooltip' }, 'Tab 3']
      },
      slots: {
        'Tab-1': '<div>Content for Tab 1</div>',
        'Tab-2': '<div>Content for Tab 2</div>',
        'Tab-3': '<div>Content for Tab 3</div>'
      }
    })

    expect(wrapper.text()).toContain('Content for Tab 1')
    expect(wrapper.text()).not.toContain('Content for Tab 2')

    const headers = wrapper.findAll('p').filter((header) => header.text().includes('Tab'))
    expect(headers[1].text()).toBe('Tab 2')

    await headers[1].trigger('click')
    expect(wrapper.text()).toContain('Content for Tab 2')
    expect(wrapper.text()).not.toContain('Content for Tab 1')
  })
})
