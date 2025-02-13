<template>
  <div :class="{ dark: store().uiState.useDarkMode }">
    <div
      class="max-w-screen max-h-fit min-h-screen bg-background-light text-black dark:bg-background-dark dark:text-amber-50"
    >
      <RouterView class="max-h-screen overflow-hidden print:max-h-none print:overflow-visible" />

      <Button
        class="absolute bottom-2 right-2 flex h-12 w-12 items-center justify-center text-center print:hidden"
        @click="store().changeUseDarkMode()"
      >
        <FontAwesomeIcon
          class="text-2xl"
          :icon="store().uiState.useDarkMode ? ['fas', 'sun'] : ['fas', 'moon']"
        />
      </Button>
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
import Button from './components/ButtonComponent.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faMoon, faSun } from '@fortawesome/free-solid-svg-icons'
import { store } from './stores/store'
import ToastComponent from './components/ToastComponent.vue'
import { Version, reportViewerVersion } from './model/Version'
import { computed, ref } from 'vue'

library.add(faMoon)
library.add(faSun)

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
