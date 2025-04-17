<template>
  <div>
    <InformationView
      v-if="runInformation && cliOptions && topComparisonCount !== null"
      :run-information="runInformation"
      :options="cliOptions"
      :top-comparisons-count="topComparisonCount"
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
import { type Ref, ref } from 'vue'
import InformationView from '@/views/InformationView.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError, redirectToOldVersion } from '@/router'
import type { CliOptions } from '@/model/CliOptions'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import type { RunInformation } from '@/model/RunInformation'
import { DataGetter, FileContentTypes } from '@/model/factories/DataGetter'
import type { ComparisonListElement } from '@/model/ComparisonListElement'

const runInformation: Ref<RunInformation | null> = ref(null)
const cliOptions: Ref<CliOptions | undefined> = ref(undefined)
const topComparisonCount: Ref<number | null> = ref(null)

DataGetter.getFiles<{
  [FileContentTypes.RUN_INFORMATION]: RunInformation
  [FileContentTypes.OPTIONS]: CliOptions
  [FileContentTypes.TOP_COMPARISON]: ComparisonListElement[]
}>([FileContentTypes.RUN_INFORMATION, FileContentTypes.OPTIONS, FileContentTypes.TOP_COMPARISON])
  .then((r) => {
    if (r.result == 'valid') {
      runInformation.value = r.data[FileContentTypes.RUN_INFORMATION]
      cliOptions.value = r.data[FileContentTypes.OPTIONS]
      topComparisonCount.value = r.data[FileContentTypes.TOP_COMPARISON].length
    } else if (r.result == 'versionError') {
      redirectToOldVersion(r.reportVersion)
    } else {
      redirectOnError(
        r.error,
        'Could not load run information:\n',
        'OverviewView',
        'Back to overview'
      )
    }
  })
  .catch((error) =>
    redirectOnError(error, 'Could not load run information:\n', 'OverviewView', 'Back to overview')
  )
</script>
