<!--
  A view displaying the overview file of a JPlag report.
-->
<template>
  <div class="absolute bottom-0 left-0 right-0 top-0 flex flex-col">
    <div class="relative left-0 right-0 top-0 flex space-x-5 p-5 pb-0">
      <Container class="flex-grow">
        <h2>JPlag Report</h2>
        <div class="flex flex-row items-center space-x-5">
          <TextInformation label="Directory">{{ submissionPathValue }}</TextInformation>
          <TextInformation label="Total Submissions">{{
            store().getSubmissionIds.length
          }}</TextInformation>
          <TextInformation label="Total Comparisons">{{
            overview.totalComparisons
          }}</TextInformation>
          <TextInformation label="Min Match Length">{{
            overview.matchSensitivity
          }}</TextInformation>

          <ToolTipComponent direction="left">
            <template #default>
              <Button @click="router.push({ name: 'InfoView' })"> More </Button>
            </template>
            <template #tooltip>
              <p class="whitespace-pre text-sm">More information about the CLI run of JPlag</p>
            </template>
          </ToolTipComponent>
        </div>
      </Container>
    </div>

    <div class="relative bottom-0 left-0 right-0 flex flex-grow space-x-5 p-5 pt-5">
      <Container class="flex max-h-0 min-h-full flex-1 flex-col">
        <h2>Distribution of Comparisons:</h2>
        <DistributionDiagram
          :distribution="overview.distribution[store().uiState.distributionChartConfig.metric]"
          :x-scale="store().uiState.distributionChartConfig.xScale"
          class="h-2/3 w-full"
        />
        <div class="flex flex-grow flex-col space-y-1">
          <h3 class="text-lg underline">Options:</h3>
          <ScrollableComponent class="h-fit flex-grow">
            <MetricSelector
              class="mt-2"
              title="Metric:"
              :defaultSelected="store().uiState.distributionChartConfig.metric"
              @selection-changed="
                (metric: MetricType) => (store().uiState.distributionChartConfig.metric = metric)
              "
            />
            <OptionsSelector
              class="mt-2"
              title="Scale x-Axis:"
              :labels="['Linear', 'Logarithmic']"
              :defaultSelected="store().uiState.distributionChartConfig.xScale == 'linear' ? 0 : 1"
              @selection-changed="
                (i: number) =>
                  (store().uiState.distributionChartConfig.xScale =
                    i == 0 ? 'linear' : 'logarithmic')
              "
            />
          </ScrollableComponent>
        </div>
      </Container>

      <Container class="flex max-h-0 min-h-full flex-1 flex-col space-y-2">
        <div class="flex flex-row items-center space-x-8">
          <h2>Top Comparisons:</h2>
          <ToolTipComponent direction="bottom" class="flex-grow">
            <template #default>
              <SearchBarComponent
                placeholder="Filter/Unhide Comparisons"
                @input-changed="(value) => (searchString = value)"
              />
            </template>
            <template #tooltip>
              <p class="whitespace-pre text-sm">
                Type in the name of a submission to only show comparisons that contain this
                submission.
              </p>
              <p class="whitespace-pre text-sm">Fully written out names get unhidden.</p>
            </template>
          </ToolTipComponent>

          <Button class="w-24" @click="changeAnnoymousForAll()">
            {{
              store().state.anonymous.size == store().getSubmissionIds.length
                ? 'Show All'
                : 'Hide All'
            }}
          </Button>
        </div>
        <OptionsSelector
          title="Sort By:"
          :defaultSelected="getSortingMetric()"
          :labels="tableSortingOptions"
          @selection-changed="(index: number) => changeSortingMetric(index)"
        />
        <ComparisonsTable
          :clusters="overview.clusters"
          :top-comparisons="displayedComparisons"
          class="min-h-0 flex-1"
        >
          <template #footer v-if="overview.topComparisons.length < overview.totalComparisons">
            <p class="w-full pt-1 text-center font-bold">
              Not all comparisons are shown. To see more, re-run JPlag with a higher maximum number
              argument.
            </p>
          </template>
        </ComparisonsTable>
      </Container>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onErrorCaptured, ref, watch, type PropType } from 'vue'
import { router } from '@/router'
import DistributionDiagram from '@/components/DistributionDiagram.vue'
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import { store } from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'
import Button from '@/components/ButtonComponent.vue'
import ScrollableComponent from '@/components/ScrollableComponent.vue'
import { MetricType, metricToolTips } from '@/model/MetricType'
import SearchBarComponent from '@/components/SearchBarComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import MetricSelector from '@/components/optionsSelectors/MetricSelector.vue'
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import OptionsSelector from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import { Overview } from '@/model/Overview'
import type { Cluster } from '@/model/Cluster'
import type { ToolTipLabel } from '@/model/ui/ToolTip'

