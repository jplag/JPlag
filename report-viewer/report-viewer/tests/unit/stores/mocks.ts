import { File } from '@jplag/model'
import { vi } from 'vitest'
import clusterText from './data/cluster.json?raw'
import distributionText from './data/distribution.json?raw'
import optionsText from './data/options.json?raw'
import runInformationText from './data/runInformation.json?raw'
import submissionMappingsText from './data/submissionMappings.json?raw'
import topComparisonsText from './data/topComparisons.json?raw'

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
  }
]
