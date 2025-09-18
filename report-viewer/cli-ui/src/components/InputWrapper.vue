<template>
  <input
    v-model="model"
    class="border-interactable-border-light dark:border-interactable-border-dark bg-interactable-light dark:bg-interactable-dark rounded border px-2"
  />
</template>

<script setup lang="ts">
const model = defineModel<string>({
  default: ''
})
</script>
