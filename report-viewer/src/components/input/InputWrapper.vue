<template>
  <input
    v-model="value"
    class="border-interactable-border-light dark:border-interactable-border-dark bg-interactable-light dark:bg-interactable-dark rounded border px-2"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    required: false,
    default: ''
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const value = computed({
  get: () => props.modelValue,
  set: (val) => {
    emit('update:modelValue', val)
  }
})
</script>
