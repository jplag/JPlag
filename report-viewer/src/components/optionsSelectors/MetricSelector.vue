<template>
  <OptionsSelectorComponent
    :name="name"
    :labels="labels"
    @selection-changed="(i) => $emit('selectionChanged', metrics[i])"
  />
</template>

<script setup lang="ts">
import OptionsSelectorComponent from './OptionsSelectorComponent.vue'
import MetricType, { metricToolTips } from '@/model/MetricType'

const props = defineProps({
  metrics: {
    type: Array<MetricType>,
    required: false,
    default: [MetricType.AVERAGE, MetricType.MAXIMUM]
  },
  name: {
    type: String,
    required: false,
    default: ''
  }
})

defineEmits(['selectionChanged'])

const labels: { displayValue: string; tooltip: string }[] = []

for (const metric of props.metrics) {
  labels.push({
    displayValue: metricToolTips[metric].longName,
    tooltip: metricToolTips[metric].tooltip
  })
}
</script>
