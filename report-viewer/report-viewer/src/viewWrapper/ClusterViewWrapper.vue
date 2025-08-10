<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <ClusterView
      v-if="reportStore().isReportLoaded()"
      :index="parsedIndex"
      class="flex-1 print:flex-none"
    />
    <div v-else class="flex flex-1 flex-col items-center justify-center">
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import ClusterView from '@/views/ClusterView.vue'
import LoadingCircle from '@jplag/ui-components/base/LoadingCircle.vue'
import VersionRepositoryReference from '../components/VersionRepositoryReference.vue'
import { reportStore } from '@/stores/reportStore'
import { loadReport } from '@/stores/fileLoading'
import { computed } from 'vue'
import { redirectOnError } from '@/router'

const props = defineProps({
  clusterIndex: {
    type: String,
    required: true
  }
})

const parsedIndex = computed(() => {
  const index = parseInt(props.clusterIndex)
  if (isNaN(index) || index < 0) {
    redirectOnError(new Error('Invalid cluster index provided.'))
    return -1
  }
  return index
})

if (!reportStore().isReportLoaded()) {
  loadReport().catch((error) => redirectOnError(error))
}
</script>
