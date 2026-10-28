<template>
  <input v-model.number="model" class="border rounded-md border-interactable-border bg-interactable outline-none px-1">
</template>

<script setup lang="ts">
const model = defineModel<number>()
</script>
