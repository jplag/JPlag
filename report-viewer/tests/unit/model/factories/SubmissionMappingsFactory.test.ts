import { beforeEach, describe, it, expect } from 'vitest'
import { SubmissionMappingsFactory } from '@/model/factories/SubmissionMappingsFactory'
import validSubmissionMappings from './assets/ValidSubmissionMappings.json'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'

describe('Test JSON to Options', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    store().setLoadingType('zip')
  })

  it('Test Valid JSON', async () => {
    store().state.files['submissionMappings.json'] = JSON.stringify(validSubmissionMappings)
    await SubmissionMappingsFactory.getSubmissionMappings()

    expect(store().submissionDisplayName('Submission01')).toEqual('submission 01')
    expect(store().submissionDisplayName('Test123')).toEqual('Test123')
    expect(store().submissionDisplayName('MaxMustermann')).toEqual('max_mustermann')
    expect(store().state.fileIdToDisplayName.size).toEqual(3)

    const comparisonFileMapKeys = Array.from(store().state.submissionIdsToComparisonFileName.keys())
    expect(store().state.submissionIdsToComparisonFileName.size).toEqual(3)
    for (const keys of comparisonFileMapKeys) {
      expect(store().state.submissionIdsToComparisonFileName.get(keys)?.size).toEqual(2)
    }
    expect(store().getComparisonFileName('Submission01', 'Test123')).toEqual(
      'submission01-test123.json'
    )
    expect(store().getComparisonFileName('Test123', 'Submission01')).toEqual(
      'submission01-test123.json'
    )
    expect(store().getComparisonFileName('MaxMustermann', 'Submission01')).toEqual(
      'submission01-max_mustermann.json'
    )
  })
})
