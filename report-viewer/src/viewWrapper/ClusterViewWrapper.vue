<template>
  <div>
    <ClusterView v-if="overview" :overview="overview" :cluster="overview.clusters[clusterIndex]" />
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
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import ClusterView from '@/views/ClusterView.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import type { Overview } from '@/model/Overview'
import { redirectOnError, router } from '@/router'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'

const props = defineProps({
  clusterIndex: {
    type: String,
    required: true
  }
})

const clusterIndex = computed(() => parseInt(props.clusterIndex))

const overview: Ref<Overview | null> = ref(null)

OverviewFactory.getOverview()
  .then((r) => {
    if (r.result == 'success') {
      overview.value = r.overview
    } else if (r.result == 'oldReport') {
      router.push({ name: 'OldVersionRedirectView', params: { version: r.version.toString() } })
    }
  })
  .catch((error) => {
    redirectOnError(error, 'Could not load cluster:\n', 'OverviewView', 'Back to overview')
  })
</script>
