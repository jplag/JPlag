<template>
  <div class="w-full grow flex items-center justify-center h-96">
    <div class="border-1 border-red-500">
      <MetricSelector />
    </div>
  </div>
</template>

<script setup lang="ts">
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import MetricSelector from '@/components/optionsSelectors/MetricSelector.vue'
import OptionsSelectorComponent from '@/components/optionsSelectors/OptionsSelectorComponent.vue'

const label = [
  {
    displayValue: 'Test',
    tooltip: 'This is a test'
  },
  {
    displayValue: 'Test2',
    tooltip: 'This is a test2'
  }
]
</script>
