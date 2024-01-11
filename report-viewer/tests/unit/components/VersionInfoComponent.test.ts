import VersionInfoComponent from '@/components/VersionInfoComponent.vue'
import { flushPromises, mount } from '@vue/test-utils'
import { describe, it, vi, expect } from 'vitest'
import * as versionTsFile from '@/model/Version'
import { Version } from '@/model/Version'

describe('VersionInfoComponent', () => {
  it('Render develop version', async () => {
    vi.spyOn(versionTsFile, 'reportViewerVersion', 'get').mockReturnValue(mockVersionJSON(0, 0, 0))
    vi.spyOn(versionTsFile, 'minimalReportVersion', 'get').mockReturnValue(mockVersionJSON(4, 0, 0))
    global.fetch = vi.fn().mockResolvedValueOnce(mockVersionResponse('v4.3.0'))

    const wrapper = mount(VersionInfoComponent)
    await flushPromises()

    expect(wrapper.text()).toContain('development version')
    expect(wrapper.text()).not.toContain(
      'The minimal version of JPlag that is supported by the viewer is v4.0.0.'
    )
  })

  it('Render outdated version', async () => {
    vi.spyOn(versionTsFile, 'reportViewerVersion', 'get').mockReturnValue(mockVersionJSON(4, 3, 0))
    vi.spyOn(versionTsFile, 'minimalReportVersion', 'get').mockReturnValue(mockVersionJSON(4, 0, 0))
    global.fetch = vi.fn().mockResolvedValueOnce(mockVersionResponse('v4.4.0'))

    const wrapper = mount(VersionInfoComponent)
    await flushPromises()

    expect(wrapper.text()).toContain('outdated version')
    expect(wrapper.text()).toContain(
      'The minimal version of JPlag that is supported by the viewer is v4.0.0.'
    )
  })

  it('Render latest version', async () => {
    vi.spyOn(versionTsFile, 'reportViewerVersion', 'get').mockReturnValue(mockVersionJSON(4, 3, 0))
    vi.spyOn(versionTsFile, 'minimalReportVersion', 'get').mockReturnValue(mockVersionJSON(4, 0, 0))
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
  return new Version(major, minor, patch)
}
