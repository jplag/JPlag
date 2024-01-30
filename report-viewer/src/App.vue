<template>
  <div :class="{ dark: store().uiState.useDarkMode }">
    <div
      class="max-w-screen max-h-fit min-h-screen bg-backgorund-light text-black dark:bg-backgorund-dark dark:text-amber-50"
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
      <div class="absolute bottom-1 left-5 text-xs text-black dark:text-white print:hidden">
        Bug reports and feature requests can be submitted on
        <a
          href="https://github.com/jplag/JPlag/issues"
          class="text-link-dark underline dark:text-link"
          ><FontAwesomeIcon :icon="faGithub" /> GitHub</a
        >
      </div>
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
import { faGithub } from '@fortawesome/free-brands-svg-icons'

library.add(faMoon)
library.add(faSun)
</script>
