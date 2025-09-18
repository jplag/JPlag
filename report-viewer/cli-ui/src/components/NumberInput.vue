<template>
  <input
    v-model="internalModel"
    class="border-interactable-border-light dark:border-interactable-border-dark bg-interactable-light dark:bg-interactable-dark rounded border px-2"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'

const model = defineModel<number>({
  default: 0
})

const internalModel = computed({
  get: () => model.value.toString(),
  set: (v: string) => {
    model.value = parseInt(v)
  }
})
</script>
