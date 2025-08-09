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
import MetricSelector from '../optionsSelectors/MetricSelector.vue'
import { ScrollableComponent } from '../../base'
import { MetricJsonIdentifier } from '@jplag/model'

const model = defineModel<MetricJsonIdentifier>('metric', {
  default: MetricJsonIdentifier.AVERAGE_SIMILARITY
})

const metricOptions = [
  MetricJsonIdentifier.AVERAGE_SIMILARITY,
  MetricJsonIdentifier.MAXIMUM_SIMILARITY
]
</script>