const props = defineProps({
  overview: {
    type: Object as PropType<Overview>,
    required: true
  }
})

const searchString = ref('')
const tableSortingMetricOptions = [MetricType.AVERAGE, MetricType.MAXIMUM]
const tableSortingOptions = computed(() => {
  const options: (ToolTipLabel | string)[] = tableSortingMetricOptions.map((metric) => {
    return {
      displayValue: metricToolTips[metric].longName,
      tooltip: metricToolTips[metric].tooltip
    }
  })
  options.push('Cluster')
  return options
})

function changeSortingMetric(index: number) {
  store().uiState.comparisonTableSortingMetric =
    index < tableSortingMetricOptions.length ? tableSortingMetricOptions[index] : MetricType.AVERAGE
  store().uiState.comparisonTableClusterSorting = tableSortingOptions.value[index] == 'Cluster'
}

function getSortingMetric() {
  if (store().uiState.comparisonTableClusterSorting) {
    return tableSortingOptions.value.indexOf('Cluster')
  }
  return tableSortingMetricOptions.indexOf(store().uiState.comparisonTableSortingMetric)
}

/**
 * This funtion gets called when the search bar for the compariosn table has been updated.
 * It updates the displayed comparisons to only show the ones that  have part of any search result in their id. The search is not case sensitive. The parts can be seprarated by commas or spaces.
 * It also updates the annonmous set to unhide a submission if its name was typed in the search bar at any point in time.
 *
 * @param newVal The new value of the search bar
 */
function getFilteredComparisons(comparisons: ComparisonListElement[]) {
  const searches = searchString.value
    .trimEnd()
    .toLowerCase()
    .split(/ +/g)
    .map((s) => s.trim().replace(/,/g, ''))
  if (searches.length == 0) {
    return comparisons
  }

  return comparisons.filter((c) => {
    const id1 = c.firstSubmissionId.toLowerCase()
    const id2 = c.secondSubmissionId.toLowerCase()
    return searches.some((s) => id1.includes(s) || id2.includes(s))
  })
}

function getClusterIndexFor(id1: string, id2: string) {
  let clusterIndex = -1
  props.overview.clusters.forEach((c: Cluster, index: number) => {
    if (c.members.includes(id1) && c.members.includes(id2) && c.members.length > 2) {
      clusterIndex = index
    }
  })
  return clusterIndex
}

function getClusterFor(id1: string, id2: string) {
  const index = getClusterIndexFor(id1, id2)
  if (index < 0) {
    return { averageSimilarity: 0 }
  }
  return props.overview.clusters[index]
}

function getSortedComparisons(comparisons: ComparisonListElement[]) {
  comparisons.sort(
    (a, b) =>
      b.similarities[store().uiState.comparisonTableSortingMetric] -
      a.similarities[store().uiState.comparisonTableSortingMetric]
  )

  if (store().uiState.comparisonTableClusterSorting) {
    comparisons.sort(
      (a, b) =>
        getClusterFor(b.firstSubmissionId, b.secondSubmissionId).averageSimilarity -
        getClusterFor(a.firstSubmissionId, a.secondSubmissionId).averageSimilarity
    )

    comparisons.sort(
      (a, b) =>
        getClusterIndexFor(b.firstSubmissionId, b.secondSubmissionId) -
        getClusterIndexFor(a.firstSubmissionId, a.secondSubmissionId)
    )
  }

  let index = 0
  comparisons.forEach((c) => {
    c.sortingPlace = index++
  })
  return props.overview.topComparisons
}

const displayedComparisons = computed(() => {
  const comparisons = getFilteredComparisons(getSortedComparisons(props.overview.topComparisons))
  let index = 1
  comparisons.forEach((c) => {
    c.id = index++
  })
  return comparisons
})

// Update the anonymous set
watch(searchString, () => {
  const searches = searchString.value
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
})

/**
 * Sets the annonymous set to empty if it is full or adds all submission ids to it if it is not full
 */
function changeAnnoymousForAll() {
  if (store().state.anonymous.size == store().getSubmissionIds.length) {
    store().state.anonymous.clear()
  } else {
    store().state.anonymous = new Set(store().getSubmissionIds)
  }
}

const hasMoreSubmissionPaths = computed(() => props.overview.submissionFolderPath.length > 1)
const submissionPathValue = computed(() =>
  hasMoreSubmissionPaths.value
    ? 'Click More to see all paths'
    : props.overview.submissionFolderPath[0]
)

onErrorCaptured((e) => {
  console.log(e)
  router.push({
    name: 'ErrorView',
    state: {
      message: 'Overview.json could not be found!',
      to: '/',
      routerInfo: 'back to FileUpload page'
    }
  })
  store().clearStore()
  return false
})
</script>
