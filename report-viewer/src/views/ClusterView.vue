<template>
  <div
    class="grid grid-cols-1 grid-rows-[auto_600px_90vh] gap-5 md:grid-cols-[2fr_1fr] md:grid-rows-[auto_1fr] md:overflow-hidden print:grid-cols-1 print:grid-rows-[auto_1fr]"
  >
    <Container class="col-start-1 row-start-1 md:col-end-3 md:row-end-2">
      <h2>Cluster</h2>
      <div class="flex flex-row items-center space-x-5">
        <TextInformation label="Average Similarity"
          >{{ (cluster.averageSimilarity * 100).toFixed(2) }}%</TextInformation
        >
      </div>
    </Container>

    <div class="col-start-1 row-start-2 flex flex-col overflow-hidden">
      <Container
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
      </Container>
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
            @line-hovered="(value) => (highlightedElement = value)"
          />
        </template>
        <template #Radar>
          <ClusterRadarChart :cluster="clusterListElement" class="grow" />
        </template>
      </TabbedContainer>
    </div>

    <TabbedContainer
      class="col-start-1 row-start-3 flex overflow-hidden md:col-start-2 md:row-start-2 print:hidden"
      :tabs="comparisonTableOptions"
      :first-bottom-tooltip-index="1"
    >
      <template #Members>
        <ComparisonsTable
          :top-comparisons="comparisons"
          class="max-h-0 min-h-full flex-1 overflow-hidden"
          header="Comparisons of Cluster Members:"
          :highlighted-row-ids="highlightedElementProp"
          @line-hovered="(value) => (hoveredEdge = value)"
        >
          <template v-if="comparisons.length < maxAmountOfComparisonsInCluster" #footer>
            <p class="w-full pt-1 text-center font-bold">
              Not all comparisons inside the cluster are shown. To see more, re-run JPlag with a
              higher maximum number argument.
            </p>
          </template>
        </ComparisonsTable>
      </template>
      <template #Related-Comparisons>
        <ComparisonsTable
          :top-comparisons="relatedComparisons"
          class="max-h-0 min-h-full flex-1 overflow-hidden"
          header="Comparisons related to the Cluster:"
        />
      </template>
    </TabbedContainer>
  </div>
</template>

<script setup lang="ts">
import ClusterRadarChart from '@/components/ClusterRadarChart.vue'
import ClusterGraph from '@/components/ClusterGraph.vue'
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import Container from '@/components/ContainerComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import type { Cluster } from '@/model/Cluster'
import type { ClusterListElement, ClusterListElementMember } from '@/model/ClusterListElement'
import { MetricType } from '@/model/MetricType'
import { computed, ref, onErrorCaptured, type PropType, type Ref } from 'vue'
import { redirectOnError } from '@/router'
import TabbedContainer from '@/components/TabbedContainer.vue'
import type { ComparisonListElement } from '@/model/ComparisonListElement'

const props = defineProps({
  cluster: {
    type: Object as PropType<Cluster>,
    required: true
  },
  topComparisons: {
    type: Array<ComparisonListElement>,
    required: true
  }
})

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
const usedMetric = MetricType.AVERAGE

const comparisons = computed(() =>
  props.topComparisons.filter(
    (c) =>
      props.cluster.members.includes(c.firstSubmissionId) &&
      props.cluster.members.includes(c.secondSubmissionId)
  )
)

let counter = 0
comparisons.value
  .sort((a, b) => b.similarities[usedMetric] - a.similarities[usedMetric])
  .forEach((c) => {
    c.sortingPlace = counter++
    c.id = counter
  })

const relatedComparisons = computed(() =>
  props.topComparisons.filter(
    (c) =>
      (props.cluster.members.includes(c.firstSubmissionId) &&
        !props.cluster.members.includes(c.secondSubmissionId)) ||
      (!props.cluster.members.includes(c.firstSubmissionId) &&
        props.cluster.members.includes(c.secondSubmissionId))
  )
)
counter = 0
relatedComparisons.value
  .sort((a, b) => b.similarities[usedMetric] - a.similarities[usedMetric])
  .forEach((c) => {
    c.sortingPlace = counter++
    c.id = counter
  })

for (const member of props.cluster.members) {
  const membersComparisons: { matchedWith: string; similarity: number }[] = []
  comparisons.value
    .filter((c) => c.firstSubmissionId === member || c.secondSubmissionId === member)
    .forEach((c) => {
      membersComparisons.push({
        matchedWith: c.firstSubmissionId === member ? c.secondSubmissionId : c.firstSubmissionId,
        similarity: c.similarities[usedMetric]
      })
    })
  clusterMemberList.set(member, membersComparisons)
}

const clusterListElement: Ref<ClusterListElement> = computed(() => {
  return {
    averageSimilarity: props.cluster.averageSimilarity,
    members: clusterMemberList,
    strength: props.cluster.strength
  }
})

const canShowRadarChart = computed(
  () =>
    props.cluster.members.length >= 3 &&
    props.cluster.members.some((member) => (clusterMemberList.get(member)?.length ?? 0) >= 3)
)

/** The amount of comparisons if every single one was included */
const maxAmountOfComparisonsInCluster = computed(() => {
  return props.cluster.members.length ** 2 / 2 - props.cluster.members.length
})

const highlightedElement: Ref<{ firstId: string; secondId: string } | null> = ref(null)
const hoveredEdge: Ref<{ firstId: string; secondId: string } | null> = ref(null)
const highlightedElementProp = computed(() => {
  if (highlightedElement.value) {
    return {
      scroll: true,
      ids: [highlightedElement.value]
    }
  }
  return {
    scroll: false,
    ids: []
  }
})

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying cluster:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>
