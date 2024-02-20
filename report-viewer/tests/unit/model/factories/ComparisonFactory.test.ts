import { vi, it, beforeAll, describe, expect } from 'vitest'
import validNew from './ValidNewComparison.json'
import validOld from './ValidOldComparison.json'
import { ComparisonFactory } from '@/model/factories/ComparisonFactory'
import { store } from '@/stores/store'
import { MetricType } from '@/model/MetricType'

const store = {
  state: {
    localModeUsed: false,
    zipModeUsed: true,
    singleModeUsed: false,
    files: {}
  },
  getComparisonFileName: (id1: string, id2: string) => {
    return `${id1}-${id2}.json`
  },
  filesOfSubmission: (name: string) => {
    return [
      {
        name: `${name}/Structure.java`,
        value: ''
      },
      {
        name: `${name}/Submission.java`,
        value: ''
      }
    ]
  },
  getSubmissionFile: (id: string, name: string) => {
    return {
      fileName: name,
      submissionId: id,
      matchedTokenCount: 0
    }
  }
}

describe('Test JSON to Comparison', () => {
  beforeAll(() => {
    vi.mock('@/stores/store', () => ({
      store: vi.fn(() => {
        return store
      })
    }))
  })

  it('Post 5.0', async () => {
    store.state.files['root1-root2.json'] = JSON.stringify(validNew)

    const result = await ComparisonFactory.getComparison(
      store.getComparisonFileName('root1', 'root2')
    )

    expect(result).toBeDefined()
    expect(result.firstSubmissionId).toBe('root1')
    expect(result.secondSubmissionId).toBe('root2')
    expect(result.similarities[MetricType.AVERAGE]).toBe(0.6900452488687783)
    expect(result.similarities[MetricType.MAXIMUM]).toBe(0.9936000000000001)
    expect(result.filesOfFirstSubmission).toBeDefined()
    expect(result.filesOfSecondSubmission).toBeDefined()
    expect(result.allMatches.length).toBe(4)
    expect(result.matchesInFirstSubmission.size).toBe(2)
    expect(result.matchesInSecondSubmissions.size).toBe(2)
  })

  it('Pre 5.0', async () => {
    store.state.files['root1-root2.json'] = JSON.stringify(validOld)

    const result = await ComparisonFactory.getComparison(
      store.getComparisonFileName('root1', 'root2')
    )

    expect(result).toBeDefined()
    expect(result.firstSubmissionId).toBe('root1')
    expect(result.secondSubmissionId).toBe('root2')
    expect(result.similarities[MetricType.AVERAGE]).toBe(0.6900452488687783)
    expect(result.similarities[MetricType.MAXIMUM]).toBe(Number.NaN)
    expect(result.filesOfFirstSubmission).toBeDefined()
    expect(result.filesOfSecondSubmission).toBeDefined()
    expect(result.allMatches.length).toBe(4)
    expect(result.matchesInFirstSubmission.size).toBe(2)
    expect(result.matchesInSecondSubmissions.size).toBe(2)
  })
})
