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

      <RepositoryReference class="absolute bottom-1 left-5" />
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
import RepositoryReference from './components/RepositoryReference.vue'

library.add(faMoon)
library.add(faSun)
</script>
