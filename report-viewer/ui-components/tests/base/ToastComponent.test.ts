import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { ToastComponent } from '../../base'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

describe('ToastComponent', async () => {
  it('renders', () => {
    const wrapper = mount(ToastComponent, {
      props: {
        timeToLive: 100000
      },
      slots: {
        default: 'Test Message'
      }
    })
    expect(wrapper.text()).toContain('Test Message')
  })

  it('hide on x click', async () => {
    const wrapper = mount(ToastComponent, {
      props: {
        timeToLive: 100000
      },
      slots: {
        default: 'Test Message'
      }
    })

    expect(wrapper.isVisible()).toBeTruthy()
    await wrapper.findComponent(FontAwesomeIcon).trigger('click')
    expect(wrapper.isVisible()).toBeFalsy()
  })

  it('hide after ttl', async () => {
    const wrapper = mount(ToastComponent, {
      props: {
        timeToLive: 100
      },
      slots: {
        default: 'Test Message'
      }
    })

    expect(wrapper.isVisible()).toBeTruthy()
    await new Promise((resolve) => setTimeout(resolve, 200))
    expect(wrapper.isVisible()).toBeFalsy()
  })
})
