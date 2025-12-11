<template>
  <OptionsSelectorComponent
    :title="title"
    :labels="labels"
    :default-selected="metrics.indexOf(defaultSelected)"
    :max-tool-tip-width="maxToolTipWidth"
    :tooltip-direction="tooltipDirection"
    @selection-changed="(i) => $emit('selectionChanged', metrics[i])"
  >
    <template #average-similarity>
      <MetricIcon class="h-4 pr-1" :metric="MetricJsonIdentifier.AVERAGE_SIMILARITY" />
    </template>
    <template #maximum-similarity>
      <MetricIcon class="h-4 pr-1" :metric="MetricJsonIdentifier.MAXIMUM_SIMILARITY" />
    </template>
    <template #longest-match>
      <MetricIcon class="h-4 pr-1" :metric="MetricJsonIdentifier.LONGEST_MATCH" />
    </template>
    <template #maximum-length>
      <MetricIcon class="h-4 pr-1" :metric="MetricJsonIdentifier.MAXIMUM_LENGTH" />
    </template>
  </OptionsSelectorComponent>
</template>

<script setup lang="ts">
import { computed, type PropType, type Ref } from 'vue'
import OptionsSelectorComponent from './OptionsSelectorComponent.vue'
import { MetricTypes } from '../MetricType'
import { MetricJsonIdentifier } from '@jplag/model'
import MetricIcon from '../MetricIcon.vue'
import type { ToolTipDirection } from '../../base'

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
    default: MetricJsonIdentifier.AVERAGE_SIMILARITY
  },
  maxToolTipWidth: {
    type: Number,
    required: false,
    default: -1
  },
  tooltipDirection: {
    type: String as PropType<ToolTipDirection>,
    required: false,
    default: 'right'
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
