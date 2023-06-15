<template>
  <div class="absolute top-0 bottom-0 left-0 right-0 flex flex-col">
    <div class="relative top-0 left-0 right-0 p-5 pb-0 flex space-x-5">
      <Container class="flex-grow overflow-hidden">
        <h2>Cluster</h2>
        <div class="flex flex-row space-x-5 items-center">
          <TextInformation label="Average Similarity"
            >{{ toTwoDecimals(cluster.averageSimilarity) }}%</TextInformation
          >
        </div>
      </Container>
    </div>

    <div class="relative bottom-0 right-0 left-0 flex flex-grow space-x-5 p-5 pt-5 justify-between">
      <Container class="max-h-0 min-h-full overflow-hidden flex-1 flex flex-col">
        <ClusterRadarChart :cluster="clusterListElement" class="flex-grow" />
      </Container>
      <Container class="max-h-0 min-h-full overflow-hidden w-1/3 space-y-2 flex flex-col">
        <h2>Comparisons of Cluster Members:</h2>
        <ComparisonsTable :topComparisons="comparisons" class="flex-1 min-h-0" />
      </Container>
    </div>
  </div>
</template>

<script setup lang="ts">
import ClusterRadarChart from '@/components/ClusterRadarChart.vue'
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import Container from '@/components/ContainerComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import type { ClusterListElement, ClusterListElementMember } from '@/model/ClusterListElement'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import { toTwoDecimals } from '@/utils/ComparisonUtils'

const props = defineProps({
  clusterIndex: {
    type: Number,
    required: true
  }
})

const overview = OverviewFactory.getOverview()
const cluster = overview.clusters[props.clusterIndex]
const comparisons = [] as Array<ComparisonListElement>
const clusterMemberList = new Map() as ClusterListElementMember

function getComparisonFor(id1: string, id2: string) {
  return overview.topComparisons.find(
    (c) =>
      (c.firstSubmissionId === id1 && c.secondSubmissionId === id2) ||
      (c.firstSubmissionId === id2 && c.secondSubmissionId === id1)
  )
}

for (let i = 0; i < cluster.members.length; i++) {
  for (let j = i + 1; j < cluster.members.length; j++) {
    const comparison = getComparisonFor(cluster.members[i], cluster.members[j])
    if (comparison) {
      comparisons.push(comparison)
    }
  }
}

for (const member of cluster.members) {
  const membersComparisons: { matchedWith: string; similarity: number }[] = []
  comparisons
    .filter((c) => c.firstSubmissionId === member || c.secondSubmissionId === member)
    .forEach((c) => {
      membersComparisons.push({
        matchedWith: c.firstSubmissionId === member ? c.secondSubmissionId : c.firstSubmissionId,
        similarity: c.averageSimilarity
      })
    })
  clusterMemberList.set(member, membersComparisons)
}

const clusterListElement: ClusterListElement = {
  averageSimilarity: cluster.averageSimilarity,
  members: clusterMemberList,
  strength: cluster.strength
}
</script>
