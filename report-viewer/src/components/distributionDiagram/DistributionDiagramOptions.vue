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
      <OptionsSelector
        class="mt-2"
        title="Scale x-Axis:"
        :labels="['Linear', 'Logarithmic']"
        :default-selected="store().uiState.distributionChartConfig.xScale == 'linear' ? 0 : 1"
        @selection-changed="
          (i: number) =>
            (store().uiState.distributionChartConfig.xScale = i == 0 ? 'linear' : 'logarithmic')
        "
      />
      <OptionsSelector
        class="mt-2"
        title="Bucket Count:"
        :labels="resolutionOptions.map((div) => div.toString())"
        :default-selected="
          resolutionOptions.indexOf(store().uiState.distributionChartConfig.bucketCount)
        "
        @selection-changed="
          (i: number) =>
            (store().uiState.distributionChartConfig.bucketCount = resolutionOptions[i])
        "
      />

      <div>
        <h3
          class="test-lg flex cursor-pointer items-center"
          @click="showAdvancedOptions = !showAdvancedOptions"
        >
          <FontAwesomeIcon
            :icon="showAdvancedOptions ? faAngleDown : faAngleRight"
            class="h-4 w-4"
          />
          <span>Advanced</span>
        </h3>
        <div v-if="showAdvancedOptions" class="pl-2">
          <BooleanSelector
            v-model="store().uiState.distributionChartConfig.showBinomialCurve"
            :label="{
              displayValue: 'Show Binomial Curve',
              tooltip:
                'Shows a curve representing a binomial distribution. The expected value of the distribution is the mean of the comparisons.'
            }"
          />
        </div>
      </div>
    </ScrollableComponent>
  </div>
</template>

<script setup lang="ts">
import { MetricType } from '@/model/MetricType'
import { store } from '@/stores/store'
import MetricSelector from '@/components/optionsSelectors/MetricSelector.vue'
import OptionsSelector from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import ScrollableComponent from '../ScrollableComponent.vue'
import { type BucketOptions } from '@/model/Distribution'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faAngleDown, faAngleRight } from '@fortawesome/free-solid-svg-icons'
import BooleanSelector from '@/components/optionsSelectors/BooleanSelector.vue'
import { ref } from 'vue'

library.add(faAngleDown, faAngleRight)

const resolutionOptions = [10, 20, 25, 50, 100] as BucketOptions[]

const showAdvancedOptions = ref(false)
</script>
