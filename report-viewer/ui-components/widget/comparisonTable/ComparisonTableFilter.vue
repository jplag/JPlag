<template>
  <div
    class="grid grid-cols-1 grid-rows-4 space-y-2 gap-x-2 md:grid-cols-[auto_1fr_auto] md:grid-rows-[auto_auto]"
  >
    <h2 class="col-start-1 row-start-1">{{ header }}</h2>
    <ToolTipComponent
      direction="left"
      class="row-starsdt-2 col-start-1 w-full max-w-full md:col-start-2 md:row-start-1"
      :show-info-symbol="false"
    >
      <template #default>
        <SearchBarComponent
          v-model="searchString"
          class="w-full"
          placeholder="Filter/Unhide Comparisons"
        />
      </template>
      <template #tooltip>
        <p class="text-sm whitespace-pre">
          Type in the name of a submission to only show comparisons that contain this submission.
        </p>
        <p class="text-sm whitespace-pre">Fully written out names get unhidden.</p>
        <p class="text-sm whitespace-pre">
          You can also filter by index by entering a number or typing <i>index:number</i>
        </p>
        <p class="text-sm whitespace-pre">
          You can filter for specific similarity thresholds via &lt;/&gt;/&lt;=/&gt;= followed by
          the percentage. <br />
          You can filter for a specific metric by prefacing the percentage with the short metric
          name (e.g. <i>avg:>80</i>)
        </p>
      </template>
    </ToolTipComponent>

    <ButtonComponent
      class="col-start-1 row-start-3 w-30 min-w-fit whitespace-nowrap md:col-start-3 md:row-start-1"
      @click="emit('changeAnonymousForAll')"
    >
      {{ allAreAnonymized ? 'Show All' : 'Anonymize All' }}
    </ButtonComponent>
    <MetricSelector
      class="col-start-1 row-start-4 md:col-span-3 md:col-start-1 md:row-start-2"
      title="Secondary Metric:"
      :default-selected="secondaryMetric"
      :metrics="secondaryMetricOptions"
      :max-tool-tip-width="200"
      tooltip-direction="bottom"
      @selection-changed="(metric: MetricJsonIdentifier) => (secondaryMetric = metric)"
    />
  </div>
</template>

<script setup lang="ts">
import { SearchBarComponent, ToolTipComponent, ButtonComponent } from '../../base'
import { MetricJsonIdentifier } from '@jplag/model'
import MetricSelector from '../optionsSelectors/MetricSelector.vue'

defineProps({
  header: {
    type: String,
    default: 'Top Comparisons:'
  },
  allAreAnonymized: {
    type: Boolean,
    default: false
  }
})

const searchString = defineModel<string>('searchString', {
  default: ''
})
const secondaryMetric = defineModel<MetricJsonIdentifier>('secondaryMetric', {
  default: MetricJsonIdentifier.MAXIMUM_SIMILARITY
})

const emit = defineEmits<{
  (event: 'changeAnonymousForAll'): void
}>()

const secondaryMetricOptions = [
  MetricJsonIdentifier.MAXIMUM_SIMILARITY,
  MetricJsonIdentifier.LONGEST_MATCH,
  MetricJsonIdentifier.MAXIMUM_LENGTH
]
</script>
