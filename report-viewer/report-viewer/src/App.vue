<template>
  <div :class="{ dark: uiStore().useDarkMode }">
    <div
      class="bg-background-light dark:bg-background-dark flex max-h-screen min-h-screen max-w-screen flex-col overflow-scroll text-black dark:text-amber-50 print:max-h-none print:w-full print:max-w-full print:overflow-visible"
    >
      <RouterView
        class="print:min-h-none min-h-screen w-screen p-2 pb-0! md:h-screen md:max-h-screen md:p-5 print:max-h-none print:w-full print:overflow-visible print:p-0"
      />

      <ButtonComponent
        class="fixed right-2 bottom-2 flex h-12 w-12 items-center justify-center text-center print:hidden"
        @click="uiStore().useDarkMode = !uiStore().useDarkMode"
      >
        <FontAwesomeIcon class="text-2xl" :icon="uiStore().useDarkMode ? faSun : faMoon" />
      </ButtonComponent>
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
import { RouterView } from 'vue-router'
import { ButtonComponent, ToastComponent } from '@jplag/ui-components/base'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faMoon, faSun } from '@fortawesome/free-solid-svg-icons'
import { computed, ref } from 'vue'
import { uiStore } from './stores/uiStore'
import { Version } from '@jplag/model'
import { reportViewerVersion } from './version/versions'

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
