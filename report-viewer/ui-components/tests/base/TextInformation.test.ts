import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { TextInformation, ToolTipComponent } from '../../base'

describe('TextInformation', () => {
  it('Test display', () => {
    const wrapper = mount(TextInformation, {
      props: {
        label: 'Label'
      },
      slots: {
        default: 'Information'
      }
    })

    expect(wrapper.text()).toContain('Label:Information')
  })

  it('Test display with tooltip', () => {
    const wrapper = mount(TextInformation, {
      props: {
        label: 'Label'
      },
      slots: {
        default: 'Information',
        tooltip: 'This is a tooltip'
      }
    })

    expect(wrapper.findComponent(ToolTipComponent).exists()).toBeTruthy()
    expect(wrapper.text()).toContain('This is a tooltip')
  })
})
