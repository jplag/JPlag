<template>
  <div class="absolute bottom-0 left-0 right-0 top-0 flex flex-col">
    <div class="relative left-0 right-0 top-0 flex space-x-5 p-5 pb-0">
      <Container class="flex-grow overflow-hidden">
        <h2>Cluster</h2>
        <div class="flex flex-row items-center space-x-5">
          <TextInformation label="Average Similarity"
            >{{ (cluster.averageSimilarity * 100).toFixed(2) }}%</TextInformation
          >
        </div>
      </Container>
    </div>

    <div class="relative bottom-0 left-0 right-0 flex flex-grow justify-between space-x-5 p-5 pt-5">
      <Container class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden">
        <ClusterRadarChart :cluster="clusterListElement" class="flex-grow" />
      </Container>
      <Container class="flex max-h-0 min-h-full w-1/3 flex-col space-y-2">
        <h2>Comparisons of Cluster Members:</h2>
        <ComparisonsTable :topComparisons="comparisons" class="min-h-0 flex-1">
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
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import Container from '@/components/ContainerComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import type { Cluster } from '@/model/Cluster'
import type { ClusterListElement, ClusterListElementMember } from '@/model/ClusterListElement'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import { MetricType } from '@/model/MetricType'
import type { Overview } from '@/model/Overview'
import { redirectOnError } from '@/router'
import { computed, onErrorCaptured, type PropType, type Ref } from 'vue'

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

/** The amount of comparisons if every single one was included */
const maxAmountOfComparisonsInCluster = computed(() => {
  return props.cluster.members.length ** 2 / 2 - props.cluster.members.length
})

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying cluster:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>
