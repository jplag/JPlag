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
