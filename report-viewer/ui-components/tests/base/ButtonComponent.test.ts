import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { ButtonComponent } from '../../base'

describe('ButtonComponent', () => {
  it('Test click', () => {
    const wrapper = mount(ButtonComponent)

    wrapper.trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
  })

  it('Test slot', () => {
    const wrapper = mount(ButtonComponent, {
      slots: {
        default: '<b>Click Me</b>'
      }
    })

    expect(wrapper.text()).toContain('Click Me')
    expect(wrapper.find('b').exists()).toBeTruthy()
  })
})
