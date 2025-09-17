import { beforeEach, describe, expect, it, vi } from 'vitest'
import {
  mockFiles,
  mockParser,
  mockSessionStorage,
  registerMockRouter,
  submissionFiles
} from './mocks'

registerMockRouter()
mockSessionStorage()
mockParser()

import { reportStore } from '../../../src/stores/reportStore'
import { setActivePinia, createPinia } from 'pinia'
import { router } from '../../../src/router'

describe('Test Report File Handling', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    reportStore().reset()
  })

  it('Get parsed file content', () => {
    reportStore().loadReport(mockFiles, [], 'test')

    expect(reportStore().getAllClusters()).toBeDefined()
    expect(reportStore().getAllClusters().length).toBeGreaterThan(0)
    expect(reportStore().getDistributions()).toBeDefined()
    expect(reportStore().getCliOptions()).toBeDefined()
    expect(reportStore().getRunInformation()).toBeDefined()
    expect(reportStore().getTopComparisons()).toBeDefined()
    expect(reportStore().getTopComparisons().length).toBeGreaterThan(0)
  })

  it('Check loaded report and reset', () => {
    expect(reportStore().isReportLoaded()).toEqual(false)

    reportStore().loadReport(mockFiles, [], 'test')
    expect(reportStore().isReportLoaded()).toEqual(true)
    expect(reportStore().getReportFileName()).toEqual('test')

    reportStore().reset()
    expect(reportStore().isReportLoaded()).toEqual(false)

    reportStore().loadReport(mockFiles, [], 'test2')
    expect(reportStore().isReportLoaded()).toEqual(true)
    expect(reportStore().getReportFileName()).toEqual('test2')
  })

  it('Test calculated statistics', () => {
    reportStore().loadReport(mockFiles, [], 'test')

    expect(reportStore().getSubmissionCount()).toEqual(3)
    expect(reportStore().includedComparisonCount()).toEqual(3)
  })

  it('Test submission id extraction', () => {
    reportStore().loadReport(mockFiles, [], 'test')

    expect(reportStore().getSubmissionIds().length).toEqual(3)
    expect(reportStore().getSubmissionIds()).toContain('test1')
    expect(reportStore().getSubmissionIds()).toContain('test2')
    expect(reportStore().getSubmissionIds()).toContain('test3')
  })

  it('Test comparison', () => {
    reportStore().loadReport(mockFiles, submissionFiles, 'test')

    const comparison = reportStore().getComparison('test1', 'test2')

    expect(comparison).toBeDefined()
    expect(comparison.firstSubmissionId).toEqual('test1')
    expect(comparison.secondSubmissionId).toEqual('test2')
    expect(comparison.similarities).toBeDefined()
    expect(comparison.matchesInFirstSubmission.size).toBeGreaterThan(0)
    expect(comparison.matchesInSecondSubmissions.size).toBeGreaterThan(0)
    expect(comparison.allMatches.length).toBeGreaterThan(0)
    expect(comparison.filesOfFirstSubmission.length).toBeGreaterThan(0)
    expect(comparison.filesOfSecondSubmission.length).toBeGreaterThan(0)

    const filesOfFirstSubmission = comparison.filesOfFirstSubmission
    const filesOfSecondSubmission = comparison.filesOfSecondSubmission
    expect(filesOfFirstSubmission.length).toEqual(2)
    expect(filesOfSecondSubmission.length).toEqual(2)

    const test1SubmissionJava = filesOfFirstSubmission.find(
      (file) => file.fileName === 'test1/Submission.java'
    )
    const test1StructureJava = filesOfFirstSubmission.find(
      (file) => file.fileName === 'test1/Structure.java'
    )
    const test2SubmissionJava = filesOfSecondSubmission.find(
      (file) => file.fileName === 'test2/Submission.java'
    )
    const test2StructureJava = filesOfSecondSubmission.find(
      (file) => file.fileName === 'test2/Structure.java'
    )
    expect(test1SubmissionJava).toBeDefined()
    expect(test1StructureJava).toBeDefined()
    expect(test2SubmissionJava).toBeDefined()
    expect(test2StructureJava).toBeDefined()

    expect(test1StructureJava?.tokenCount).toEqual(139)
    expect(test2StructureJava?.tokenCount).toEqual(139)
    expect(test1SubmissionJava?.tokenCount).toEqual(100)
    expect(test2SubmissionJava?.tokenCount).toEqual(100)

    expect(test1StructureJava?.matchedTokenCount).toEqual(139)
    expect(test2StructureJava?.matchedTokenCount).toEqual(138)
    expect(test1SubmissionJava?.matchedTokenCount).toEqual(90)
    expect(test2SubmissionJava?.matchedTokenCount).toEqual(85)
  })

  it('Test old version in runInformation', () => {
    reportStore().loadReport(
      [
        {
          fileName: 'runInformation.json',
          data: buildOldVersionInfo('version', 3, 0, 0)
        }
      ],
      [],
      'test'
    )

    expect(reportStore().isReportLoaded()).toEqual(false)
    expect(router.push).toHaveBeenCalled()
    expect(router.push).toHaveBeenCalledWith({
      name: 'OldVersionRedirectView',
      params: { version: '3.0.0' }
    })
  })

  it('Test old version in overview', () => {
    reportStore().loadReport(
      [
        {
          fileName: 'overview.json',
          data: buildOldVersionInfo('jplag_version', 2, 0, 0)
        }
      ],
      [],
      'test'
    )

    expect(reportStore().isReportLoaded()).toEqual(false)
    expect(router.push).toHaveBeenCalled()
    expect(router.push).toHaveBeenCalledWith({
      name: 'OldVersionRedirectView',
      params: { version: '2.0.0' }
    })
  })
})

function buildOldVersionInfo(
  fieldName: string,
  major: number,
  minor: number,
  patch: number
): string {
  const version = { major, minor, patch }
  return `{"${fieldName}": ${JSON.stringify(version)}}`
}
