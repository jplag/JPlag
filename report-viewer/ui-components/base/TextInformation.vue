<!--
  A container displaying simple text information
-->
<template>
  <div class="print:flex-none">
    <ToolTipComponent :direction="tooltipSide">
      <template #default>
        <div class="flex items-start gap-x-1">
          <span>{{ label }}:</span>
          <i><slot name="default"></slot></i>
        </div>
      </template>
      <template v-if="$slots.tooltip" #tooltip>
        <slot name="tooltip"></slot>
      </template>
    </ToolTipComponent>
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import ToolTipComponent from './ToolTipComponent.vue'
import type { ToolTipDirection } from './ToolTip'

defineProps({
  label: {
    type: String,
    required: true
  },
  tooltipSide: {
    type: String as PropType<ToolTipDirection>,
    required: false,
    default: 'bottom'
  }
})
</script>
