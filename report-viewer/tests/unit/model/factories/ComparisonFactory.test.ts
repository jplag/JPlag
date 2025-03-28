import { it, beforeEach, describe, expect } from 'vitest'
import validNew from './ValidComparison.json'
import { ComparisonFactory } from '@/model/factories/ComparisonFactory'
import { store } from '@/stores/store'
import { MetricType } from '@/model/MetricType'
import { setActivePinia, createPinia } from 'pinia'

describe('Test JSON to Comparison', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    store().setLoadingType('zip')
  })

  it('Post 5.0', async () => {
    store().state.files['root1-root2.json'] = JSON.stringify(validNew)
    store().state.submissionIdsToComparisonFileName.set(
      'root1',
      new Map([['root2', 'root1-root2.json']])
    )
    store().state.submissionIdsToComparisonFileName.set(
      'root2',
      new Map([['root1', 'root1-root2.json']])
    )
    store().state.submissions['root1'] = new Map()
    store().state.submissions['root1'].set('root1/Structure.java', {
      fileName: 'root1/Structure.java',
      value: '',
      submissionId: 'root1',
      matchedTokenCount: 0,
      displayName: 'Structure.java'
    })
    store().state.submissions['root1'].set('root1/Submission.java', {
      fileName: 'root1/Submission.java',
      value: '',
      submissionId: 'root1',
      matchedTokenCount: 0,
      displayName: 'Submission.java'
    })
    store().state.submissions['root2'] = new Map()
    store().state.submissions['root2'].set('root2/Structure.java', {
      fileName: 'root2/Structure.java',
      value: '',
      submissionId: 'root2',
      matchedTokenCount: 0,
      displayName: 'Structure.java'
    })
    store().state.submissions['root2'].set('root2/Submission.java', {
      fileName: 'root2/Submission.java',
      value: '',
      submissionId: 'root2',
      matchedTokenCount: 0,
      displayName: 'Submission.java'
    })

    const result = await ComparisonFactory.getComparison(
      store().getComparisonFileName('root1', 'root2')
    )

    expect(result).toBeDefined()
    expect(result.firstSubmissionId).toBe('root1')
    expect(result.secondSubmissionId).toBe('root2')
    expect(result.similarities[MetricType.AVERAGE]).toBe(0.45)
    expect(result.similarities[MetricType.MAXIMUM]).toBe(0.5)
    expect(result.filesOfFirstSubmission).toBeDefined()
    expect(result.filesOfSecondSubmission).toBeDefined()
    expect(result.allMatches.length).toBe(4)
    expect(result.matchesInFirstSubmission.size).toBe(2)
    expect(result.matchesInSecondSubmissions.size).toBe(2)
    expect(result.firstSimilarity).toBe(0.4)
    expect(result.secondSimilarity).toBe(0.5)
  })
})
