<template>
  <ButtonComponent
    class="fixed right-2 bottom-2 flex h-12 w-12 items-center justify-center text-center print:hidden"
    @click="useDarkMode = !useDarkMode"
  >
    <FontAwesomeIcon class="text-2xl" :icon="useDarkMode ? faSun : faMoon" />
  </ButtonComponent>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faSun, faMoon } from '@fortawesome/free-solid-svg-icons'
import ButtonComponent from './ButtonComponent.vue'
const useDarkMode = defineModel<boolean>({ default: false })
</script>
