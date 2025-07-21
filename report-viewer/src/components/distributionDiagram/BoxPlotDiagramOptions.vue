<template>
  <div class="flex flex-col space-y-1">
    <h3 class="text-lg underline">Options:</h3>
    <ScrollableComponent class="h-fit grow">
      <MetricSelector
        class="mt-2"
        title="Metric:"
        :default-selected="store().uiState.distributionChartConfig.metric"
        @selection-changed="
          (metric: MetricType) => (store().uiState.distributionChartConfig.metric = metric)
        "
      />
    </ScrollableComponent>
  </div>
</template>

<script setup lang="ts">
import type { MetricType } from '@/model/MetricType'
import { store } from '@/stores/store'
import MetricSelector from '@/components/optionsSelectors/MetricSelector.vue'
import ScrollableComponent from '../ScrollableComponent.vue'
</script>
