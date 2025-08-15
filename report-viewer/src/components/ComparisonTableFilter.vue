<template>
  <div
    class="grid grid-cols-1 grid-rows-4 space-y-2 gap-x-2 md:grid-cols-[auto_1fr_auto] md:grid-rows-[auto_auto]"
  >
    <h2 class="col-start-1 row-start-1">{{ header }}</h2>
    <ToolTipComponent
      direction="left"
      class="col-start-1 row-start-2 w-full max-w-full md:col-start-2 md:row-start-1"
      :show-info-symbol="false"
    >
      <template #default>
        <SearchBarComponent
          v-model="searchStringValue"
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
      @click="changeAnonymousForAll()"
    >
      {{
        store().state.anonymous.size == store().getSubmissionIds.length
          ? 'Show All'
          : 'Anonymize All'
      }}
    </ButtonComponent>
    <MetricSelector
      class="col-start-1 row-start-4 md:col-span-3 md:col-start-1 md:row-start-2"
      title="Secondary Metric:"
      :default-selected="store().uiState.comparisonTableSecondaryMetric"
      :metrics="secondaryMetricOptions"
      :max-tool-tip-width="200"
      tooltip-direction="bottom"
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
import { store } from '@/stores/store'
import { MetricJsonIdentifier } from '@/model/MetricJsonIdentifier'
import MetricSelector from './optionsSelectors/MetricSelector.vue'

const props = defineProps({
  searchString: {
    type: String,
    default: ''
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

const secondaryMetricOptions = [
  MetricJsonIdentifier.MAXIMUM_SIMILARITY,
  MetricJsonIdentifier.LONGEST_MATCH,
  MetricJsonIdentifier.MAXIMUM_LENGTH
]
</script>
