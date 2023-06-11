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
          <Button @click="router.push('test')"> More </Button>
        </div>
      </Container>
    </div>

    <div class="relative bottom-0 right-0 left-0 flex flex-grow space-x-5 p-5 pt-5">
      <Container class="max-h-0 min-h-full flex flex-col flex-1">
        <h2>Distribution of Comparisons:</h2>
        <DistributionDiagram
          :distribution="overview.distribution[selectedMetric].asLinearArray()"
          class="w-full h-2/3"
        />
        <div class="flex flex-col flex-grow space-y-1">
          <h3 class="text-lg underline">Options:</h3>
          <ScrollableComponent class="flex-grow space-y-2">
            <OptionsSelectorComponent
              name="Metric"
              :labels="['Average', 'Maximum']"
              @selection-changed="(i) => selectMetric(i)"
            />
          </ScrollableComponent>
        </div>
      </Container>

      <Container class="max-h-0 min-h-full flex-1 flex flex-col">
        <div class="flex flex-row space-x-8 items-center">
          <h2>Top Comparisons:</h2>
          <SearchBarComponent placeholder="Filter Comparisons" class="flex-grow" />
          <Button class="w-fit">Hide All</Button>
        </div>
        <ComparisonsTable
          :clusters="overview.clusters"
          :top-comparisons="overview.topComparisons"
          class="flex-1 min-h-0"
        />
      </Container>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onErrorCaptured, ref } from 'vue'
import router from '@/router'
import DistributionDiagram from '@/components/DistributionDiagram.vue'
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import store from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'
import Button from '@/components/ButtonComponent.vue'
import ScrollableComponent from '@/components/ScrollableComponent.vue'
import OptionsSelectorComponent from '@/components/OptionsSelectorComponent.vue'
import MetricType from '@/model/ui/MetricType'
import SearchBarComponent from '@/components/SearchBarComponent.vue'
import TextInformation from '@/components/TextInformation.vue'

const overview = OverviewFactory.getOverview()

/**
 * Handles the selection of an Id to anonymize.
 * If all submission ids are provided as parameter it hides or displays them based on their previous state.
 * If a single id is provided it hides all of the other ids except for the chosen one.
 * @param ids - IDs to hide
 */
function handleId(ids: Array<string>) {
  if (ids.length === store().getSubmissionIds.length) {
    if (store().anonymous.size > 0) {
      store().resetAnonymous()
    } else {
      store().addAnonymous(ids)
    }
  } else {
    if (store().anonymous.has(ids[0])) {
      store().removeAnonymous(ids)
    } else {
      if (store().anonymous.size === 0) {
        store().addAnonymous(store().getSubmissionIds.filter((s: string) => s !== ids[0]))
      } else {
        store().addAnonymous(ids)
      }
    }
  }
}

//Metrics
// const selectedMetric = ref(overview.metrics.map(() => false))

const selectedMetric = ref(MetricType.AVERAGE)

/**
 * Switch between metrics
 * @param metric Metric to switch to
 */
function selectMetric(metric: number) {
  selectedMetric.value = metric
  console.log('Selected metric: ' + metric)
}

const hasMoreSubmissionPaths = overview.submissionFolderPath.length > 1
const submissionPathValue = hasMoreSubmissionPaths
  ? 'Click arrow to see all paths'
  : overview.submissionFolderPath[0]

const shownComparisons = computed(() => {
  return overview.topComparisons.length
})
const missingComparisons = overview.totalComparisons - shownComparisons.value

onErrorCaptured(() => {
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
