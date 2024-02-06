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
      class="relative bottom-0 left-0 right-0 flex flex-grow justify-between space-x-5 p-5 pt-5 print:grow-0 print:flex-col print:space-x-0 print:space-y-5 print:p-0"
    >
      <Container
        class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden print:max-h-none print:min-h-0 print:flex-none"
      >
        <div
          class="flex max-h-full flex-col overflow-hidden print:flex-none"
          v-if="cluster.members.length < 35"
        >
          <OptionsSelectorComponent
            :labels="clusterVisualizationOptions"
            @selectionChanged="
              (index) => (selectedClusterVisualization = index == 0 ? 'Graph' : 'Radar')
            "
            title="Cluster Visualization:"
            class="mb-3"
            v-if="canShowRadarChart"
          />
          <ClusterRadarChart
            v-if="selectedClusterVisualization == 'Radar'"
            :cluster="clusterListElement"
            class="flex-grow"
          />
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
      <Container class="flex max-h-0 min-h-full w-1/3 flex-col space-y-2 print:hidden">
        <ComparisonsTable
          :topComparisons="comparisons"
          class="min-h-0 flex-1"
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
      </Container>
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
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import { MetricType } from '@/model/MetricType'
import type { Overview } from '@/model/Overview'
import { computed, ref, onErrorCaptured, type PropType, type Ref } from 'vue'
import OptionsSelectorComponent from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import { redirectOnError } from '@/router'

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

const comparisons = [] as Array<ComparisonListElement>
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
const usedMetric = MetricType.AVERAGE

function getComparisonFor(id1: string, id2: string) {
  return props.overview.topComparisons.find(
    (c) =>
      (c.firstSubmissionId === id1 && c.secondSubmissionId === id2) ||
      (c.firstSubmissionId === id2 && c.secondSubmissionId === id1)
  )
}

for (let i = 0; i < props.cluster.members.length; i++) {
  for (let j = i + 1; j < props.cluster.members.length; j++) {
    const comparison = getComparisonFor(props.cluster.members[i], props.cluster.members[j])
    if (comparison) {
      comparisons.push(comparison)
    }
  }
}
let counter = 0
comparisons
  .sort((a, b) => b.similarities[usedMetric] - a.similarities[usedMetric])
  .forEach((c) => {
    c.sortingPlace = counter++
    c.id = counter
  })

for (const member of props.cluster.members) {
  const membersComparisons: { matchedWith: string; similarity: number }[] = []
  comparisons
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

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying cluster:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>
