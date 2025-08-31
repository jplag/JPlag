import {
  CliOptions,
  Cluster,
  Comparison,
  ComparisonListElement,
  ComparisonSubmissionFile,
  Distribution,
  DistributionMap,
  File,
  Match,
  MetricJsonIdentifier,
  ParserLanguage,
  RunInformation,
  SubmissionFile,
  SubmissionState,
  Version
} from '@jplag/model'
import { vi } from 'vitest'

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

export function mockParser() {
  vi.mock('@jplag/parser', () => ({
    BaseCodeReportFactory: {
      getReport: vi.fn().mockReturnValue([])
    },
    ClusterFactory: {
      getClusters: vi.fn().mockReturnValue(mockClusters)
    },
    ComparisonFactory: {
      getComparison: vi.fn().mockReturnValue(mockComparison)
    },
    DistributionFactory: {
      getDistributions: vi.fn().mockReturnValue(mockDistributions)
    },
    OptionsFactory: {
      getCliOptions: vi.fn().mockReturnValue(mockOptions)
    },
    RunInformationFactory: {
      getRunInformation: vi.fn().mockReturnValue(mockRunInformation)
    },
    SubmissionMappingsFactory: {
      getSubmissionMappings: vi.fn().mockReturnValue(mockSubmissionMappings)
    },
    TopComparisonFactory: {
      getTopComparisons: vi.fn().mockReturnValue(mockTopComparisons)
    }
  }))
}

