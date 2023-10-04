<template>
  <InformationView v-if="overview" :overview="overview" />
  <div
    v-else
    class="absolute bottom-0 left-0 right-0 top-0 flex flex-col items-center justify-center"
  >
    <LoadingCircle class="mx-auto" />
  </div>
</template>

<script setup lang="ts">
import { type Ref, ref } from 'vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'
import InformationView from '@/views/InformationView.vue'
import type { Overview } from '@/model/Overview'
import LoadingCircle from '@/components/LoadingCircle.vue'

const overview: Ref<Overview | null> = ref(null)

OverviewFactory.getOverview().then((o) => {
  overview.value = o
})
</script>
