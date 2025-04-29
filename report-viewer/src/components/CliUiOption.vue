<template>
  <label :for="id" class="col-start-1 flex items-center">
    <FontAwesomeIcon v-if="error != ''" :icon="faCircleExclamation" class="text-error text-xs" />
    {{ label }}:
  </label>
  <div class="col-start-2">
    <slot />
  </div>
  <div v-if="error != ''" class="text-error col-span-1 col-start-1">{{ error }}</div>
</template>

<script setup lang="ts">
import { faCircleExclamation } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'

const props = defineProps({
  label: {
    type: String,
    required: true
  },

  error: {
    type: String,
    required: false,
    default: ''
  }
})

const id = computed(() => {
  return props.label.replace(/\s+/g, '-').toLowerCase()
})
</script>
