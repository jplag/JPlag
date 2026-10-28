import { defineStore } from 'pinia'
import {
  ComparsionOptionsState,
  LanguageState,
  ResultFileOptionsState,
  StartState,
  SubmissionsState
} from './model/Steps'
import { computed, ref, watch } from 'vue'
import { MetricJsonIdentifier, ParserLanguage } from '@jplag/model'
import { ParsingResponse } from './model/Responses'

export const configState = defineStore('configStore', () => {
  const startState = ref<StartState>({ clickedButton: false })
  const submissionsState = ref<SubmissionsState>({ newSubmissions: [], oldSubmissions: [] })
  const languageState = ref<LanguageState>({ selectedLanguages: [] })
  const comparisonOptionsState = ref<ComparsionOptionsState>({
    clustering: {
      enabled: true,
      similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
      spectralKernelBandwidth: 1,
      spectralGaussianProcessVariance: 1,
      spectralMinRuns: 1,
      spectralMaxRuns: 1,
      spectralMaxKMeansIterationPerRun: 1,
      agglomerativeThreshold: 1,
      preprocessor: 'ma,e',
      algorithm: 'string',
      agglomerativeInterClusterSimilarity: 'string',
      preprocessorThreshold: 1,
      preprocessorPercentile: 1
    },
    merging: {
      enabled: false,
      minimumNeighborLength: 1,
      maximumGapSize: 1,
      minimumRequiredMerges: 1
    }
  })
  const resultFileOptionsState = ref<ResultFileOptionsState>({ 
    csvFile: false,
    overwrite: true,
    resultFileName: 'results.jplag',
    shownComparisons: 2000,
    similarityThreshold: 0
   })
  const currentStep = ref(0)
  const lastStateWithChanges = ref(0)
  const hadChanges = ref(false)
  const loading = ref(false)
  const parsingAnswer = ref<ParsingResponse|null>(null)

  const startStateValid = computed(() => {
    return startState.value.clickedButton
  })
  const submissionsStateValid = computed(() => {
    return submissionsState.value.newSubmissions.length > 0
  })
  const languageStateValid = computed(() => {
    return languageState.value.selectedLanguages.length > 0
  })
  const resultFileStateValid = computed(() => {
    return resultFileOptionsState.value.resultFileName !== ''
  })

  function getState(i: number): 'choosable' | 'active' | 'future' {
    if (i === currentStep.value) {
      return 'active'
    }
    return canChooseStep(i) ? 'choosable' : 'future'
  }

  function getValidity(i: number): boolean {
    if (i > lastStateWithChanges.value) return false
    if (i === 0) {
      return startStateValid.value
    }
    if (i === 1) {
      return submissionsStateValid.value
    }
    if (i === 2) {
      return languageStateValid.value
    }
    if (i === 3) {
      return true
    }
    if (i === 4) {
      return resultFileStateValid.value
    }
    return false
  }

  function canChooseStep(step: number): boolean {
    if (step === currentStep.value) {
      return false
    }
    if (step < currentStep.value) return true
    for (let s = currentStep.value; s < step; s++) {
      if (!getValidity(s)) return false
    }
    return true
  }

  function setStep(step: number) {
    if (!canChooseStep(step)) return
    if (loading.value) return
    if (lastStateWithChanges.value < step) lastStateWithChanges.value = step

    currentStep.value = step
    if (hadChanges.value) {
      loading.value = true
      if (step === 2) {
        execute<SubmissionsState, ParsingResponse>('submissions', submissionsState.value).then((r) => {
          parsingAnswer.value = r
          languageState.value.selectedLanguages = Object.keys(r.detectedLanguages) as ParserLanguage[]
          loading.value = false
        })
      } else {
        loading.value = false
      }
    }
    hadChanges.value = false
  }

  function getStepIdx(): number {
    return currentStep.value
  }

  watch(
    () => submissionsState,
    () => {
      lastStateWithChanges.value = 1
      hadChanges.value = true
    },
    { deep: true }
  )
  watch(
    () => languageState,
    () => {
      lastStateWithChanges.value = 2
      hadChanges.value = true
    },
    { deep: true }
  )
  watch(
    () => comparisonOptionsState,
    () => {
      lastStateWithChanges.value = 3
      hadChanges.value = true
    },
    { deep: true }
  )
  watch(
    () => resultFileOptionsState,
    () => {
      lastStateWithChanges.value = 4
      hadChanges.value = true
    },
    { deep: true }
  )

  return {
    getState,
    setStep,
    startState,
    submissionsState,
    languageState,
    comparisonOptionsState,
    resultFileOptionsState,
    getStepIdx,
    getValidity,
    canChooseStep,
    loading,
    parsingAnswer
  }
})

async function execute<I, O>(path: string, data: I): Promise<O> {
  const url = `${window.location.origin}${import.meta.env.BASE_URL}${path}`
  const response = await fetch(url, {method: 'POST', body: JSON.stringify(data)})
  if (!response.ok) {
    throw new Error(await response.text())
  }
  return response.json()
}