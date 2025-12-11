<template>
  <div class="flex flex-col space-y-1">
    <h3 class="text-lg underline">Options:</h3>
    <ScrollableComponent class="h-fit grow">
      <MetricSelector
        class="mt-2"
        title="Metric:"
        :default-selected="config.metric"
        :metrics="metricOptions"
        @selection-changed="(metric: DistributionMetrics) => (config.metric = metric)"
      />
      <OptionsSelector
        class="mt-2"
        title="Scale x-Axis:"
        :labels="['Linear', 'Logarithmic']"
        :default-selected="config.xScale == 'linear' ? 0 : 1"
        @selection-changed="(i: number) => (config.xScale = i == 0 ? 'linear' : 'logarithmic')"
      />
      <OptionsSelector
        class="mt-2"
        title="Bucket Count:"
        :labels="resolutionOptions.map((div) => div.toString())"
        :default-selected="resolutionOptions.indexOf(config.bucketCount)"
        @selection-changed="(i: number) => (config.bucketCount = resolutionOptions[i])"
      />
    </ScrollableComponent>
  </div>
</template>

<script setup lang="ts">
import { DistributionMetrics, MetricJsonIdentifier, type BucketOptions } from '@jplag/model'
import MetricSelector from '../optionsSelectors/MetricSelector.vue'
import OptionsSelector from '../optionsSelectors/OptionsSelectorComponent.vue'
import { ScrollableComponent } from '../../base'
import { DistributionChartConfig } from './DistributionChartConfig'

const config = defineModel<DistributionChartConfig>({
  default: {
    metric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
    xScale: 'linear',
    bucketCount: 10
  }
})

const resolutionOptions = [10, 20, 25, 50, 100] as BucketOptions[]

const metricOptions: DistributionMetrics[] = [
  MetricJsonIdentifier.AVERAGE_SIMILARITY,
  MetricJsonIdentifier.MAXIMUM_SIMILARITY
]
</script>
