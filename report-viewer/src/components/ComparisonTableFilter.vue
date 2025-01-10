<template>
  <div class="space-y-2">
    <div class="flex flex-row flex-wrap items-center gap-x-8 gap-y-2">
      <h2>{{ header }}</h2>
      <ToolTipComponent direction="left" class="min-w-[50%] flex-grow">
        <template #default>
          <SearchBarComponent v-model="searchStringValue" placeholder="Filter/Unhide Comparisons" />
        </template>
        <template #tooltip>
          <p class="whitespace-pre text-sm">
            Type in the name of a submission to only show comparisons that contain this submission.
          </p>
          <p class="whitespace-pre text-sm">Fully written out names get unhidden.</p>
          <p class="whitespace-pre text-sm">
            You can also filter by index by entering a number or typing <i>index:number</i>
          </p>
          <p class="whitespace-pre text-sm">
            You can filter for specific similarity thresholds via &lt;/&gt;/&lt;=/&gt;= followed by
            the percentage. <br />
            You can filter for a specific metric by prefacing the percentage with the three-letter
            metric name (e.g. <i>avg:>80</i>)
          </p>
        </template>
      </ToolTipComponent>

      <ButtonComponent class="w-24" @click="changeAnonymousForAll()">
        {{
          store().state.anonymous.size == store().getSubmissionIds.length ? 'Show All' : 'Hide All'
        }}
      </ButtonComponent>
    </div>
    <OptionsSelector
      title="Sorting Metric:"
      :default-selected="getSortingMetric()"
      :labels="tableSortingOptions"
      @selection-changed="(index: number) => changeSortingMetric(index)"
    />
    <MetricSelector
      title="Secondary Metric:"
      :default-selected="store().uiState.comparisonTableSecondaryMetric"
      :metrics="secondaryMetricOptions"
      @selection-changed="
        (metric: MetricJsonIdentifier) => (store().uiState.comparisonTableSecondaryMetric = metric)
      "
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SearchBarComponent from './SearchBarComponent.vue'
import ToolTipComponent from './ToolTipComponent.vue'
import ButtonComponent from './ButtonComponent.vue'
import OptionsSelector from './optionsSelectors/OptionsSelectorComponent.vue'
import { store } from '@/stores/store'
import { MetricJsonIdentifier, MetricTypes } from '@/model/MetricType'
import type { ToolTipLabel } from '@/model/ui/ToolTip'
import MetricSelector from './optionsSelectors/MetricSelector.vue'

const props = defineProps({
  searchString: {
    type: String,
    default: ''
  },
  enableClusterSorting: {
    type: Boolean,
    default: true
  },
  header: {
    type: String,
    default: 'Top Comparisons:'
  }
})

const emit = defineEmits<{
  (e: 'update:searchString', v: string): void
}>()

const searchStringValue = computed({
  get: () => props.searchString,
  set: (value) => {
    emit('update:searchString', value)
    // Update the anonymous set

    const searchParts = value
      .trimEnd()
      .toLowerCase()
      .split(/ +/g)
      .map((s) => s.trim().replace(/,/g, ''))
    if (searchParts.length == 0) {
      return
    }

    for (const submissionId of store().getSubmissionIds) {
      const submissionParts = store().submissionDisplayName(submissionId).toLowerCase().split(/ +/g)
      if (submissionParts.every((part) => searchParts.includes(part))) {
        store().state.anonymous.delete(submissionId)
      }
    }
  }
})

function changeSortingMetric(index: number) {
  store().uiState.comparisonTableSortingMetric =
    index < tableSortingMetricOptions.length
      ? tableSortingMetricOptions[index].identifier
      : MetricJsonIdentifier.AVERAGE_SIMILARITY
  store().uiState.comparisonTableClusterSorting = tableSortingOptions.value[index] == 'Cluster'
}

function getSortingMetric() {
  if (store().uiState.comparisonTableClusterSorting && props.enableClusterSorting) {
    return tableSortingOptions.value.indexOf('Cluster')
  }
  return tableSortingMetricOptions.findIndex(
    (m) => m.identifier == store().uiState.comparisonTableSortingMetric
  )
}

const tableSortingMetricOptions = MetricTypes.METRIC_LIST
const tableSortingOptions = computed(() => {
  const options: (ToolTipLabel | string)[] = tableSortingMetricOptions.map((metric) => {
    return {
      displayValue: metric.longName,
      tooltip: metric.tooltip
    }
  })
  if (props.enableClusterSorting) {
    options.push('Cluster')
  }
  return options
})

const secondaryMetricOptions = [
  MetricJsonIdentifier.MAXIMUM_SIMILARITY,
  MetricJsonIdentifier.MINIMUM_SIMILARITY,
  MetricJsonIdentifier.SYMMETRIC,
  MetricJsonIdentifier.INTERSECTION,
  MetricJsonIdentifier.LONGEST_MATCH,
  MetricJsonIdentifier.OVERALL
]

/**
 * Sets the anonymous set to empty if it is full or adds all submission ids to it if it is not full
 */
function changeAnonymousForAll() {
  if (store().state.anonymous.size == store().getSubmissionIds.length) {
    store().state.anonymous.clear()
  } else {
    store().state.anonymous = new Set(store().getSubmissionIds)
  }
}
</script>
