<!--
  A view displaying the overview file of a JPlag report.
-->
<template>
  <div
    class="grid grid-cols-1 grid-rows-[auto_800px_90vh] gap-5 md:grid-cols-2 md:grid-rows-[auto_1fr] md:overflow-hidden print:grid-cols-1 print:grid-rows-[auto_auto]"
  >
    <Container class="col-start-1 row-start-1 md:col-end-3 md:row-end-2">
      <div class="flex flex-col gap-x-5 md:flex-row md:items-center">
        <h2>JPlag Report</h2>
        <ToolTipComponent v-if="runInformation.failedSubmissions.length > 0" direction="bottom">
          <template #default>
            <p class="text-error font-bold">
              {{ runInformation.failedSubmissions.length }} invalid submissions. They are excluded
              from the comparison. Click "<i>More</i>" to show all failed submissions.
            </p>
          </template>
          <template #tooltip>
            <p class="max-w-[50rem] text-sm whitespace-pre-wrap">
              {{ runInformation.failedSubmissions.slice(0, 20).join(', ')
              }}<span v-if="runInformation.failedSubmissions.length > 20"
                >... (click "<i>More</i>" to see the complete list of failed submissions)</span
              >
            </p>
          </template>
        </ToolTipComponent>
      </div>

      <div
        class="flex flex-col gap-x-5 gap-y-2 md:flex-row md:items-center print:flex-col print:items-start"
      >
        <TextInformation label="Submission Directory" class="flex-auto">{{
          submissionPathValue
        }}</TextInformation>
        <TextInformation label="Result name" class="flex-auto">{{
          store().state.uploadedFileName
        }}</TextInformation>
        <TextInformation label="Total Submissions" class="flex-auto">{{
          store().getSubmissionIds.length
        }}</TextInformation>

        <TextInformation label="Shown/Total Comparisons" class="flex-auto">
          <template #default
            >{{ shownComparisons }} / {{ runInformation.totalComparisons }}</template
          >
          <template #tooltip>
            <div class="text-sm whitespace-pre">
              <TextInformation label="Shown Comparisons">{{ shownComparisons }}</TextInformation>
              <TextInformation label="Total Comparisons">{{
                runInformation.totalComparisons
              }}</TextInformation>
              <div v-if="missingComparisons > 0">
                <TextInformation label="Missing Comparisons">{{
                  missingComparisons
                }}</TextInformation>
                <p>
                  To include more comparisons in the report modify the number of shown comparisons
                  in the CLI.
                </p>
              </div>
            </div>
          </template>
        </TextInformation>

        <TextInformation label="Min Token Match" class="flex-auto">
          <template #default>
            {{ options.minimumTokenMatch }}
          </template>
          <template #tooltip>
            <div class="text-sm whitespace-pre">
              <p>
                Tunes the comparison sensitivity by adjusting the minimum token required to be
                counted as a matching section.
              </p>
              <p>It can be adjusted in the CLI.</p>
            </div>
          </template>
        </TextInformation>

        <ToolTipComponent direction="left" class="grow-0 print:hidden" :show-info-symbol="false">
          <template #default>
            <Button @click="router.push({ name: 'InfoView' })"
              ><span class="flex items-center">More <InfoIcon /></span
            ></Button>
          </template>
          <template #tooltip>
            <p class="text-sm whitespace-pre">More information about the CLI run of JPlag</p>
          </template>
        </ToolTipComponent>
      </div>
    </Container>

    <TabbedContainer
      :tabs="['Distribution', 'Boxplot']"
      class="col-start-1 row-start-2 flex flex-col overflow-hidden print:overflow-visible"
    >
      <template #Distribution>
        <DistributionDiagram
          :distributions="distributions"
          class="grow print:flex-none"
          @click:upper-percentile="onBarClicked"
        />
      </template>
      <template #Boxplot>
        <BoxPlot :distributions="distributions" class="grow print:flex-none" />
      </template>
    </TabbedContainer>

    <Container
      class="col-start-1 row-start-3 flex overflow-hidden md:col-start-2 md:row-start-2 print:hidden"
    >
      <ComparisonsTable
        ref="comparisonTable"
        :clusters="clusters"
        :top-comparisons="topComparisons"
        class="min-h-0 max-w-full flex-1 print:min-h-full print:grow"
      >
        <template v-if="topComparisons.length < runInformation.totalComparisons" #footer>
          <p class="w-full pt-1 text-center font-bold">
            Not all comparisons are shown. To see more, re-run JPlag with a higher maximum number
            argument.
          </p>
        </template>
      </ComparisonsTable>
    </Container>
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType, onErrorCaptured, type Ref, ref, nextTick } from 'vue'
import { redirectOnError, router } from '@/router'
import DistributionDiagram from '@/components/distributionDiagram/DistributionDiagram.vue'
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import { store } from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'
import Button from '@/components/ButtonComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import type { CliOptions } from '@/model/CliOptions'
import type { RunInformation } from '@/model/RunInformation'
import type { DistributionMap } from '@/model/Distribution'
import type { Cluster } from '@/model/Cluster'
import InfoIcon from '@/components/InfoIcon.vue'
import { Column, Direction } from '@/model/ui/ComparisonSorting'
import BoxPlot from '@/components/distributionDiagram/BoxPlot.vue'
import TabbedContainer from '@/components/TabbedContainer.vue'

const props = defineProps({
  topComparisons: {
    type: Array<ComparisonListElement>,
    required: true
  },
  options: {
    type: Object as PropType<CliOptions>,
    required: true
  },
  runInformation: {
    type: Object as PropType<RunInformation>,
    required: true
  },
  distributions: {
    type: Object as PropType<DistributionMap>,
    required: true
  },
  clusters: {
    type: Array<Cluster>,
    required: true
  }
})

document.title = `${store().state.uploadedFileName} - JPlag Report Viewer`

const hasMoreSubmissionPaths = computed(() => props.options.submissionDirectories.length > 1)
const submissionPathValue = computed(() =>
  hasMoreSubmissionPaths.value
    ? 'Click More to see all paths'
    : props.options.submissionDirectories[0]
)

const shownComparisons = computed(() => props.topComparisons.length)
const missingComparisons = computed(
  () => props.runInformation.totalComparisons - shownComparisons.value
)

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying overview:\n')
  return false
})

const comparisonTable: Ref<typeof ComparisonsTable | null> = ref(null)
function onBarClicked(upperPercentile: number) {
  const adjustedPercentile = upperPercentile / 100
  if (!comparisonTable.value) {
    return
  }
  const metric = store().uiState.distributionChartConfig.metric
  store().uiState.comparisonTableSorting = {
    column: Column.getSortingFromMetric(metric),
    direction: Direction.descending
  }

  // determine largest similarity value that is still in the bucket
  let value = -1
  for (const comparison of props.topComparisons) {
    if (
      comparison.similarities[metric] <= adjustedPercentile &&
      comparison.similarities[metric] > value
    ) {
      value = comparison.similarities[metric]
    }
  }
  // the number of elements in this metric that are larger than that value equal the index in the list sorted by that metric
  let index = 0
  for (const comparison of props.topComparisons) {
    if (comparison.similarities[metric] > value) {
      index++
    }
  }

  // we scroll in the next tick so the table can adjust its sorting to the new metric
  nextTick(() => {
    comparisonTable.value?.scrollToItem(value < 0 ? undefined : index)
  })
}
</script>
