<template>
  <OptionsSelectorComponent
    :title="title"
    :labels="labels"
    :default-selected="metrics.indexOf(defaultSelected)"
    :max-tool-tip-width="maxToolTipWidth"
    @selection-changed="(i) => $emit('selectionChanged', metrics[i])"
  />
</template>

<script setup lang="ts">
import { computed, type PropType, type Ref } from 'vue'
import OptionsSelectorComponent from './OptionsSelectorComponent.vue'
import { MetricTypes } from '@/model/MetricType'
import { MetricJsonIdentifier } from '@/model/MetricJsonIdentifier'

const props = defineProps({
  metrics: {
    type: Array<MetricJsonIdentifier>,
    required: false,
    default: MetricTypes.METRIC_JSON_IDENTIFIERS
  },
  title: {
    type: String,
    required: false,
    default: ''
  },
  defaultSelected: {
    type: String as PropType<MetricJsonIdentifier>,
    required: false,
    default: MetricTypes.AVERAGE_SIMILARITY
  },
  maxToolTipWidth: {
    type: Number,
    required: false,
    default: -1
  }
})

defineEmits(['selectionChanged'])

const labels: Ref<{ displayValue: string; tooltip: string }[]> = computed(() =>
  props.metrics.map((metric) => ({
    displayValue: MetricTypes.METRIC_MAP[metric].longName,
    tooltip: MetricTypes.METRIC_MAP[metric].tooltip
  }))
)
</script>
