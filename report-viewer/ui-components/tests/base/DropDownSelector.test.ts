import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { DropDownSelector } from '../../base'

describe('DropDownSelector', async () => {
  it('renders all options', () => {
    const options = ['Option 1', 'Option 2', 'Option 3']
    const wrapper = mount(DropDownSelector, {
      props: {
        options: options
      }
    })
    options.forEach((option) => {
      expect(wrapper.text()).toContain(option)
    })
  })

  it('renders name display function', async () => {
    const options = ['test1', 'test2', 'test3']
    const nameMap: Record<string, string> = {
      test1: 'Test 1',
      test2: 'Test 2',
      test3: 'Test 3'
    }
    const wrapper = mount(DropDownSelector, {
      props: {
        options: options,
        getDisplayName: (name: string) => nameMap[name]
      }
    })

    expect(wrapper.text()).toContain('Test 1')
    expect(wrapper.text()).toContain('Test 2')
    expect(wrapper.text()).toContain('Test 3')
    expect(wrapper.text()).not.toContain('test1')
    expect(wrapper.text()).not.toContain('test2')
    expect(wrapper.text()).not.toContain('test3')

    nameMap['test1'] = 'Test Not 1'
    await wrapper.setProps({ getDisplayName: (name: string) => nameMap[name] })
    expect(wrapper.text()).toContain('Test Not 1')
    expect(wrapper.text()).not.toContain('Test 1')
  })

  it('emits selected option on change', async () => {
    const options = ['Option 1', 'Option 2', 'Option 3']
    const wrapper = mount(DropDownSelector, {
      props: {
        options: options
      }
    })

    wrapper.find('select').setValue('Option 2')
    expect(wrapper.emitted('selectionChanged')).toBeTruthy()
    expect(wrapper.emitted('selectionChanged')![0]).toEqual(['Option 2'])
  })
})
