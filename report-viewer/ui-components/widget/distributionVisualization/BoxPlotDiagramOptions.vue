<template>
  <div class="flex flex-col space-y-1">
    <h3 class="text-lg underline">Options:</h3>
    <ScrollableComponent class="h-fit grow">
      <MetricSelector
        class="mt-2"
        title="Metric:"
        :default-selected="model"
        :metrics="metricOptions"
        @selection-changed="(metric: MetricJsonIdentifier) => (model = metric)"
      />
    </ScrollableComponent>
  </div>
</template>

<script setup lang="ts">
import { computed, watch, type PropType } from 'vue'
import MetricSelector from '../optionsSelectors/MetricSelector.vue'
import { ScrollableComponent } from '../../base'
import { MetricJsonIdentifier } from '@jplag/model'

const model = defineModel<MetricJsonIdentifier>('metric', {
  default: MetricJsonIdentifier.AVERAGE_SIMILARITY
})

const props = defineProps({
  secondaryMetrics: {
    type: Object as PropType<Set<MetricJsonIdentifier>>,
    default: () =>
      new Set([
        MetricJsonIdentifier.AVERAGE_SIMILARITY,
        MetricJsonIdentifier.MAXIMUM_SIMILARITY,
        MetricJsonIdentifier.WEIGHTED_SIMILARITY
      ])
  }
})

const metricOptions = computed(() => {
  const allOptions = [
    MetricJsonIdentifier.AVERAGE_SIMILARITY,
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    MetricJsonIdentifier.WEIGHTED_SIMILARITY
  ]
  return allOptions.filter((m) => props.secondaryMetrics.has(m))
})

watch(
  () => props.secondaryMetrics,
  (metrics) => {
    if (!metrics.has(model.value)) {
      model.value = MetricJsonIdentifier.AVERAGE_SIMILARITY
    }
  },
  { immediate: true, deep: true }
)
</script>
