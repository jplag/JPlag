import { describe, it, expect } from 'vitest'
import validRunInformation from './assets/ValidRunInformation.json?raw'
import { RunInformationFactory } from '../../src'
import { SubmissionState } from '@jplag/model'

describe('Test JSON to Run Information', () => {
  it('Test Valid JSON', () => {
    const result = RunInformationFactory.getRunInformation(validRunInformation)

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
