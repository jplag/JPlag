<template>
  <div
    class="grid grid-cols-1 grid-rows-[auto_600px_90vh] gap-5 md:grid-cols-[2fr_1fr] md:grid-rows-[auto_1fr] md:overflow-hidden print:grid-cols-1 print:grid-rows-[auto_1fr]"
  >
    <ContainerComponent class="col-start-1 row-start-1 md:col-end-3 md:row-end-2">
      <h2>Cluster</h2>
      <div class="flex flex-row items-center space-x-5">
        <span class="flex items-center gap-x-1">
          <MetricIcon class="h-3" :metric="MetricJsonIdentifier.AVERAGE_SIMILARITY" />
          <TextInformation label="Average Similarity" class="font-bold">{{
            MetricTypes.AVERAGE_SIMILARITY.format(cluster.averageSimilarity)
          }}</TextInformation>
        </span>
      </div>
    </ContainerComponent>

    <div class="col-start-1 row-start-2 flex flex-col overflow-hidden">
      <ContainerComponent
        v-if="cluster.members.length >= 35 || !canShowRadarChart"
        class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden print:max-h-none print:min-h-0 print:flex-none"
      >
        <div
          v-if="cluster.members.length < 35"
          class="flex max-h-full flex-col overflow-hidden print:flex-none"
        >
          <ClusterGraph
            v-if="selectedClusterVisualization == 'Graph'"
            :cluster="clusterListElement"
            :use-dark-mode="uiStore().useDarkMode"
            class="grow print:max-h-full print:max-w-full print:grow-0"
            @line-hovered="(value) => (highlightedElement = value)"
          />
        </div>
        <div v-else class="mx-auto space-y-5">
          <p class="text-error text-center font-bold">
            The cluster has too many members to be displayed as a graph or radar chart.
          </p>
          <p class="text-center font-bold text-gray-500 dark:text-gray-400">
            Consider whether this is an actual cluster or a false positive.
          </p>
        </div>
      </ContainerComponent>
      <TabbedContainer
        v-else
        class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden print:max-h-none print:min-h-0 print:flex-none"
        :tabs="clusterVisualizationOptions"
      >
        <template #Graph>
          <ClusterGraph
            :cluster="clusterListElement"
            class="grow print:max-h-full print:max-w-full print:grow-0"
            :highlighted-edge="hoveredEdge"
            :use-dark-mode="uiStore().useDarkMode"
            @line-hovered="(value) => (highlightedElement = value)"
          />
        </template>
        <template #Radar>
          <ClusterRadarChart
            :cluster="clusterListElement"
            :use-dark-mode="uiStore().useDarkMode"
            :get-display-name="reportStore().getDisplayName"
            class="grow"
          />
        </template>
      </TabbedContainer>
    </div>

    <TabbedContainer
      class="col-start-1 row-start-3 flex overflow-hidden md:col-start-2 md:row-start-2 print:hidden"
      :tabs="comparisonTableOptions"
      :first-bottom-tooltip-index="1"
    >
      <template #Members>
        <ComparisonTableWrapper
          :comparisons="comparisons"
          class="max-h-0 min-h-full flex-1 overflow-hidden"
          header="Comparisons of Cluster Members:"
          :highlighted-row-ids="highlightedElement ?? undefined"
          @line-hovered="(value) => (hoveredEdge = value)"
        >
          <template v-if="comparisons.length < maxAmountOfComparisonsInCluster" #footer>
            <p class="w-full pt-1 text-center font-bold">
              Not all comparisons inside the cluster are shown. To see more, re-run JPlag with a
              higher maximum number argument.
            </p>
          </template>
        </ComparisonTableWrapper>
      </template>
      <template #Related-Comparisons>
        <ComparisonTableWrapper
          :comparisons="relatedComparisons"
          class="max-h-0 min-h-full flex-1 overflow-hidden"
          header="Comparisons related to the Cluster:"
        />
      </template>
    </TabbedContainer>
  </div>
</template>

