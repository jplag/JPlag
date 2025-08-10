<!--
  A view displaying the overview file of a JPlag report.
-->
<template>
  <div
    ref="container"
    class="grid grid-cols-1 grid-rows-[auto_800px_90vh] gap-y-5 md:grid-cols-[1fr_20px_1fr] md:grid-rows-[auto_1fr] md:overflow-hidden print:grid-cols-1 print:grid-rows-[auto_auto]"
  >
    <ContainerComponent class="col-start-1 row-start-1 md:col-end-4 md:row-end-2">
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
              {{
                runInformation.failedSubmissions
                  .slice(0, 20)
                  .map((f) => f.submissionId)
                  .join(', ')
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
          reportStore().getReportFileName()
        }}</TextInformation>
        <TextInformation label="Total Submissions" class="flex-auto">{{
          reportStore().getSubmissionCount()
        }}</TextInformation>

        <TextInformation label="Shown/Total Comparisons" class="flex-auto">
          <template #default
            >{{ reportStore().includedComparisonCount() }} /
            {{ runInformation.totalComparisons }}</template
          >
          <template #tooltip>
            <div class="text-sm whitespace-pre">
              <TextInformation label="Shown Comparisons">{{
                reportStore().includedComparisonCount()
              }}</TextInformation>
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
            {{ reportStore().getCliOptions().minimumTokenMatch }}
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
            <ButtonComponent @click="router.push({ name: 'InfoView' })"
              ><span class="flex items-center">More <InfoIcon /></span
            ></ButtonComponent>
          </template>
          <template #tooltip>
            <p class="text-sm whitespace-pre">More information about the CLI run of JPlag</p>
          </template>
        </ToolTipComponent>
      </div>
    </ContainerComponent>

    <TabbedContainer
      :tabs="['Distribution', 'Boxplot']"
      class="col-start-1 row-start-2 flex flex-col overflow-hidden print:overflow-visible"
    >
      <template #Distribution>
        <DistributionDiagram
          v-model:config="uiStore().distributionChartConfig"
          :distributions="reportStore().getDistributions()"
          :use-dark-mode="uiStore().useDarkMode"
          class="grow print:flex-none"
          @click:upper-percentile="onBarClicked"
        />
      </template>
      <template #Boxplot>
        <BoxPlot
          v-model:metric="uiStore().distributionChartConfig.metric"
          :distributions="reportStore().getDistributions()"
          :use-dark-mode="uiStore().useDarkMode"
          class="grow print:flex-none"
        />
      </template>
    </TabbedContainer>

    <div ref="resizer" class="hidden w-5 cursor-col-resize md:col-start-2 md:row-start-2 md:flex">
      <!-- Resizer -->
    </div>

    <ContainerComponent
      class="col-start-1 row-start-3 flex overflow-hidden md:col-start-3 md:row-start-2 print:hidden"
    >
      <ComparisonTableWrapper
        ref="comparisonTable"
        :clusters="reportStore().getAllClusters()"
        :comparisons="topComparisons"
        class="min-h-0 max-w-full flex-1 print:min-h-full print:grow"
      >
        <template v-if="topComparisons.length < runInformation.totalComparisons" #footer>
          <p class="w-full pt-1 text-center font-bold">
            Not all comparisons are shown. To see more, re-run JPlag with a higher maximum number
            argument.
          </p>
        </template>
      </ComparisonTableWrapper>
    </ContainerComponent>
  </div>
</template>

<script setup lang="ts">
import { computed, onErrorCaptured, type Ref, ref, nextTick, onMounted } from 'vue'
import { redirectOnError, router } from '@/router'
import {
  ContainerComponent,
  ButtonComponent,
  TextInformation,
  ToolTipComponent,
  InfoIcon,
  TabbedContainer
} from '@jplag/ui-components/base'
import { DistributionDiagram, BoxPlot, Column, Direction } from '@jplag/ui-components/widget'
import { reportStore } from '@/stores/reportStore'
import { uiStore } from '@/stores/uiStore'
import ComparisonTableWrapper from '../components/ComparisonTableWrapper.vue'

const runInformation = computed(() => reportStore().getRunInformation())
const topComparisons = computed(() => reportStore().getTopComparisons())

const hasMoreSubmissionPaths = computed(
  () => reportStore().getCliOptions().submissionDirectories.length > 1
)
const submissionPathValue = computed(() =>
  hasMoreSubmissionPaths.value
    ? 'Click More to see all paths'
    : reportStore().getCliOptions().submissionDirectories[0]
)

const missingComparisons = computed(
  () => runInformation.value.totalComparisons - reportStore().includedComparisonCount()
)

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying overview:\n')
  return false
})

const comparisonTable: Ref<typeof ComparisonTableWrapper | null> = ref(null)
function onBarClicked(upperPercentile: number) {
  const adjustedPercentile = upperPercentile / 100
  if (!comparisonTable.value) {
    return
  }
  const metric = uiStore().distributionChartConfig.metric
  uiStore().comparisonTableSorting = {
    column: Column.getSortingFromMetric(metric),
    direction: Direction.descending
  }

  // determine largest similarity value that is still in the bucket
  let value = -1
  for (const comparison of topComparisons.value) {
    if (
      comparison.similarities[metric] <= adjustedPercentile &&
      comparison.similarities[metric] > value
    ) {
      value = comparison.similarities[metric]
    }
  }
  // the number of elements in this metric that are larger than that value equal the index in the list sorted by that metric
  let index = 0
  for (const comparison of topComparisons.value) {
    if (comparison.similarities[metric] > value) {
      index++
    }
  }

  // we scroll in the next tick so the table can adjust its sorting to the new metric
  nextTick(() => {
    comparisonTable.value?.scrollToItem(value < 0 ? undefined : index)
  })
}

const isResizing = ref(false)
const resizer = ref<HTMLDivElement | null>(null)
const container = ref<HTMLDivElement | null>(null)
onMounted(() => {
  if (resizer.value) {
    resizer.value.addEventListener('mousedown', (event) => {
      isResizing.value = true
      document.body.style.cursor = 'col-resize'
      event.preventDefault()
    })
  }
})
document.addEventListener('mouseup', () => {
  if (isResizing.value) {
    isResizing.value = false
    document.body.style.cursor = 'default'
  }
})
const minWidthPercentage = 33 // Minimum width percentage for each column
document.addEventListener('mousemove', (event) => {
  if (isResizing.value && resizer.value && container.value) {
    const containerRect = container.value.getBoundingClientRect()
    const containerWidth = containerRect.width
    const leftWidth = event.clientX - containerRect.left
    const rightWidth = containerWidth - leftWidth - resizer.value.offsetWidth
    let leftPercentage = (leftWidth / containerWidth) * 100
    let rightPercentage = (rightWidth / containerWidth) * 100
    if (leftPercentage < minWidthPercentage) {
      leftPercentage = minWidthPercentage // Minimum width for left column
      rightPercentage = 100 - leftPercentage
    }
    if (rightPercentage < minWidthPercentage) {
      rightPercentage = minWidthPercentage // Minimum width for right column
      leftPercentage = 100 - rightPercentage
    }

    container.value.style.gridTemplateColumns = `${leftPercentage}fr ${resizer.value.offsetWidth}px ${rightPercentage}fr`
  }
})
</script>
