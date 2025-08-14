import { File, SubmissionFile } from '@jplag/model'
import { vi } from 'vitest'
import clusterText from './data/cluster.json?raw'
import distributionText from './data/distribution.json?raw'
import optionsText from './data/options.json?raw'
import runInformationText from './data/runInformation.json?raw'
import submissionMappingsText from './data/submissionMappings.json?raw'
import topComparisonsText from './data/topComparisons.json?raw'
import baseCode1Text from './data/basecode/test1.json?raw'
import baseCode2Text from './data/basecode/test2.json?raw'
import baseCode3Text from './data/basecode/test3.json?raw'
import comparison12Text from './data/comparison/test1-test2.json?raw'
import submissionFileIndexText from './data/submissionFileIndex.json?raw'

export function registerMockRouter() {
  vi.mock('../../../src/router', () => ({
    router: {
      push: vi.fn()
    }
  }))
}

export function mockSessionStorage() {
  vi.stubGlobal('sessionStorage', {
    getItem: vi.fn(),
    setItem: vi.fn(),
    removeItem: vi.fn(),
    clear: vi.fn()
  })
}

export const mockFiles: File[] = [
  {
    fileName: 'cluster.json',
    data: clusterText
  },
  {
    fileName: 'distribution.json',
    data: distributionText
  },
  {
    fileName: 'options.json',
    data: optionsText
  },
  {
    fileName: 'runInformation.json',
    data: runInformationText
  },
  {
    fileName: 'submissionMappings.json',
    data: submissionMappingsText
  },
  {
    fileName: 'topComparisons.json',
    data: topComparisonsText
  },

  {
    fileName: 'basecode/test1.json',
    data: baseCode1Text
  },
  {
    fileName: 'basecode/test2.json',
    data: baseCode2Text
  },
  {
    fileName: 'basecode/test3.json',
    data: baseCode3Text
  },

  {
    fileName: 'submissionFileIndex.json',
    data: submissionFileIndexText
  },
  {
    fileName: 'comparisons/test1-test2.json',
    data: comparison12Text
  }
]

export const submissionFiles: SubmissionFile[] = [
  {
    submissionId: 'test1',
    fileName: 'test1/Submission.java',
    data: ''
  },
  {
    submissionId: 'test1',
    fileName: 'test1/Structure.java',
    data: ''
  },
  {
    submissionId: 'test2',
    fileName: 'test2/Submission.java',
    data: ''
  },
  {
    submissionId: 'test2',
    fileName: 'test2/Structure.java',
    data: ''
  },
  {
    submissionId: 'test3',
    fileName: 'test3/Submission.java',
    data: ''
  },
  {
    submissionId: 'test3',
    fileName: 'test3/Structure.java',
    data: ''
  }
]
