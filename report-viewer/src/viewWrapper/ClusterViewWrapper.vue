<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <ClusterView
      v-if="clusters !== null && topComparisons !== null"
      :top-comparisons="topComparisons"
      :cluster="clusters[clusterIndex]"
      class="flex-1 print:flex-none"
    />
    <div v-else class="flex flex-1 flex-col items-center justify-center">
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import { type Ref, ref, computed } from 'vue'
import ClusterView from '@/views/ClusterView.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError, redirectToOldVersion } from '@/router'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import type { Cluster } from '@/model/Cluster'
import { DataGetter, FileContentTypes } from '@/model/factories/DataGetter'

const props = defineProps({
  clusterIndex: {
    type: String,
    required: true
  }
})

const clusterIndex = computed(() => parseInt(props.clusterIndex))

const topComparisons: Ref<ComparisonListElement[] | null> = ref(null)
const clusters: Ref<Cluster[] | null> = ref(null)

DataGetter.getFiles<{
  [FileContentTypes.CLUSTER]: Cluster[]
  [FileContentTypes.TOP_COMPARISON]: ComparisonListElement[]
}>([FileContentTypes.CLUSTER, FileContentTypes.TOP_COMPARISON])
  .then((r) => {
    if (r.result == 'valid') {
      clusters.value = r.data[FileContentTypes.CLUSTER]
      topComparisons.value = r.data[FileContentTypes.TOP_COMPARISON]
    } else if (r.result == 'versionError') {
      redirectToOldVersion(r.reportVersion)
    } else {
      redirectOnError(r.error, 'Could not load clusters:\n', 'OverviewView', 'Back to overview')
    }
  })
  .catch((error) =>
    redirectOnError(error, 'Could not load clusters:\n', 'OverviewView', 'Back to overview')
  )
</script>
