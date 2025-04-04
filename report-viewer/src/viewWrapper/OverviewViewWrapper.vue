<template>
  <div>
    <OverviewView
      v-if="
        runInformation && topComparisons !== null && options && clusters !== null && distribution
      "
      :run-information="runInformation"
      :clusters="clusters"
      :top-comparisons="topComparisons"
      :options="options"
      :distributions="distribution"
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
import OverviewView from '@/views/OverviewView.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError } from '@/router'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import type { RunInformation } from '@/model/RunInformation'
import type { ComparisonListElement } from '@/model/ComparisonListElement'
import type { DistributionMap } from '@/model/Distribution'
import type { Cluster } from '@/model/Cluster'
import type { CliOptions } from '@/model/CliOptions'
import { RunInformationFactory } from '@/model/factories/RunInformationFactory'
import { TopComparisonFactory } from '@/model/factories/TopComparisonFactory'
import { ClusterFactory } from '@/model/factories/ClusterFactory'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import { DistributionFactory } from '@/model/factories/DistributionFactory'

const runInformation: Ref<RunInformation | null> = ref(null)
const topComparisons: Ref<ComparisonListElement[] | null> = ref(null)
const options: Ref<CliOptions | null> = ref(null)
const clusters: Ref<Cluster[] | null> = ref(null)
const distribution: Ref<DistributionMap | null> = ref(null)

RunInformationFactory.getRunInformation()
  .then((r) => (runInformation.value = r))
  .catch((error) => redirectOnError(error, 'Could not load run information:\n'))

ClusterFactory.getClusters()
  .then((r) => (clusters.value = r))
  .then(() => {
    if (clusters.value === null) {
      throw new Error('No clusters found')
    }
    return TopComparisonFactory.getTopComparisons(clusters.value)
  })
  .then((r) => (topComparisons.value = r))
  .catch((error) => redirectOnError(error, 'Could not load clusters or top comparisons:\n'))

OptionsFactory.getCliOptions()
  .then((o) => (options.value = o))
  .catch((error) => redirectOnError(error, 'Could not load options:\n'))

DistributionFactory.getDistributions()
  .then((r) => (distribution.value = r))
  .catch((error) => redirectOnError(error, 'Could not load distribution:\n'))
</script>
