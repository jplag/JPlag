<template>
  <div :class="{ dark: false }">
    <div
      class="bg-background flex max-h-screen min-h-screen max-w-screen flex-col overflow-scroll text-font print:max-h-none print:w-full print:max-w-full print:overflow-visible"
    >
      <div
        class="print:min-h-none min-h-screen w-screen md:h-screen md:max-h-screen print:max-h-none print:w-full print:overflow-visible print:p-0 grid grid-cols-[auto_1fr]"
      >
        <div class="col-start-1 flex flex-col gap-5 border-r border-container-border shadow-sm box-border p-2">
          <StepIndicator 
            v-for="[idx, step] in steps.entries()"
            :key="idx"
            :title="step"
            :description="'Lorem ipsum'"
            :state="getState(idx)"
          />
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

const reportViewerVersion = new Version(0,0,0)
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

const selectedIndex = ref(2)
const steps = ['Submissions', 'Language', 'Comparison Options', 'Result File Options', 'Results']
function getState(i: number): 'done'|'active'|'future' {
  if (i === selectedIndex.value) return 'active'
  if (i < selectedIndex.value) return 'done'
  return 'future'
}
</script>
