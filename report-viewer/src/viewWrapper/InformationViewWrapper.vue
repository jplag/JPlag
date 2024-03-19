<template>
  <div>
    <InformationView v-if="overview" :overview="overview" :options="cliOptions" />
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
import { type Ref, ref } from 'vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import InformationView from '@/views/InformationView.vue'
import type { Overview } from '@/model/Overview'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError } from '@/router'
import { OptionsFactory } from '@/model/factories/OptionsFactory'
import type { CliOptions } from '@/model/CliOptions'
import RepositoryReference from '@/components/RepositoryReference.vue'

const overview: Ref<Overview | null> = ref(null)
const cliOptions: Ref<CliOptions | undefined> = ref(undefined)

OverviewFactory.getOverview()
  .then((o) => {
    overview.value = o
  })
  .catch((error) => {
    redirectOnError(error, 'Could not load information:\n', 'OverviewView', 'Back to overview')
  })

OptionsFactory.getCliOptions()
  .then((o) => (cliOptions.value = o))
  .catch((error) => console.log('Could not load full options.', error))
</script>
