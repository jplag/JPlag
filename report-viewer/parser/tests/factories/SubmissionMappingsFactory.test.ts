import { describe, it, expect } from 'vitest'
import validSubmissionMappings from './assets/ValidSubmissionMappings.json?raw'
import { SubmissionMappingsFactory } from '../../src'

describe('Test JSON to Options', () => {
  it('Test Valid JSON', () => {
    const { idToDisplayNameMap, comparisonFilesLookup } =
      SubmissionMappingsFactory.getSubmissionMappings(validSubmissionMappings)

    expect(idToDisplayNameMap.get('Submission01')).toEqual('submission 01')
    expect(idToDisplayNameMap.get('Test123')).toEqual('Test123')
    expect(idToDisplayNameMap.get('MaxMustermann')).toEqual('max_mustermann')
    expect(idToDisplayNameMap.size).toEqual(3)

    const comparisonFileMapKeys = Array.from(comparisonFilesLookup.keys())
    expect(comparisonFilesLookup.size).toEqual(3)
    for (const keys of comparisonFileMapKeys) {
      expect(comparisonFilesLookup.get(keys)?.size).toEqual(2)
    }
    expect(comparisonFilesLookup.get('Submission01')?.get('Test123')).toEqual(
      'submission01-test123.json'
    )
    expect(comparisonFilesLookup.get('Test123')?.get('Submission01')).toEqual(
      'submission01-test123.json'
    )
    expect(comparisonFilesLookup.get('MaxMustermann')?.get('Submission01')).toEqual(
      'submission01-max_mustermann.json'
    )
  })
})