const mockClusters: Cluster[] = [
  {
    index: 0,
    averageSimilarity: 0.45,
    strength: 0.00232302,
    members: ['test1', 'test2', 'test3']
  }
]
const mockDistributions: DistributionMap = {
  [MetricJsonIdentifier.AVERAGE_SIMILARITY]: new Distribution([]),
  [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: new Distribution([])
}
const mockOptions: CliOptions = {
  language: ParserLanguage.JAVA,
  minimumTokenMatch: 9,
  submissionDirectories: ['.\\files'],
  oldSubmissionDirectories: ['old'],
  baseCodeSubmissionDirectory: '.',
  subdirectoryName: 'src/',
  fileSuffixes: ['.java', '.JAVA'],
  exclusionFileName: 'ex.txt',
  similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
  similarityThreshold: 0.0,
  maximumNumberOfComparisons: 500,
  debugParser: false,
  analyzeComments: false,
  normalize: false,
  clusteringOptions: {
    enabled: true,
    similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
    spectralKernelBandwidth: 20.0,
    spectralGaussianProcessVariance: 0.0025000000000000005,
    spectralMinRuns: 5,
    spectralMaxRuns: 50,
    spectralMaxKMeansIterationPerRun: 200,
    agglomerativeThreshold: 0.2,
    preprocessor: 'CUMULATIVE_DISTRIBUTION_FUNCTION',
    algorithm: 'SPECTRAL',
    agglomerativeInterClusterSimilarity: 'AVERAGE',
    preprocessorThreshold: 0.2,
    preprocessorPercentile: 0.5
  },
  mergingOptions: {
    enabled: false,
    minimumNeighborLength: 0,
    maximumGapSize: 0,
    minimumRequiredMerges: 3
  }
}
const mockRunInformation = new RunInformation(
  new Version(6, 2, 0),
  [
    { submissionId: 'failed1', submissionState: SubmissionState.TOO_SMALL },
    { submissionId: 'failedTest', submissionState: SubmissionState.CANNOT_PARSE }
  ],
  '06/05/25',
  471,
  3
)
const mockSubmissionMappings = {
  idToDisplayNameMap: new Map<string, string>([
    ['test1', 'Test1'],
    ['test2', 'Test2'],
    ['test3', 'Test3']
  ]),
  comparisonFilesLookup: new Map<string, Map<string, string>>([
    [
      'test1',
      new Map<string, string>([
        ['test2', 'test1-test2.json'],
        ['test3', 'test1-test3.json']
      ])
    ],
    [
      'test2',
      new Map<string, string>([
        ['test1', 'test1-test2.json'],
        ['test3', 'test2-test3.json']
      ])
    ],
    [
      'test3',
      new Map<string, string>([
        ['test1', 'test1-test3.json'],
        ['test2', 'test2-test3.json']
      ])
    ]
  ])
}
const mockTopComparisons: ComparisonListElement[] = [
  buildMockTopComparison(1, 'test1', 'test2', 0.41, 0.81, 29, 35),
  buildMockTopComparison(2, 'test1', 'test3', 0.22, 0.34, 15, 40),
  buildMockTopComparison(3, 'test2', 'test3', 0.21, 0.25, 10, 50)
]
const mockComparison: Comparison = new Comparison(
  'test1',
  'test2',
  buildSimilarityMap(0.41, 0.81, 29, 35),
  [
    buildSubmissionFile('test1/Submission.java', 'test1', 100, 90),
    buildSubmissionFile('test1/Structure.java', 'test1', 139, 139)
  ],
  [
    buildSubmissionFile('test2/Submission.java', 'test2', 100, 85),
    buildSubmissionFile('test2/Structure.java', 'test2', 139, 138)
  ],
  [
    buildMatch(
      'test1/Structure.java',
      'test2/Structure.java',
      1,
      1,
      0,
      3,
      16,
      139,
      1,
      1,
      0,
      3,
      16,
      138,
      139,
      138,
      0
    ),
    buildMatch(
      'test1/Submission.java',
      'test2/Submission.java',
      129,
      1,
      200,
      155,
      89,
      234,
      134,
      1,
      205,
      160,
      89,
      234,
      34,
      29,
      1
    ),
    buildMatch(
      'test1/Submission.java',
      'test2/Submission.java',
      165,
      1,
      240,
      194,
      89,
      273,
      173,
      1,
      248,
      203,
      89,
      281,
      33,
      33,
      2
    ),
    buildMatch(
      'test1/Submission.java',
      'test2/Submission.java',
      112,
      1,
      101,
      127,
      89,
      124,
      116,
      1,
      105,
      133,
      89,
      128,
      23,
      23,
      1
    )
  ],
  0.81,
  0.11
)

function buildMatch(
  file1: string,
  file2: string,
  startLine1: number,
  startColumn1: number,
  startIndex1: number,
  endLine1: number,
  endColumn1: number,
  endIndex1: number,
  startLine2: number,
  startColumn2: number,
  startIndex2: number,
  endLine2: number,
  endColumn2: number,
  endIndex2: number,
  length1: number,
  length2: number,
  color: number
): Match {
  return {
    colorIndex: color,
    firstFileName: file1,
    secondFileName: file2,
    startInFirst: {
      line: startLine1,
      column: startColumn1,
      tokenListIndex: startIndex1
    },
    endInFirst: {
      line: endLine1,
      column: endColumn1,
      tokenListIndex: endIndex1
    },
    startInSecond: {
      line: startLine2,
      column: startColumn2,
      tokenListIndex: startIndex2
    },
    endInSecond: {
      line: endLine2,
      column: endColumn2,
      tokenListIndex: endIndex2
    },
    lengthOfFirst: length1,
    lengthOfSecond: length2
  }
}

function buildSubmissionFile(
  name: string,
  id: string,
  tokens: number,
  matchedTokens: number
): ComparisonSubmissionFile {
  return {
    fileName: name,
    submissionId: id,
    tokenCount: tokens,
    matchedTokenCount: matchedTokens,
    data: '',
    displayFileName: name
  }
}

function buildSimilarityMap(
  avg: number,
  max: number,
  long: number,
  len: number
): Record<MetricJsonIdentifier, number> {
  return {
    [MetricJsonIdentifier.AVERAGE_SIMILARITY]: avg,
    [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: max,
    [MetricJsonIdentifier.LONGEST_MATCH]: long,
    [MetricJsonIdentifier.MAXIMUM_LENGTH]: len
  }
}

function buildMockTopComparison(
  id: number,
  first: string,
  second: string,
  avg: number,
  max: number,
  long: number,
  len: number
): ComparisonListElement {
  return {
    id: id,
    sortingPlace: id,
    firstSubmissionId: first,
    secondSubmissionId: second,
    similarities: buildSimilarityMap(avg, max, long, len)
  }
}
export const mockFiles: File[] = [
  {
    fileName: 'cluster.json',
    data: ''
  },
  {
    fileName: 'distribution.json',
    data: ''
  },
  {
    fileName: 'options.json',
    data: ''
  },
  {
    fileName: 'runInformation.json',
    // the version check uses the raw string, so we need a version here
    data: '{"version":{"major":6,"minor":2,"patch":0}}'
  },
  {
    fileName: 'submissionMappings.json',
    data: ''
  },
  {
    fileName: 'topComparisons.json',
    data: ''
  },

  {
    fileName: 'basecode/test1.json',
    data: ''
  },
  {
    fileName: 'basecode/test2.json',
    data: ''
  },
  {
    fileName: 'basecode/test3.json',
    data: ''
  },

  {
    fileName: 'submissionFileIndex.json',
    data: ''
  },
  {
    fileName: 'comparisons/test1-test2.json',
    data: ''
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
