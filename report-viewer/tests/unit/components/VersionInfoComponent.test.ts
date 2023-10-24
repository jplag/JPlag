import VersionInfoComponent from '@/components/VersionInfoComponent.vue'
import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, vi, expect } from 'vitest'
import version from '@/version.json'

vi.mock('@/version.json')

describe('VersionInfoComponent', () => {
  it('Render develop version', async () => {
    version.report_viewer_version = mockVersionJSON(0, 0, 0)
    version.minimal_report_version = mockVersionJSON(4, 0, 0)
    global.fetch = vi.fn().mockResolvedValueOnce(mockVersionResponse('v4.3.0'))

    const wrapper = mount(VersionInfoComponent)
    await flushPromises()

    expect(wrapper.text()).toContain('development version')
    expect(wrapper.text()).not.toContain(
      'The minimal version of JPlag that is supported by the viewer is v4.0.0.'
    )
  })

  it('Render outdated version', async () => {
    version.report_viewer_version = mockVersionJSON(4, 3, 0)
    version.minimal_report_version = mockVersionJSON(4, 0, 0)
    global.fetch = vi.fn().mockResolvedValueOnce(mockVersionResponse('v4.4.0'))

    const wrapper = mount(VersionInfoComponent)
    await flushPromises()

    expect(wrapper.text()).toContain('outdated version')
    expect(wrapper.text()).toContain(
      'The minimal version of JPlag that is supported by the viewer is v4.0.0.'
    )
  })

  it('Render latest version', async () => {
    version.report_viewer_version = mockVersionJSON(4, 3, 0)
    version.minimal_report_version = mockVersionJSON(4, 0, 0)
    global.fetch = vi.fn().mockResolvedValueOnce(mockVersionResponse('v4.3.0'))

    const wrapper = mount(VersionInfoComponent)
    await flushPromises()

    expect(wrapper.text()).toContain('JPlag v4.3.0')
    expect(wrapper.text()).toContain(
      'The minimal version of JPlag that is supported by the viewer is v4.0.0.'
    )
  })
})

function mockVersionResponse(version: string) {
  return {
    json: () =>
      Promise.resolve({
        tag_name: version
      })
  }
}

function mockVersionJSON(major: number, minor: number, patch: number) {
  return {
    major: major,
    minor: minor,
    patch: patch
  }
}
