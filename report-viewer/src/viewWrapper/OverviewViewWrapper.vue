<template>
  <div>
    <OverviewView v-if="overview" :overview="overview" />
    <div
      v-else
      class="absolute bottom-0 left-0 right-0 top-0 flex flex-col items-center justify-center"
    >
      <LoadingCircle class="mx-auto" />
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import { type Ref, ref } from 'vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import OverviewView from '@/views/OverviewView.vue'
import type { Overview } from '@/model/Overview'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError, router } from '@/router'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'

const overview: Ref<Overview | null> = ref(null)

OverviewFactory.getOverview()
  .then((r) => {
    if (r.result == 'success') {
      overview.value = r.overview
    } else if (r.result == 'oldReport') {
      router.push({ name: 'OldVersionRedirectView', params: { version: r.version.toString() } })
    }
  })
  .catch((error) => {
    redirectOnError(error, 'Could not load overview:\n')
  })
</script>
