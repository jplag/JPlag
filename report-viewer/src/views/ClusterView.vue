<template>
  <div class="absolute bottom-0 left-0 right-0 top-0 flex flex-col print:space-y-5">
    <div class="relative left-0 right-0 top-0 flex space-x-5 p-5 pb-0 print:p-0">
      <Container class="flex-grow overflow-hidden">
        <h2>Cluster</h2>
        <div class="flex flex-row items-center space-x-5">
          <TextInformation label="Average Similarity"
            >{{ (cluster.averageSimilarity * 100).toFixed(2) }}%</TextInformation
          >
        </div>
      </Container>
    </div>

    <div
      class="relative bottom-0 left-0 right-0 flex flex-grow justify-between space-x-5 px-5 pb-7 pt-5 print:grow-0 print:flex-col print:space-x-0 print:space-y-5 print:p-0"
    >
      <Container
        class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden print:max-h-none print:min-h-0 print:flex-none"
        v-if="cluster.members.length >= 35 || !canShowRadarChart"
      >
        <div
          class="flex max-h-full flex-col overflow-hidden print:flex-none"
          v-if="cluster.members.length < 35"
        >
          <ClusterGraph
            v-if="selectedClusterVisualization == 'Graph'"
            :cluster="clusterListElement"
            class="flex-grow print:max-h-full print:max-w-full print:flex-grow-0"
            @line-hovered="(value) => (highlightedElement = value)"
          />
        </div>
        <div v-else class="mx-auto space-y-5">
          <p class="text-center font-bold text-error">
            The cluster has too many members to be displayed as a graph or radar chart.
          </p>
          <p class="text-center font-bold text-gray-500 dark:text-gray-400">
            Consider whether this is an actual cluster or a false positive.
          </p>
        </div>
      </Container>
      <TabbedContainer
        class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden print:max-h-none print:min-h-0 print:flex-none"
        :tabs="clusterVisualizationOptions"
        v-else
      >
        <template #Graph>
          <ClusterGraph
            :cluster="clusterListElement"
            class="flex-grow print:max-h-full print:max-w-full print:flex-grow-0"
            @line-hovered="(value) => (highlightedElement = value)"
          />
        </template>
        <template #Radar>
          <ClusterRadarChart :cluster="clusterListElement" class="flex-grow" />
        </template>
      </TabbedContainer>

      <TabbedContainer
        class="flex max-h-0 min-h-full w-1/3 flex-col space-y-2 print:hidden"
        :tabs="comparisonTableOptions"
        :first-bottom-tooltip-index="1"
      >
        <template #Members>
          <ComparisonsTable
            :topComparisons="comparisons"
            class="max-h-0 min-h-full flex-1 overflow-hidden"
            header="Comparisons of Cluster Members:"
            :highlighted-row-ids="highlightedElement ?? undefined"
          >
            <template #footer v-if="comparisons.length < maxAmountOfComparisonsInCluster">
              <p class="w-full pt-1 text-center font-bold">
                Not all comparisons inside the cluster are shown. To see more, re-run JPlag with a
                higher maximum number argument.
              </p>
            </template>
          </ComparisonsTable>
        </template>
        <template #Related-Comparisons>
          <ComparisonsTable
            :topComparisons="relatedComparisons"
            class="max-h-0 min-h-full flex-1 overflow-hidden"
            header="Comparisons related to the Cluster:"
          />
        </template>
      </TabbedContainer>
    </div>
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
import { MetricType, MetricTypes } from '@/model/MetricType'
import type { Overview } from '@/model/Overview'
import { computed, ref, onErrorCaptured, type PropType, type Ref } from 'vue'
import { redirectOnError } from '@/router'
import TabbedContainer from '@/components/TabbedContainer.vue'

const props = defineProps({
  overview: {
    type: Object as PropType<Overview>,
    required: true
  },
  cluster: {
    type: Object as PropType<Cluster>,
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
const usedMetric: MetricType = MetricTypes.AVERAGE_SIMILARITY

const comparisons = computed(() =>
  props.overview.topComparisons.filter(
    (c) =>
      props.cluster.members.includes(c.firstSubmissionId) &&
      props.cluster.members.includes(c.secondSubmissionId)
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
  props.overview.topComparisons.filter(
    (c) =>
      (props.cluster.members.includes(c.firstSubmissionId) &&
        !props.cluster.members.includes(c.secondSubmissionId)) ||
      (!props.cluster.members.includes(c.firstSubmissionId) &&
        props.cluster.members.includes(c.secondSubmissionId))
  )
)
counter = 0
relatedComparisons.value
  .sort((a, b) => b.similarities[usedMetric.identifier] - a.similarities[usedMetric.identifier])
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
        similarity: c.similarities[usedMetric.identifier]
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

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying cluster:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>
