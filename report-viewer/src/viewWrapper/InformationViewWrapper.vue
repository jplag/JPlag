<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <InformationView
      v-if="overview && cliOptions"
      :overview="overview"
      :options="cliOptions"
      class="flex-1"
    />
    <div v-else class="flex flex-1 flex-col items-center justify-center">
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import { type Ref, ref } from 'vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import InformationView from '@/views/InformationView.vue'
import type { Overview } from '@/model/Overview'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError, router } from '@/router'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import type { CliOptions } from '@/model/CliOptions'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'

const overview: Ref<Overview | null> = ref(null)
const cliOptions: Ref<CliOptions | undefined> = ref(undefined)

OverviewFactory.getOverview()
  .then((r) => {
    if (r.result == 'success') {
      overview.value = r.overview
    } else if (r.result == 'oldReport') {
      router.push({ name: 'OldVersionRedirectView', params: { version: r.version.toString() } })
    }
  })
  .catch((error) => {
    redirectOnError(error, 'Could not load information:\n', 'OverviewView', 'Back to overview')
  })

OptionsFactory.getCliOptions()
  .then((o) => (cliOptions.value = o))
  .catch((error) => console.error('Could not load full options.', error))
</script>
