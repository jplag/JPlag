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
import { redirectOnError } from '@/router'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import type { CliOptions } from '@/model/CliOptions'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import type { RunInformation } from '@/model/RunInformation'
import { RunInformationFactory } from '@/model/factories/RunInformationFactory'
import { TopComparisonFactory } from '@/model/factories/TopComparisonFactory'

const runInformation: Ref<RunInformation | null> = ref(null)
const cliOptions: Ref<CliOptions | undefined> = ref(undefined)
const topComparisonCount: Ref<number | null> = ref(null)

RunInformationFactory.getRunInformation()
  .then((r) => (runInformation.value = r))
  .catch((error) =>
    redirectOnError(error, 'Could not load run information:\n', 'OverviewView', 'Back to overview')
  )

OptionsFactory.getCliOptions()
  .then((o) => (cliOptions.value = o))
  .catch((error) =>
    redirectOnError(error, 'Could not load run information:\n', 'OverviewView', 'Back to overview')
  )

// we can provide the clusters as an empty array as we are only interested in the number of the top comparisons
TopComparisonFactory.getTopComparisons([])
  .then((r) => (topComparisonCount.value = r.length))
  .catch((error) =>
    redirectOnError(error, 'Could not load run information:\n', 'OverviewView', 'Back to overview')
  )
</script>
