<template>
  <div>
    <ClusterView
      v-if="clusters !== null && topComparisons !== null"
      :top-comparisons="topComparisons"
      :cluster="clusters[clusterIndex]"
    />
    <div
      v-else
      class="absolute top-0 right-0 bottom-0 left-0 flex flex-col items-center justify-center"
    >
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import { type Ref, ref, computed } from 'vue'
import ClusterView from '@/views/ClusterView.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError } from '@/router'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import { TopComparisonFactory } from '@/model/factories/TopComparisonFactory'
import type { Cluster } from '@/model/Cluster'
import { ClusterFactory } from '@/model/factories/ClusterFactory'

const props = defineProps({
  clusterIndex: {
    type: String,
    required: true
  }
})

const clusterIndex = computed(() => parseInt(props.clusterIndex))

const topComparisons: Ref<ComparisonListElement[] | null> = ref(null)
const clusters: Ref<Cluster[] | null> = ref(null)

ClusterFactory.getClusters()
  .then((r) => (clusters.value = r))
  .then(() => {
    if (clusters.value === null) {
      throw new Error('No clusters found')
    }
    return TopComparisonFactory.getTopComparisons(clusters.value)
  })
  .then((r) => (topComparisons.value = r))
  .catch((error) =>
    redirectOnError(error, 'Could not load clusters:\n', 'OverviewView', 'Back to overview')
  )
</script>
