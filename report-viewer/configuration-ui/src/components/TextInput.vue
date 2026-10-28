<template>
  <input v-model="model" class="border rounded-md border-interactable-border bg-interactable outline-none px-1">
</template>

<script setup lang="ts">
const model = defineModel<string>()
</script>
