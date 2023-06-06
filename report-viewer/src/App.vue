<template>
  <!--meta content="width=device-width, initial-scale=1, shrink-to-fit=no" name="viewport" /-->
  <div :class="{ dark: useDarkMode }">
    <div
      class="min-h-screen max-h-fit max-w-screen text-black dark:text-amber-50 bg-backgorund-light dark:bg-backgorund-dark"
    >
      <RouterView class="max-h-screen overflow-hidden" />
      <Button
        class="absolute right-2 bottom-2 w-12 text-center h-12 flex justify-center items-center"
        @click="toogleDarkMoode()"
      >
        <FontAwesomeIcon class="text-2xl" :icon="useDarkMode ? ['fas', 'sun'] : ['fas', 'moon']" />
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterView } from 'vue-router'
import Button from './components/ButtonComponent.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faMoon } from '@fortawesome/free-solid-svg-icons'
import { faSun } from '@fortawesome/free-solid-svg-icons'
import { useDarkMode } from './main'

library.add(faMoon)
library.add(faSun)

function toogleDarkMoode() {
  useDarkMode.value = !useDarkMode.value
}
</script>
