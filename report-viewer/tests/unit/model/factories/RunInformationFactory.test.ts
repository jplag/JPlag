import { beforeEach, describe, it, expect } from 'vitest'
import { RunInformationFactory } from '@/model/factories/RunInformationFactory'
import validRunInformation from './assets/ValidRunInformation.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'

describe('Test JSON to Run Information', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    store().setLoadingType('zip')
  })

  it('Test Valid JSON', async () => {
    store().state.files['runInformation.json'] = JSON.stringify(validRunInformation)
    const result = await RunInformationFactory.getRunInformation()

    expect(result).toEqual({
      _version: { major: 6, minor: 2, patch: 0 },
      _failedSubmissions: ['failed1', 'failedTest'],
      _dateOfExecution: '06/05/25',
      _executionTime: 471,
      _totalComparisons: 2145
    })
  })
})
