<template>
  <div :class="{ dark: false }">
    <div
      class="bg-background text-font flex max-h-screen min-h-screen max-w-screen flex-col overflow-hidden print:max-h-none print:w-full print:max-w-full print:overflow-visible"
    >
      <div
        class=" grid min-h-screen w-screen grid-cols-[auto_1fr] overflow-hidden h-screen max-h-screen"
      >
        <SideBar class="col-start-1" />

        <div class="col-start-2 p-5 max-h-full h-full overflow-hidden">
          <div class="relative h-full overflow-hidden max-w-full w-full">
            <div v-if="configState().loading" class="h-full w-full flex items-center justify-center">
              <LoadingCircle message="Loading..." />
            </div>
            <StartView v-else-if="configState().getStepIdx() === 0" @start="start()" />
            <SubmissionView
              v-else-if="configState().getStepIdx() === 1"
              v-model="configState().submissionsState"
            />
            <LanguageView v-else-if="configState().getStepIdx() === 2" v-model="configState().languageState" />
            <ComparisonOptionsView
              v-else-if="configState().getStepIdx() === 3"
              v-model="configState().comparisonOptionsState"
            />
            <ResultFileOptionsView v-else-if="configState().getStepIdx() === 4" v-model="configState().resultFileOptionsState" />
            <ResultView v-else-if="configState().getStepIdx() === 5" class="h-full w-full" />

            <InteractableComponent
              v-if="
                configState().getStepIdx() !== 0 &&
                configState().canChooseStep(configState().getStepIdx() + 1) &&
                !configState().loading
              "
              class="border-accent-dark! bg-accent-light/25! absolute right-0 bottom-0 select-none"
              @click="configState().setStep(configState().getStepIdx() + 1)"
              ><FontAwesomeIcon :icon="faForwardStep" /> Next</InteractableComponent
            >
          </div>
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
import { ToastComponent, InteractableComponent } from '@jplag/ui-components/base'
import { computed, ref } from 'vue'
import { Version } from '@jplag/model'
import { configState } from './state'
import StartView from './views/StartView.vue'
import SubmissionView from './views/SubmissionView.vue'
import LanguageView from './views/LanguageView.vue'
import ComparisonOptionsView from './views/ComparisonOptionsView.vue'
import ResultFileOptionsView from './views/ResultFileOptionsView.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faForwardStep } from '@fortawesome/free-solid-svg-icons'
import ResultView from './views/ResultView.vue'
import LoadingCircle from '@jplag/ui-components/base/LoadingCircle.vue'
import SideBar from './components/SideBar.vue'
import { router } from '@jplag/report-viewer'

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

//router.push('OverviewView')

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

function start() {
  configState().startState.clickedButton = true
  configState().setStep(1)
}
</script>
