<template>
  <label :for="id" class="col-start-1 flex items-center gap-x-1">
    <FontAwesomeIcon v-if="error != ''" :icon="faCircleExclamation" class="text-error text-xs" />
    {{ label }}:
  </label>
  <div class="col-start-2">
    <slot />
  </div>
  <ToolTipComponent
    v-if="tooltip != ''"
    class="col-start-3"
    direction="left"
    :scroll-offset-y="scrollOffsetY"
  >
    <template #tooltip>
      <p class="max-w-60 min-w-60 text-sm whitespace-pre-wrap">{{ tooltip }}</p>
    </template>
  </ToolTipComponent>
  <span v-else></span>
  <div v-if="error != ''" class="text-error col-span-3 col-start-1">{{ error }}</div>
</template>

<script setup lang="ts">
import { faCircleExclamation } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { ToolTipComponent } from '@jplag/ui-components/base'

const props = defineProps({
  label: {
    type: String,
    required: true
  },
  error: {
    type: String,
    required: false,
    default: ''
  },
  tooltip: {
    type: String,
    required: false,
    default: ''
  },
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  }
})

const id = computed(() => {
  return props.label.replace(/\s+/g, '-').toLowerCase()
})
</script>
