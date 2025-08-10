<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <OverviewView v-if="reportStore().isReportLoaded()" class="flex-1 print:flex-none" />
    <div v-else class="flex flex-1 flex-col items-center justify-center">
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import OverviewView from '@/views/OverviewView.vue'
import { LoadingCircle } from '@jplag/ui-components/base'
import VersionRepositoryReference from '../components/VersionRepositoryReference.vue'
import { reportStore } from '@/stores/reportStore'
import { loadReport } from '@/stores/fileLoading'
import { redirectOnError } from '@/router'

if (!reportStore().isReportLoaded()) {
  loadReport().catch((error) => redirectOnError(error))
}
</script>
