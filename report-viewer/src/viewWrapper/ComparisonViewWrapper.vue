<template>
  <div>
    <ComparisonView v-if="comparison && language" :comparison="comparison" :language="language" />
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
import ComparisonView from '@/views/ComparisonView.vue'
import type { Comparison } from '@/model/Comparison'
import { ComparisonFactory } from '@/model/factories/ComparisonFactory'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError } from '@/router'
import type { Language } from '@/model/Language'
import RepositoryReference from '@/components/RepositoryReference.vue'

const props = defineProps({
  comparisonFileName: {
    type: String,
    required: true
  }
})

const comparison: Ref<Comparison | null> = ref(null)
const language: Ref<Language | null> = ref(null)

// This eslint rule is disabled to allow the use of await in the setup function. Disabling this rule is safe, because the props are gathered from the url, so changing them would reload the pafe anyway.
// eslint-disable-next-line vue/no-setup-props-reactivity-loss
ComparisonFactory.getComparison(props.comparisonFileName)
  .then((comp) => {
    comparison.value = comp
  })
  .catch((error) => {
    redirectOnError(error, 'Could not load comparison:\n', 'OverviewView', 'Back to overview')
  })

OverviewFactory.getOverview()
  .then((overview) => {
    language.value = overview.language
  })
  .catch((error) => {
    redirectOnError(error, 'Could not load coparison:\n')
  })
</script>
