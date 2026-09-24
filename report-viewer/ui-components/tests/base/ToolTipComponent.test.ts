import { beforeAll, describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { InfoIcon, ToolTipComponent } from '../../base'
import { nextTick } from 'vue'

describe('ToolTipComponent', async () => {
  beforeAll(() => {
    // simulate relevant CSS styles for visibility from tailwind
    // we use .group.hover instead of .group:hover, since jsdom does not properly support pseudo-classes
    const style = document.createElement('style')
    style.innerHTML = `
      .invisible { visibility: hidden !important; }
      .visible { visibility: visible !important; }
      .group.hover .group-hover\\:visible { visibility: visible !important; }
    `
    document.head.appendChild(style)
  })

  it('Test display on hover', async () => {
    const wrapper = mount(ToolTipComponent, {
      attachTo: document.body,
      slots: {
        default: '<span id="text">Hover over me</span>',
        tooltip: '<span id="tooltip">Tooltip text</span>'
      }
    })

    const groupingElement = wrapper.find('.group')
    const textElement = wrapper.find('#text')
    const tooltipElement = wrapper.find('#tooltip')

    expect(textElement.isVisible()).toBe(true)
    expect(tooltipElement.isVisible()).toBe(false)

    groupingElement.element.classList.add('hover')
    await nextTick()
    expect(tooltipElement.isVisible()).toBe(true)

    groupingElement.element.classList.remove('hover')
    await nextTick()
    expect(tooltipElement.isVisible()).toBe(false)
  })

  it('show info icon', () => {
    const wrapperWithIcon = mount(ToolTipComponent, {
      showInfoSymbol: true,
      slots: {
        tooltip: '<span id="tooltip">Tooltip text</span>'
      }
    })
    const wrapperWithoutIcon = mount(ToolTipComponent, {
      showInfoSymbol: false
    })

    expect(wrapperWithIcon.findComponent(InfoIcon).exists()).toBeTruthy()
    expect(wrapperWithoutIcon.findComponent(InfoIcon).exists()).toBeFalsy()
  })

  it('dont show info icon without tooltip slot', () => {
    const wrapper = mount(ToolTipComponent, {
      showInfoSymbol: true,
      slots: {
        default: '<span id="text">Hover over me to see nothing</span>'
      }
    })

    expect(wrapper.findComponent(InfoIcon).exists()).toBeFalsy()
  })
})
