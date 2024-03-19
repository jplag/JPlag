<template>
  <div>
    <ClusterView v-if="overview" :overview="overview" :cluster="overview.clusters[clusterIndex]" />
    <div
      v-else
      class="absolute bottom-0 left-0 right-0 top-0 flex flex-col items-center justify-center"
    >
      <LoadingCircle class="mx-auto" />
    </div>

    <RepositoryReference />
  </div>
</template>

<script setup lang="ts">
import { type Ref, ref, computed } from 'vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import ClusterView from '@/views/ClusterView.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import type { Overview } from '@/model/Overview'
import { redirectOnError } from '@/router'
import RepositoryReference from '@/components/RepositoryReference.vue'

const props = defineProps({
  clusterIndex: {
    type: String,
    required: true
  }
})

const clusterIndex = computed(() => parseInt(props.clusterIndex))

const overview: Ref<Overview | null> = ref(null)

OverviewFactory.getOverview()
  .then((o) => {
    overview.value = o
  })
  .catch((error) => {
    redirectOnError(error, 'Could not load cluster:\n', 'OverviewView', 'Back to overview')
  })
</script>
