<!--
  A container displaying simple text information
-->
<template>
  <div class="print:flex-none">
    <ToolTipComponent :direction="tooltipSide">
      <template #default>
        {{ label }}:
        <i><slot name="default"></slot></i>
      </template>
      <template #tooltip v-if="$slots.tooltip">
        <slot name="tooltip"></slot>
      </template>
    </ToolTipComponent>
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import ToolTipComponent from './ToolTipComponent.vue'
import type { ToolTipDirection } from '@/model/ui/ToolTip'

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
