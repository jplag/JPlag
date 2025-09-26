import { describe, expect, it, vi, afterEach, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import { ToastComponent } from '../../base'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

describe('ToastComponent', async () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })
  afterEach(() => {
    vi.useRealTimers()
  })

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
    vi.advanceTimersByTime(90)
    await flushPromises()
    expect(wrapper.isVisible()).toBeTruthy()
    vi.advanceTimersByTime(20)
    await flushPromises()
    expect(wrapper.isVisible()).toBeFalsy()
  })
})
