<template>
  <div class="flex flex-col gap-1 md:overflow-hidden print:w-full">
    <ComparisonView
      v-if="reportStore().isReportLoaded()"
      :first-submission-id="firstSubmissionId"
      :second-submission-id="secondSubmissionId"
      class="flex-1 print:w-full print:flex-none"
    />
    <div v-else class="flex flex-1 flex-col items-center justify-center">
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import ComparisonView from '@/views/ComparisonView.vue'
import { reportStore } from '@/stores/reportStore'
import { loadReport } from '@/stores/fileLoading'
import { redirectOnError } from '@/router'
import { LoadingCircle } from '@jplag/ui-components/base'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'

defineProps({
  firstSubmissionId: {
    type: String,
    required: true
  },
  secondSubmissionId: {
    type: String,
    required: true
  }
})

if (!reportStore().isReportLoaded()) {
  loadReport().catch((error) => redirectOnError(error))
}
</script>
