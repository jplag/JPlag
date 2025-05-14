<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <ClusterView
      v-if="overview"
      :overview="overview"
      :cluster="overview.clusters[clusterIndex]"
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
