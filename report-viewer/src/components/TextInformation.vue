<!--
  A container displaying simple text information
-->
<template>
  <div class="flex-auto print:flex-none">
    <ToolTipComponent direction="bottom">
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
import ToolTipComponent from './ToolTipComponent.vue'

defineProps({
  label: {
    type: String,
    required: true
  }
})
</script>
