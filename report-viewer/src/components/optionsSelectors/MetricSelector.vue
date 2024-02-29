<template>
  <OptionsSelectorComponent
    :title="title"
    :labels="labels"
    :default-selected="metrics.indexOf(defaultSelected)"
    @selection-changed="(i) => $emit('selectionChanged', metrics[i])"
  />
</template>

<script setup lang="ts">
import { computed, type PropType, type Ref } from 'vue'
import OptionsSelectorComponent from './OptionsSelectorComponent.vue'
import { MetricType, metricToolTips } from '@/model/MetricType'

const props = defineProps({
  metrics: {
    type: Array<MetricType>,
    required: false,
    default: [MetricType.AVERAGE, MetricType.MAXIMUM]
  },
  title: {
    type: String,
    required: false,
    default: ''
  },
  defaultSelected: {
    type: String as PropType<MetricType>,
    required: false,
    default: MetricType.AVERAGE
  }
})

defineEmits(['selectionChanged'])

const labels: Ref<{ displayValue: string; tooltip: string }[]> = computed(() =>
  props.metrics.map((metric) => ({
    displayValue: metricToolTips[metric].longName,
    tooltip: metricToolTips[metric].tooltip
  }))
)
</script>
