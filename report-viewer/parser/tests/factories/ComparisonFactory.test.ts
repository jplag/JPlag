import { it, describe, expect } from 'vitest'
import validNew from './assets/ValidComparison.json?raw'
import submissionFileIndex from './assets/ValidSubmissionFileIndex.json?raw'
import { ComparisonFactory } from '../../src'
import { MetricJsonIdentifier, SubmissionFile } from '@jplag/model'

describe('Test JSON to Comparison', () => {
  it('Post 5.0', () => {
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

    const comparison = ComparisonFactory.getComparison(
      validNew,
      submissionFileIndex,
      filesOfFirst,
      filesOfSecond
    )

    expect(comparison).toBeDefined()
    expect(comparison.firstSubmissionId).toBe('root1')
    expect(comparison.secondSubmissionId).toBe('root2')
    expect(comparison.similarities[MetricJsonIdentifier.AVERAGE_SIMILARITY]).toBe(0.45)
    expect(comparison.similarities[MetricJsonIdentifier.MAXIMUM_SIMILARITY]).toBe(0.5)
    expect(comparison.similarities[MetricJsonIdentifier.LONGEST_MATCH]).toBe(139)
    expect(comparison.similarities[MetricJsonIdentifier.MAXIMUM_LENGTH]).toBe(462)

    expect(comparison.allMatches.length).toBe(4)
    expect(comparison.matchesInFirstSubmission.size).toBe(2)
    expect(comparison.matchesInSecondSubmissions.size).toBe(2)
    expect(comparison.firstSimilarity).toBe(0.4)
    expect(comparison.secondSimilarity).toBe(0.5)

    expect(comparison.filesOfFirstSubmission).toBeDefined()
    expect(comparison.filesOfSecondSubmission).toBeDefined()
    const root1Submission = comparison.filesOfFirstSubmission.find(
      (file) => file.fileName === 'root1/Submission.java'
    )
    const root2Submission = comparison.filesOfSecondSubmission.find(
      (file) => file.fileName === 'root2/Submission.java'
    )
    const root1Structure = comparison.filesOfFirstSubmission.find(
      (file) => file.fileName === 'root1/Structure.java'
    )
    const root2Structure = comparison.filesOfSecondSubmission.find(
      (file) => file.fileName === 'root2/Structure.java'
    )
    expect(root1Submission).toBeDefined()
    expect(root2Submission).toBeDefined()
    expect(root1Structure).toBeDefined()
    expect(root2Structure).toBeDefined()

    expect(root1Structure!.tokenCount).toBe(139)
    expect(root1Structure!.tokenCount).toBe(139)
    expect(root1Structure!.matchedTokenCount).toBe(139)
    expect(root2Structure!.matchedTokenCount).toBe(138)

    expect(root1Submission!.tokenCount).toBe(100)
    expect(root2Submission!.tokenCount).toBe(100)
    expect(root1Submission!.matchedTokenCount).toBe(90)
    expect(root2Submission!.matchedTokenCount).toBe(89)
  })
})
