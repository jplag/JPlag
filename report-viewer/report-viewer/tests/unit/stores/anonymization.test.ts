import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mockFiles, mockParser, mockSessionStorage, registerMockRouter } from './mocks'

registerMockRouter()
mockSessionStorage()
mockParser()

import { reportStore } from '../../../src/stores/reportStore'
import { setActivePinia, createPinia } from 'pinia'

describe('Test Anonymization', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    reportStore().reset()
    reportStore().loadReport(mockFiles, [], 'test')
  })

  it('Getting Display Name', () => {
    expect(reportStore().getPlainDisplayName('test1')).toEqual('Test1')
    expect(reportStore().getDisplayName('test1')).toEqual('Test1')
  })

  it('Getting Display Name with Anonymization', () => {
    reportStore().setAnonymous('test1', true)
    expect(reportStore().getAnonymizedName('test1')).toEqual('anon1')
    expect(reportStore().getDisplayName('test1')).toEqual('anon1')
    expect(reportStore().getPlainDisplayName('test1')).toEqual('Test1')
  })

  it('Keep number asociated with anonymized names', () => {
    reportStore().setAnonymous('test1', true)
    reportStore().setAnonymous('test2', true)
    expect(reportStore().getDisplayName('test1')).toEqual('anon1')
    expect(reportStore().getDisplayName('test2')).toEqual('anon2')

    reportStore().setAnonymous('test1', false)
    reportStore().setAnonymous('test3', true)

    expect(reportStore().getDisplayName('test1')).toEqual('Test1')
    expect(reportStore().getDisplayName('test2')).toEqual('anon2')
    expect(reportStore().getDisplayName('test3')).toEqual('anon3')

    reportStore().setAnonymous('test1', true)
    expect(reportStore().getDisplayName('test1')).toEqual('anon1')
  })

  it('Test isAnonymized', () => {
    expect(reportStore().isAnonymized('test1')).toEqual(false)
    reportStore().setAnonymous('test1', true)
    expect(reportStore().isAnonymized('test1')).toEqual(true)
    reportStore().setAnonymous('test1', false)
    expect(reportStore().isAnonymized('test1')).toEqual(false)
    reportStore().setAnonymous('test1', true)

    reportStore().setAnonymous('test2', true)
    expect(reportStore().isAnonymized('test2')).toEqual(true)
    reportStore().setAnonymous('test2', false)
    expect(reportStore().isAnonymized('test2')).toEqual(false)
  })

  it('Test toggleAnonymous', () => {
    expect(reportStore().isAnonymized('test1')).toEqual(false)
    reportStore().toggleAnonymous('test1')
    expect(reportStore().isAnonymized('test1')).toEqual(true)
    reportStore().toggleAnonymous('test1')
    expect(reportStore().isAnonymized('test1')).toEqual(false)

    reportStore().setAnonymous('test2', true)
    expect(reportStore().isAnonymized('test2')).toEqual(true)
    reportStore().toggleAnonymous('test2')
    expect(reportStore().isAnonymized('test2')).toEqual(false)
    reportStore().toggleAnonymous('test2')
    expect(reportStore().isAnonymized('test2')).toEqual(true)
  })

  it('Test allAreAnonymized', () => {
    expect(reportStore().allAreAnonymized()).toEqual(false)
    reportStore().setAnonymous('test1', true)
    expect(reportStore().allAreAnonymized()).toEqual(false)
    reportStore().setAnonymous('test2', true)
    expect(reportStore().allAreAnonymized()).toEqual(false)
    reportStore().setAnonymous('test3', true)
    expect(reportStore().allAreAnonymized()).toEqual(true)
    reportStore().setAnonymous('test2', false)
    expect(reportStore().allAreAnonymized()).toEqual(false)
    reportStore().setAnonymous('test1', false)
    expect(reportStore().allAreAnonymized()).toEqual(false)
    reportStore().setAnonymous('test2', true)
    expect(reportStore().allAreAnonymized()).toEqual(false)
    reportStore().setAnonymous('test1', true)
    expect(reportStore().allAreAnonymized()).toEqual(true)
  })

  it('Test toggleAnonymousForAll', () => {
    expect(reportStore().isAnonymized('test1')).toEqual(false)
    expect(reportStore().isAnonymized('test2')).toEqual(false)
    expect(reportStore().isAnonymized('test3')).toEqual(false)
    reportStore().toggleAnonymousForAll()
    expect(reportStore().isAnonymized('test1')).toEqual(true)
    expect(reportStore().isAnonymized('test2')).toEqual(true)
    expect(reportStore().isAnonymized('test3')).toEqual(true)
    reportStore().toggleAnonymousForAll()
    expect(reportStore().isAnonymized('test1')).toEqual(false)
    expect(reportStore().isAnonymized('test2')).toEqual(false)
    expect(reportStore().isAnonymized('test3')).toEqual(false)

    // Test behavior when some are already anonymized and some are not
    reportStore().setAnonymous('test1', true)
    reportStore().setAnonymous('test2', true)
    expect(reportStore().isAnonymized('test1')).toEqual(true)
    expect(reportStore().isAnonymized('test2')).toEqual(true)
    expect(reportStore().isAnonymized('test3')).toEqual(false)
    reportStore().toggleAnonymousForAll()
    expect(reportStore().isAnonymized('test1')).toEqual(true)
    expect(reportStore().isAnonymized('test2')).toEqual(true)
    expect(reportStore().isAnonymized('test3')).toEqual(true)
  })
})
