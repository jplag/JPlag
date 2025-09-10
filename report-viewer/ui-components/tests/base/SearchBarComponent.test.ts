import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { SearchBarComponent } from '../../base'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

describe('SearchBarComponent', async () => {
  it('render placeholder', () => {
    const wrapper = mount(SearchBarComponent, {
      props: {
        placeholder: 'Test'
      }
    })
    expect(wrapper.find('input').attributes('placeholder')).toBe('Test')
  })

  it('test v-model', async () => {
    const wrapper = mount(SearchBarComponent, {
      props: {
        modelValue: 'Initial'
      }
    })

    expect(wrapper.find('input').element.value).toBe('Initial')

    await wrapper.setProps({ modelValue: 'Updated' })
    expect(wrapper.find('input').element.value).toBe('Updated')

    wrapper.find('input').setValue('New Value')
    expect(wrapper.emitted('update:modelValue')).toBeTruthy()
    expect(wrapper.emitted('update:modelValue')![0]).toEqual(['New Value'])
  })

  it('test emit inputChanged', () => {
    const wrapper = mount(SearchBarComponent)

    wrapper.find('input').setValue('Search Term')
    expect(wrapper.emitted('inputChanged')).toBeTruthy()
    expect(wrapper.emitted('inputChanged')![0]).toEqual(['Search Term'])
  })

  it('test emit search on click', () => {
    const wrapper = mount(SearchBarComponent)
    wrapper.find('input').setValue('Search Term')

    wrapper.findComponent(FontAwesomeIcon).trigger('click')
    expect(wrapper.emitted('searchClicked')).toBeTruthy()
    expect(wrapper.emitted('searchClicked')![0]).toEqual(['Search Term'])
  })
})
