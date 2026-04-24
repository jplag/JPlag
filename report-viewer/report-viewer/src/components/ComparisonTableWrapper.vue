<template>
  <ComparisonTable
    ref="table"
    v-model:secondary-metric="uiStore().secondaryTableMetric"
    v-model:sorting="uiStore().comparisonTableSorting"
    v-model:search-string="searchString"
    :clusters="cluster"
    :top-comparisons="comparisons"
    :use-dark-mode="uiStore().useDarkMode"
    :get-display-name="reportStore().getDisplayName"
    :get-anonymous-name="reportStore().getAnonymizedName"
    :get-plain-display-name="reportStore().getPlainDisplayName"
    :is-anonymous="reportStore().isAnonymized"
    :all-are-anonymized="reportStore().allAreAnonymized()"
    class="min-h-0 max-w-full flex-1 print:min-h-full print:grow"
    @change-anonymous="(id) => reportStore().toggleAnonymous(id)"
    @change-anonymous-for-all="reportStore().toggleAnonymousForAll()"
    @line-hovered="(e) => emit('lineHovered', e)"
  >
    <slot name="footer" />
  </ComparisonTable>
</template>

<script setup lang="ts">
import { ComparisonTable } from '@jplag/ui-components/widget'
import { uiStore } from '@/stores/uiStore'
import { reportStore } from '@/stores/reportStore'
import { Cluster, ComparisonListElement } from '@jplag/model'
import { PropType, Ref, ref, watch } from 'vue'

defineProps({
  comparisons: {
    type: Array<ComparisonListElement>,
    required: true
  },
  cluster: {
    type: Array<Cluster>,
    required: false,
    default: undefined
  },
  header: {
    type: String,
    default: 'Top Comparisons:'
  },
  highlightedRowIds: {
    type: Object as PropType<{ firstId: string; secondId: string }>,
    required: false,
    default: undefined
  }
})

const emit = defineEmits<{
  (event: 'lineHovered', value: { firstId: string; secondId: string } | null): void
}>()

const searchString = ref('')

watch(
  () => searchString.value,
  (value) => {
    // Update the anonymous set
    const searchParts = value
      .trimEnd()
      .toLowerCase()
      .split(/ +/g)
      .map((s) => s.trim().replace(/,/g, ''))
    if (searchParts.length == 0) {
      return
    }

    for (const submissionId of reportStore().getSubmissionIds()) {
      const submissionParts = reportStore()
        .getPlainDisplayName(submissionId)
        .toLowerCase()
        .split(/ +/g)
      if (submissionParts.every((part) => searchParts.includes(part))) {
        reportStore().setAnonymous(submissionId, false)
      }
    }
  }
)

const table: Ref<typeof ComparisonTable | null> = ref(null)

function scrollToItem(index: number | undefined) {
  table.value?.scrollToItem(index)
}
defineExpose({ scrollToItem })
</script>
