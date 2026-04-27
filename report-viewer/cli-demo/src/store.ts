import { defineStore } from "pinia";

export const store = defineStore('cliStore', () => {
  const cliOptions = ref<ExpandedOptions>({
    resultFileName: 'results.jplag',
    minimumTokenMatch: {[ParserLanguage.JAVA]: 9, [ParserLanguage.PYTHON]: 12} as Record<ParserLanguage, number>,
    language: [],
    submissionDirectories: [],
    oldSubmissionDirectories: [],
    baseCodeSubmissionDirectory: '',
    subdirectoryName: '',
    fileSuffixes: {[ParserLanguage.JAVA]: '.java', [ParserLanguage.PYTHON]: '.py'} as Record<ParserLanguage, string>,
    exclusionFileName: '',
    similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
    similarityThreshold: 0,
    maximumNumberOfComparisons: 2500,
    debugParser: false,
    normalize: false,
    analyzeComments: false,
    overwriteResultFile: false,
    generateCsvFile: false,
    clusteringOptions: {
      enabled: true,
      similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
      spectralKernelBandwidth: 0,
      spectralGaussianProcessVariance: 0,
      spectralMinRuns: 0,
      spectralMaxRuns: 0,
      spectralMaxKMeansIterationPerRun: 0,
      agglomerativeThreshold: 0,
      preprocessor: '',
      algorithm: '',
      agglomerativeInterClusterSimilarity: '',
      preprocessorThreshold: 0,
      preprocessorPercentile: 0
    },
    mergingOptions: {
      enabled: false,
      minimumNeighborLength: 5,
      maximumGapSize: 2,
      minimumRequiredMerges: 7
    },
    frequencyAnalysisOptions: {
      enabled: false,
      analysisStrategy: 'string',
    weightingFunction: 'string',
    weightingFactor: -1
    }
  })

  return {cliOptions}
  
});

import { CliOptions, MetricJsonIdentifier, ParserLanguage } from '@jplag/model'
import { ref } from "vue";

export type MinimumTokenMatch = number | 'default'

interface _ExpandedOptions {
  resultFileName: string
  language: ParserLanguage[]
  minimumTokenMatch: Record<ParserLanguage, number>
  fileSuffixes: Record<ParserLanguage, string>
  overwriteResultFile: boolean
  generateCsvFile: boolean
}

export type ExpandedOptions = Omit<CliOptions, 'minimumTokenMatch' | 'language' | 'fileSuffixes'> & _ExpandedOptions