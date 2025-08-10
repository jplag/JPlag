import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import ComparisonTableFilter from '../../../widget/comparisonTable/ComparisonTableFilter.vue'
import { ButtonComponent } from '../../../base'

describe('ComparisonTableFilter', async () => {
  it('Test search string updating', async () => {
    const wrapper = mount(ComparisonTableFilter, {
      props: {
        searchString: '',
        'onUpdate:searchString': (e) => wrapper.setProps({ searchString: e })
      }
    })

    const searchValue = 'JPlag'

    wrapper.find('input').setValue(searchValue)
    await flushPromises()
    expect(wrapper.props('searchString')).toBe(searchValue)
  })

  it('Test anonymous button', async () => {
    const wrapper = mount(ComparisonTableFilter)

    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('Anonymize All')

    await wrapper.getComponent(ButtonComponent).trigger('click')

    expect(wrapper.emitted('changeAnonymousForAll')).toBeTruthy()
  })
})
