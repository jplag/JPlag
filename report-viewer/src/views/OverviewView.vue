<!--
  A view displaying the overview file of a JPlag report.
-->
<template>
  <div class="absolute top-0 bottom-0 left-0 right-0 flex flex-col">
    <div class="relative top-0 left-0 right-0 p-5 pb-0 flex space-x-5">
      <Container class="flex-grow">
        <h2>JPlag Report</h2>
        <div class="flex flex-row space-x-5 items-center">
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
          <Button @click="router.push({ name: 'InfoView' })"> More </Button>
        </div>
      </Container>
    </div>

    <div class="relative bottom-0 right-0 left-0 flex flex-grow space-x-5 p-5 pt-5">
      <Container class="max-h-0 min-h-full flex flex-col flex-1">
        <h2>Distribution of Comparisons:</h2>
        <DistributionDiagram
          :distribution="overview.distribution[selectedDistributionDiagramMetric]"
          class="!w-full !h-2/3"
        />
        <div class="flex flex-col flex-grow space-y-1">
          <h3 class="text-lg underline">Options:</h3>
          <ScrollableComponent class="flex-grow space-y-2">
            <OptionsSelector
              name="Metric"
              :labels="['Average', 'Maximum']"
              @selection-changed="
                (i: number) => (selectedDistributionDiagramMetric = getMetricFromNumber(i))
              "
            />
          </ScrollableComponent>
        </div>
      </Container>

      <Container class="max-h-0 min-h-full flex-1 flex flex-col space-y-2">
        <div class="flex flex-row space-x-8 items-center">
          <h2>Top Comparisons:</h2>
          <SearchBarComponent
            placeholder="Filter/Unhide Comparisons"
            class="flex-grow"
            @input-changed="(value) => (searchString = value)"
          />
          <Button class="w-24" @click="changeAnnoymousForAll()">
            {{
              store().state.anonymous.size == store().getSubmissionIds.length
                ? 'Show All'
                : 'Hide All'
            }}
          </Button>
        </div>
        <OptionsSelector
          name="Sort By"
          :labels="['Average Similarity', 'Maximum Similarity']"
          @selection-changed="
            (index) => (comparisonTableSortingMetric = getMetricFromNumber(index))
          "
        />
        <ComparisonsTable
          :clusters="overview.clusters"
          :top-comparisons="displayedComparisons"
          class="flex-1 min-h-0"
        />
      </Container>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onErrorCaptured, ref, watch } from 'vue'
import router from '@/router'
import DistributionDiagram from '@/components/DistributionDiagram.vue'
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import store from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'
import Button from '@/components/ButtonComponent.vue'
import ScrollableComponent from '@/components/ScrollableComponent.vue'
import MetricType from '@/model/MetricType'
import SearchBarComponent from '@/components/SearchBarComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import OptionsSelector from '@/components/OptionsSelectorComponent.vue'

const overview = OverviewFactory.getOverview()

const searchString = ref('')
const comparisonTableSortingMetric = ref(MetricType.AVERAGE)

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

function getSortedComparisons(comparisons: ComparisonListElement[]) {
  comparisons.sort(
    (a, b) =>
      b.similarities[comparisonTableSortingMetric.value] -
      a.similarities[comparisonTableSortingMetric.value]
  )
  let index = 0
  comparisons.forEach((c) => {
    c.sortingPlace = index++
  })
  return overview.topComparisons
}

const displayedComparisons = computed(() => {
  const comparisons = getFilteredComparisons(getSortedComparisons(overview.topComparisons))
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

const selectedDistributionDiagramMetric = ref(MetricType.AVERAGE)

function getMetricFromNumber(metric: number) {
  if (metric == 0) {
    return MetricType.AVERAGE
  } else {
    return MetricType.MAXIMUM
  }
}

const hasMoreSubmissionPaths = overview.submissionFolderPath.length > 1
const submissionPathValue = hasMoreSubmissionPaths
  ? 'Click More to see all paths'
  : overview.submissionFolderPath[0]

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
