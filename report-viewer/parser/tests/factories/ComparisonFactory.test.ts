import { it, describe, expect } from 'vitest'
import validNew from './assets/ValidComparison.json?raw'
import submissionFileIndex from './assets/ValidSubmissionFileIndex.json?raw'
import { ComparisonFactory } from '../../src'
import { MetricJsonIdentifier, SubmissionFile } from '@jplag/model'

describe('Test JSON to Comparison', () => {
  it('Post 5.0', async () => {
    const filesOfFirst: SubmissionFile[] = [
      {
        fileName: 'root1/Structure.java',
        data: '',
        submissionId: 'root1'
      },
      {
        fileName: 'root1/Submission.java',
        data: '',
        submissionId: 'root1'
      }
    ]
    const filesOfSecond: SubmissionFile[] = [
      {
        fileName: 'root2/Structure.java',
        data: '',
        submissionId: 'root2'
      },
      {
        fileName: 'root2/Submission.java',
        data: '',
        submissionId: 'root2'
      }
    ]

    const result = ComparisonFactory.getComparison(
      validNew,
      submissionFileIndex,
      filesOfFirst,
      filesOfSecond
    )
    const comparison = result.comparison

    expect(result).toBeDefined()
    expect(comparison.firstSubmissionId).toBe('root1')
    expect(comparison.secondSubmissionId).toBe('root2')
    expect(comparison.similarities[MetricJsonIdentifier.AVERAGE_SIMILARITY]).toBe(0.45)
    expect(comparison.similarities[MetricJsonIdentifier.MAXIMUM_SIMILARITY]).toBe(0.5)
    expect(comparison.similarities[MetricJsonIdentifier.LONGEST_MATCH]).toBe(139)
    expect(comparison.similarities[MetricJsonIdentifier.MAXIMUM_LENGTH]).toBe(462)

    expect(comparison.filesOfFirstSubmission).toBeDefined()
    expect(comparison.filesOfSecondSubmission).toBeDefined()
    expect(comparison.allMatches.length).toBe(4)
    expect(comparison.matchesInFirstSubmission.size).toBe(2)
    expect(comparison.matchesInSecondSubmissions.size).toBe(2)
    expect(comparison.firstSimilarity).toBe(0.4)
    expect(comparison.secondSimilarity).toBe(0.5)
  })
})
