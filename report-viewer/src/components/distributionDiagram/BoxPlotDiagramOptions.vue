<template>
  <div class="flex flex-col space-y-1">
    <h3 class="text-lg underline">Options:</h3>
    <ScrollableComponent class="h-fit grow">
      <MetricSelector
        class="mt-2"
        title="Metric:"
        :default-selected="store().uiState.distributionChartConfig.metric"
        :metrics="metricOptions"
        @selection-changed="
          (metric: MetricJsonIdentifier) =>
            (store().uiState.distributionChartConfig.metric = metric)
        "
      />
    </ScrollableComponent>
  </div>
</template>

<script setup lang="ts">
import { store } from '@/stores/store'
import MetricSelector from '@/components/optionsSelectors/MetricSelector.vue'
import ScrollableComponent from '../ScrollableComponent.vue'
import { MetricJsonIdentifier } from '@/model/MetricJsonIdentifier'

const metricOptions = [
  MetricJsonIdentifier.AVERAGE_SIMILARITY,
  MetricJsonIdentifier.MAXIMUM_SIMILARITY
]
</script>
