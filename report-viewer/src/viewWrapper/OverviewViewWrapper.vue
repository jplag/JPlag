<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <OverviewView
      v-if="
        runInformation && topComparisons !== null && options && clusters !== null && distribution
      "
      :run-information="runInformation"
      :clusters="clusters"
      :top-comparisons="topComparisons"
      :options="options"
      :distributions="distribution"
      class="flex-1 print:flex-none"
    />
    <div v-else class="flex flex-1 flex-col items-center justify-center">
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import { type Ref, ref } from 'vue'
import OverviewView from '@/views/OverviewView.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError, redirectToOldVersion } from '@/router'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import type { RunInformation } from '@/model/RunInformation'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import type { DistributionMap } from '@/model/Distribution'
import type { Cluster } from '@/model/Cluster'
import type { CliOptions } from '@/model/CliOptions'
import { DataGetter, FileContentTypes } from '@/model/factories/DataGetter'

const runInformation: Ref<RunInformation | null> = ref(null)
const topComparisons: Ref<ComparisonListElement[] | null> = ref(null)
const options: Ref<CliOptions | null> = ref(null)
const clusters: Ref<Cluster[] | null> = ref(null)
const distribution: Ref<DistributionMap | null> = ref(null)

DataGetter.getFiles<{
  [FileContentTypes.RUN_INFORMATION]: RunInformation
  [FileContentTypes.TOP_COMPARISON]: ComparisonListElement[]
  [FileContentTypes.OPTIONS]: CliOptions
  [FileContentTypes.CLUSTER]: Cluster[]
  [FileContentTypes.DISTRIBUTION]: DistributionMap
}>([
  FileContentTypes.RUN_INFORMATION,
  FileContentTypes.TOP_COMPARISON,
  FileContentTypes.OPTIONS,
  FileContentTypes.CLUSTER,
  FileContentTypes.DISTRIBUTION
])
  .then((r) => {
    if (r.result == 'valid') {
      runInformation.value = r.data[FileContentTypes.RUN_INFORMATION]
      topComparisons.value = r.data[FileContentTypes.TOP_COMPARISON]
      options.value = r.data[FileContentTypes.OPTIONS]
      clusters.value = r.data[FileContentTypes.CLUSTER]
      distribution.value = r.data[FileContentTypes.DISTRIBUTION]
    } else if (r.result == 'versionError') {
      redirectToOldVersion(r.reportVersion)
    } else {
      redirectOnError(r.error, 'Could not load overview:\n')
    }
  })
  .catch((error) => redirectOnError(error, 'Could not load overview:\n'))
</script>
