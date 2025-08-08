<template>
  <div>
    <div
      class="border-interactable-border-light border-t-accent dark:border-interactable-border-dark dark:border-t-accent mx-auto h-16 w-16 animate-spin rounded-full border-8"
    ></div>
    <p class="text-2xl font-bold">{{ message }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps({
  message: {
    type: String,
    required: false,
    default: 'Loading Files...'
  }
})
</script>
