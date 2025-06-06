<template>
  <div class="space-y-2">
    <div
      class="flex flex-col flex-wrap gap-x-8 gap-y-2 overflow-hidden md:flex-row md:items-center"
    >
      <h2>{{ header }}</h2>
      <ToolTipComponent
        direction="left"
        class="max-w-full grow md:min-w-[40%]"
        :show-info-symbol="false"
      >
        <template #default>
          <SearchBarComponent v-model="searchStringValue" placeholder="Filter/Unhide Comparisons" />
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
            You can filter for a specific metric by prefacing the percentage with the three-letter
            metric name (e.g. <i>avg:>80</i>)
          </p>
        </template>
      </ToolTipComponent>

      <ButtonComponent class="w-30 min-w-fit whitespace-nowrap" @click="changeAnonymousForAll()">
        {{
          store().state.anonymous.size == store().getSubmissionIds.length
            ? 'Show All'
            : 'Anonymize All'
        }}
      </ButtonComponent>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SearchBarComponent from './SearchBarComponent.vue'
import ToolTipComponent from './ToolTipComponent.vue'
import ButtonComponent from './ButtonComponent.vue'
import { store } from '@/stores/store'

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
</script>
