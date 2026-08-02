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
import { computed, watch } from 'vue'
import MetricSelector from '../optionsSelectors/MetricSelector.vue'
import { ScrollableComponent } from '../../base'
import { MetricJsonIdentifier } from '@jplag/model'

const model = defineModel<MetricJsonIdentifier>('metric', {
  default: MetricJsonIdentifier.AVERAGE_SIMILARITY
})

const props = defineProps({
  showWeightedMetric: {
    type: Boolean,
    default: true
  }
})

const metricOptions = computed(() => {
  const options = [
    MetricJsonIdentifier.AVERAGE_SIMILARITY,
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    MetricJsonIdentifier.WEIGHTED_SIMILARITY
  ]
  if (!props.showWeightedMetric) {
    return options.filter((m) => m !== MetricJsonIdentifier.WEIGHTED_SIMILARITY)
  }
  return options
})

watch(
  () => props.showWeightedMetric,
  (shown) => {
    if (!shown && model.value === MetricJsonIdentifier.WEIGHTED_SIMILARITY) {
      model.value = MetricJsonIdentifier.AVERAGE_SIMILARITY
    }
  },
  { immediate: true }
)
</script>
