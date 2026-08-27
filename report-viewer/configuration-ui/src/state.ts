import { defineStore } from 'pinia'
import {
  ComparsionOptionsState,
  LanguageState,
  ResultFileOptionsState,
  SubmissionsState
} from './model/Steps'
import { ref, watch } from 'vue'

export const configState = defineStore('reportStore', () => {
  const submissionsState = ref<SubmissionsState>({ test: '' })
  const languageState = ref<LanguageState>({ test: '' })
  const comparisonOptionsState = ref<ComparsionOptionsState>({ test: '' })
  const resultFileOptionsState = ref<ResultFileOptionsState>({ test: '' })
  const validStates = ref([false, false, false, false, false])
  const currentStep = ref(0)

  function getState(i: number): 'choosable' | 'active' | 'future' {
    if (i === currentStep.value) {
      return 'active'
    }
    return canChooseStep(i) ? 'choosable' : 'future'
  }

  function getValidity(i: number): boolean {
    return validStates.value[i]
  }

  function canChooseStep(step: number): boolean {
    if (step === currentStep.value) {
      return false
    }
    if (step < currentStep.value) return true
    for (let s = currentStep.value; s < step; s++) {
      if (!validStates.value[s]) return false
    }
    return true
  }

  function setStep(step: number) {
    if (!canChooseStep(step)) return

    // temp
    if (step === 4) {
      validStates.value[step] = true
    }
    currentStep.value = step
  }

  function getStepIdx(): number {
    return currentStep.value
  }

  const stateOrder = [
    submissionsState,
    languageState,
    comparisonOptionsState,
    resultFileOptionsState,
    ref({ test: '123' })
  ]

  for (let i = 0; i < stateOrder.length; i++) {
    watch(
      stateOrder[i],
      () => {
        validStates.value[currentStep.value] = stateOrder[i].value.test !== ''
        for (let j = i + 1; j < validStates.value.length; j++) {
          validStates.value[j] = false
        }
      },
      { deep: true }
    )
  }

  return {
    getState,
    setStep,
    submissionsState,
    languageState,
    comparisonOptionsState,
    resultFileOptionsState,
    getStepIdx,
    getValidity,
    canChooseStep
  }
})
