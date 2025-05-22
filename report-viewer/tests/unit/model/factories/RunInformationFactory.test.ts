import { beforeEach, describe, it, expect } from 'vitest'
import { RunInformationFactory } from '@/model/factories/RunInformationFactory'
import validRunInformation from './assets/ValidRunInformation.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'
import { SubmissionState } from '@/model/RunInformation'

describe('Test JSON to Run Information', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('Test Valid JSON', async () => {
    store().state.files['runInformation.json'] = JSON.stringify(validRunInformation)
    const result = await RunInformationFactory.getRunInformation()

    expect(result).toEqual({
      _version: { major: 6, minor: 2, patch: 0 },
      _failedSubmissions: [
        {
          submissionId: 'failed1',
          submissionState: SubmissionState.TOO_SMALL
        },
        {
          submissionId: 'failedTest',
          submissionState: SubmissionState.CANNOT_PARSE
        }
      ],
      _dateOfExecution: '06/05/25',
      _executionTime: 471,
      _totalComparisons: 2145
    })
  })
})
