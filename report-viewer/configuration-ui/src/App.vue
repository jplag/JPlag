<template>
  <div :class="{ dark: false }">
    <div
      class="bg-background text-font flex max-h-screen min-h-screen max-w-screen flex-col overflow-hidden print:max-h-none print:w-full print:max-w-full print:overflow-visible"
    >
      <div
        class="print:min-h-none grid min-h-screen w-screen grid-cols-[auto_1fr] overflow-hidden md:h-screen md:max-h-screen print:max-h-none print:w-full print:overflow-visible print:p-0"
      >
        <div
          class="border-container-border col-start-1 box-border flex flex-col gap-5 border-r p-2 shadow-sm"
        >
          <StepIndicator
            v-for="[idx, step] in STEPS.entries()"
            :key="idx"
            :class="configState().canChooseStep(idx) ? 'cursor-pointer' : 'cursor-not-allowed'"
            :title="step"
            :description="'Lorem ipsum'"
            :state="configState().getState(idx)"
            :validity="configState().getValidity(idx)"
            @click="configState().setStep(idx)"
          />
        </div>
        <div class="col-start-2 p-5">
          <TempModifier
            v-if="configState().getStepIdx() == 0"
            v-model="configState().submissionsState.test"
          />
          <TempModifier
            v-if="configState().getStepIdx() == 1"
            v-model="configState().languageState.test"
          />
          <TempModifier
            v-if="configState().getStepIdx() == 2"
            v-model="configState().comparisonOptionsState.test"
          />
          <TempModifier
            v-if="configState().getStepIdx() == 3"
            v-model="configState().resultFileOptionsState.test"
          />
          <div v-if="configState().getStepIdx() == 4">Super Tolle Ergebnisse</div>
        </div>
      </div>
      <ToastComponent v-if="showToast" :time-to-live="10000">
        You are using an outdated version of the JPlag Report Viewer ({{
          reportViewerVersion.toString()
        }}).<br />
        Version {{ newestVersion.toString() }} is available on
        <a href="https://github.com/jplag/JPlag/releases/latest" class="text-link underline"
          >GitHub</a
        >.
      </ToastComponent>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ToastComponent } from '@jplag/ui-components/base'
import { computed, ref } from 'vue'
import { Version } from '@jplag/model'
import StepIndicator from './components/StepIndicator.vue'
import { STEPS } from './model/Steps'
import { configState } from './state'
import TempModifier from './components/TempModifier.vue'

const reportViewerVersion = new Version(0, 0, 0)
const newestVersion = ref(Version.ERROR_VERSION)
const isDemo = import.meta.env.MODE == 'demo'
const hasShownToast = ref(sessionStorage.getItem('hasShownToast') == 'true')

const showToast = computed(() => {
  const value =
    !isDemo &&
    !newestVersion.value.isInvalid() &&
    !reportViewerVersion.isDevVersion() &&
    newestVersion.value.compareTo(reportViewerVersion) > 0 &&
    !hasShownToast.value

  if (value) {
    sessionStorage.setItem('hasShownToast', 'true')
  } else {
    sessionStorage.removeItem('hasShownToast')
  }

  return value
})

fetch('https://api.github.com/repos/jplag/JPlag/releases/latest')
  .then((response) => response.json())
  .then((data) => {
    const versionString = data.tag_name
    // remove the 'v' from the version string and split it into an array
    const versionArray = versionString.substring(1).split('.')
    newestVersion.value = new Version(
      parseInt(versionArray[0]),
      parseInt(versionArray[1]),
      parseInt(versionArray[2])
    )
  })
  .catch(() => {
    newestVersion.value = Version.ERROR_VERSION
  })
</script>
