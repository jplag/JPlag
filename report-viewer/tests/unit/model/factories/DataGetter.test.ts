import { beforeEach, describe, it, expect, vi } from 'vitest'
import { DataGetter } from '@/model/factories/DataGetter'
import { DistributionFactory } from '@/model/factories/DistributionFactory'
import { ClusterFactory } from '@/model/factories/ClusterFactory'
import { RunInformationFactory } from '@/model/factories/RunInformationFactory'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import { ComparisonFactory } from '@/model/factories/ComparisonFactory'
import { TopComparisonFactory } from '@/model/factories/TopComparisonFactory'
import { BaseCodeReportFactory } from '@/model/factories/BaseCodeReportFactory'
import { SubmissionMappingsFactory } from '@/model/factories/SubmissionMappingsFactory'
import { setActivePinia, createPinia } from 'pinia'
import { store } from '@/stores/store'
import SubmissionMappings from './assets/ValidSubmissionMappings.json'

describe('Test DataGetter', async () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    store().setLoadingType('zip')
    store().state.files['submissionMappings.json'] = JSON.stringify(SubmissionMappings)

    vi.spyOn(DistributionFactory, 'getDistributions').mockReturnValue('mocked distributions')
    vi.spyOn(ClusterFactory, 'getClusters').mockReturnValue('mocked clusters')
    vi.spyOn(RunInformationFactory, 'getRunInformation').mockReturnValue('mocked run information')
    vi.spyOn(OptionsFactory, 'getCliOptions').mockReturnValue('mocked options')
    vi.spyOn(ComparisonFactory, 'getComparison').mockReturnValue('mocked comparison')
    vi.spyOn(TopComparisonFactory, 'getTopComparisons').mockReturnValue('mocked top comparison')
    vi.spyOn(BaseCodeReportFactory, 'getReport').mockReturnValue('mocked base code report')
    vi.spyOn(DataGetter, 'verifyVersion').mockReturnValue({ valid: true, version: undefined })
    vi.spyOn(SubmissionMappingsFactory, 'getSubmissionMappings')
  })

  it('Get all data', async () => {
    const data = await DataGetter.getFiles([
      'cluster',
      'distribution',
      'options',
      'runInformation',
      'topComparison',
      { type: 'baseCodeReport', submissionIds: ['Test123', 'Submission01'] },
      { type: 'comparison', firstSubmission: 'Test123', secondSubmission: 'Submission01' }
    ])

    expect(data.result).toBe('valid')
    expect(data.data).toEqual({
      cluster: 'mocked clusters',
      distribution: 'mocked distributions',
      options: 'mocked options',
      runInformation: 'mocked run information',
      topComparison: 'mocked top comparison',
      comparison: 'mocked comparison',
      baseCodeReport: ['mocked base code report', 'mocked base code report']
    })
  })

  it('dont get all data', async () => {
    const data = await DataGetter.getFiles([
      'cluster',
      { type: 'comparison', firstSubmission: 'Test123', secondSubmission: 'Submission01' }
    ])

    expect(data.result).toBe('valid')
    expect(data.data).toEqual({
      cluster: 'mocked clusters',
      comparison: 'mocked comparison'
    })
    expect(DistributionFactory.getDistributions).not.toHaveBeenCalled()
    expect(RunInformationFactory.getRunInformation).not.toHaveBeenCalled()
    expect(OptionsFactory.getCliOptions).not.toHaveBeenCalled()
    expect(TopComparisonFactory.getTopComparisons).not.toHaveBeenCalled()
    expect(BaseCodeReportFactory.getReport).not.toHaveBeenCalled()
    expect(ClusterFactory.getClusters).toHaveBeenCalled()
    expect(data.data.baseCodeReport).toBeUndefined()
    expect(data.data.runInformation).toBeUndefined()
    expect(data.data.options).toBeUndefined()
    expect(data.data.topComparison).toBeUndefined()
    expect(data.data.distribution).toBeUndefined()
    expect(data.data.comparison).toEqual('mocked comparison')
  })

  it('dont run mappings factory twice', async () => {
    await DataGetter.getFiles(['cluster'])
    expect(SubmissionMappingsFactory.getSubmissionMappings).toHaveBeenCalledTimes(1)
    await DataGetter.getFiles(['distribution'])
    expect(SubmissionMappingsFactory.getSubmissionMappings).toHaveBeenCalledTimes(1)
  })
})
