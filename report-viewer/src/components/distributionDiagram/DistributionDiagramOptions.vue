<template>
  <div class="flex flex-col space-y-1">
    <h3 class="text-lg underline">Options:</h3>
    <ScrollableComponent class="h-fit flex-grow">
      <MetricSelector
        class="mt-2"
        title="Metric:"
        :defaultSelected="store().uiState.distributionChartConfig.metric"
        @selection-changed="
          (metric: MetricType) => (store().uiState.distributionChartConfig.metric = metric)
        "
      />
      <OptionsSelector
        class="mt-2"
        title="Scale x-Axis:"
        :labels="['Linear', 'Logarithmic']"
        :defaultSelected="store().uiState.distributionChartConfig.xScale == 'linear' ? 0 : 1"
        @selection-changed="
          (i: number) =>
            (store().uiState.distributionChartConfig.xScale = i == 0 ? 'linear' : 'logarithmic')
        "
      />
      <OptionsSelector
        class="mt-2"
        title="Resolution:"
        :labels="['10', '100']"
        :defaultSelected="store().uiState.distributionChartConfig.resolution == 10 ? 0 : 1"
        @selection-changed="
          (i: number) => (store().uiState.distributionChartConfig.resolution = i == 0 ? 10 : 100)
        "
      />
    </ScrollableComponent>
  </div>
</template>

<script setup lang="ts">
import { MetricType } from '@/model/MetricType'
import { store } from '@/stores/store'
import MetricSelector from '@/components/optionsSelectors/MetricSelector.vue'
import OptionsSelector from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import ScrollableComponent from '../ScrollableComponent.vue'
</script>