<script setup lang="ts">
import {
  ClusterRadarChart,
  ClusterGraph,
  MetricType,
  MetricTypes,
  MetricIcon
} from '@jplag/ui-components/widget'
import { ContainerComponent, TextInformation, TabbedContainer } from '@jplag/ui-components/base'
import { computed, ref, onErrorCaptured, type Ref } from 'vue'
import { redirectOnError } from '@/router'
import { ClusterListElement, ClusterListElementMember, MetricJsonIdentifier } from '@jplag/model'
import { reportStore } from '@/stores/reportStore'
import { uiStore } from '@/stores/uiStore'
import ComparisonTableWrapper from '@/components/ComparisonTableWrapper.vue'

const props = defineProps({
  index: {
    type: Number,
    required: true
  }
})

const cluster = computed(() => reportStore().getCluster(props.index))

const clusterMemberList = new Map() as ClusterListElementMember
const selectedClusterVisualization: Ref<'Graph' | 'Radar'> = ref('Graph')
const clusterVisualizationOptions = [
  {
    displayValue: 'Graph',
    tooltip: 'A graph having the average similarity between two submissions as the edges.'
  },
  {
    displayValue: 'Radar',
    tooltip:
      'A radar chart showing the he other submissions in the cluster, relative one submission.'
  }
]
const comparisonTableOptions = [
  {
    displayValue: 'Members',
    tooltip: 'Comparisons between the cluster members.'
  },
  {
    displayValue: 'Related Comparisons',
    tooltip: 'Comparisons between the cluster members\nand other submissions.'
  }
]
const usedMetric: MetricType = MetricTypes.AVERAGE_SIMILARITY

const comparisons = computed(() =>
  reportStore()
    .getTopComparisons()
    .filter(
      (c) =>
        cluster.value.members.includes(c.firstSubmissionId) &&
        cluster.value.members.includes(c.secondSubmissionId)
    )
)

let counter = 0
comparisons.value
  .sort((a, b) => b.similarities[usedMetric.identifier] - a.similarities[usedMetric.identifier])
  .forEach((c) => {
    c.sortingPlace = counter++
    c.id = counter
  })

const relatedComparisons = computed(() =>
  reportStore()
    .getTopComparisons()
    .filter(
      (c) =>
        (cluster.value.members.includes(c.firstSubmissionId) &&
          !cluster.value.members.includes(c.secondSubmissionId)) ||
        (!cluster.value.members.includes(c.firstSubmissionId) &&
          cluster.value.members.includes(c.secondSubmissionId))
    )
)
counter = 0
relatedComparisons.value
  .sort((a, b) => b.similarities[usedMetric.identifier] - a.similarities[usedMetric.identifier])
  .forEach((c) => {
    c.sortingPlace = counter++
    c.id = counter
  })

for (const member of cluster.value.members) {
  const membersComparisons: { matchedWith: string; similarity: number }[] = []
  comparisons.value
    .filter((c) => c.firstSubmissionId === member || c.secondSubmissionId === member)
    .forEach((c) => {
      membersComparisons.push({
        matchedWith: c.firstSubmissionId === member ? c.secondSubmissionId : c.firstSubmissionId,
        similarity: c.similarities[usedMetric.identifier]
      })
    })
  clusterMemberList.set(member, membersComparisons)
}

const clusterListElement: Ref<ClusterListElement> = computed(() => {
  return {
    averageSimilarity: cluster.value.averageSimilarity,
    members: clusterMemberList,
    strength: cluster.value.strength
  }
})

const canShowRadarChart = computed(
  () =>
    cluster.value.members.length >= 3 &&
    cluster.value.members.some((member) => (clusterMemberList.get(member)?.length ?? 0) >= 3)
)

/** The amount of comparisons if every single one was included */
const maxAmountOfComparisonsInCluster = computed(() => {
  return cluster.value.members.length ** 2 / 2 - cluster.value.members.length
})

const highlightedElement: Ref<{ firstId: string; secondId: string } | null> = ref(null)
const hoveredEdge: Ref<{ firstId: string; secondId: string } | null> = ref(null)

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying cluster:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>
