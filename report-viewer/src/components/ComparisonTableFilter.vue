<template>
  <div class="space-y-2">
    <div class="flex flex-row flex-wrap items-center gap-x-8 gap-y-2">
      <h2>{{ header }}</h2>
      <ToolTipComponent direction="bottom" class="min-w-[50%] flex-grow">
        <template #default>
          <SearchBarComponent placeholder="Filter/Unhide Comparisons" v-model="searchStringValue" />
        </template>
        <template #tooltip>
          <p class="whitespace-pre text-sm">
            Type in the name of a submission to only show comparisons that contain this submission.
          </p>
          <p class="whitespace-pre text-sm">Fully written out names get unhidden.</p>
        </template>
      </ToolTipComponent>

      <ButtonComponent class="w-24" @click="changeAnonymousForAll()">
        {{
          store().state.anonymous.size == store().getSubmissionIds.length ? 'Show All' : 'Hide All'
        }}
      </ButtonComponent>
    </div>
    <OptionsSelector
      title="Sort By:"
      :defaultSelected="getSortingMetric()"
      :labels="tableSortingOptions"
      @selection-changed="(index: number) => changeSortingMetric(index)"
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
import { MetricType, metricToolTips } from '@/model/MetricType'
import type { ToolTipLabel } from '@/model/ui/ToolTip'

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

    const searches = value
      .trimEnd()
      .toLowerCase()
      .split(/ +/g)
      .map((s) => s.trim().replace(/,/g, ''))
    if (searches.length == 0) {
      return
    }

    for (const search of searches) {
      for (const submissionId of store().getSubmissionIds) {
        if (submissionId.toLowerCase() == search) {
          store().state.anonymous.delete(submissionId)
        }
      }
    }
  }
})

function changeSortingMetric(index: number) {
  store().uiState.comparisonTableSortingMetric =
    index < tableSortingMetricOptions.length ? tableSortingMetricOptions[index] : MetricType.AVERAGE
  store().uiState.comparisonTableClusterSorting = tableSortingOptions.value[index] == 'Cluster'
}

function getSortingMetric() {
  if (store().uiState.comparisonTableClusterSorting && props.enableClusterSorting) {
    return tableSortingOptions.value.indexOf('Cluster')
  }
  return tableSortingMetricOptions.indexOf(store().uiState.comparisonTableSortingMetric)
}

const tableSortingMetricOptions = [MetricType.AVERAGE, MetricType.MAXIMUM]
const tableSortingOptions = computed(() => {
  const options: (ToolTipLabel | string)[] = tableSortingMetricOptions.map((metric) => {
    return {
      displayValue: metricToolTips[metric].longName,
      tooltip: metricToolTips[metric].tooltip
    }
  })
  if (props.enableClusterSorting) {
    options.push({
      displayValue: 'Cluster',
      tooltip: 'Clustering of multiple files based on similarity percentages.'
    })
  }
  return options
})

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
