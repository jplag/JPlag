<template>
  <div>
    <ComparisonView
      v-if="
        comparison && language && firstBaseCodeMatches !== null && secondBaseCodeMatches !== null
      "
      :comparison="comparison"
      :language="language"
      :first-base-code-matches="firstBaseCodeMatches"
      :second-base-code-matches="secondBaseCodeMatches"
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
import ComparisonView from '@/views/ComparisonView.vue'
import type { Comparison } from '@/model/Comparison'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { redirectOnError, redirectToOldVersion } from '@/router'
import type { Language } from '@/model/Language'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import type { BaseCodeMatch } from '@/model/BaseCodeReport'
import { DataGetter, FileContentTypes } from '@/model/factories/DataGetter'
import type { CliOptions } from '@/model/CliOptions'

const props = defineProps({
  firstSubmissionId: {
    type: String,
    required: true
  },
  secondSubmissionId: {
    type: String,
    required: true
  }
})

const comparison: Ref<Comparison | null> = ref(null)
const language: Ref<Language | null> = ref(null)
const firstBaseCodeMatches: Ref<BaseCodeMatch[] | null> = ref(null)
const secondBaseCodeMatches: Ref<BaseCodeMatch[] | null> = ref(null)

// eslint-disable-next-line vue/no-setup-props-reactivity-loss
DataGetter.getFiles<{
  [FileContentTypes.COMPARISON]: Comparison
  [FileContentTypes.OPTIONS]: CliOptions
  [FileContentTypes.BASE_CODE_REPORT]: BaseCodeMatch[][]
}>([
  FileContentTypes.OPTIONS,
  {
    type: FileContentTypes.COMPARISON,
    firstSubmission: props.firstSubmissionId,
    secondSubmission: props.secondSubmissionId
  },
  {
    type: FileContentTypes.BASE_CODE_REPORT,
    submissionIds: [props.firstSubmissionId, props.secondSubmissionId]
  }
]).then((r) => {
  if (r.result == 'valid') {
    comparison.value = r.data[FileContentTypes.COMPARISON]
    language.value = r.data[FileContentTypes.OPTIONS].language
    firstBaseCodeMatches.value = r.data[FileContentTypes.BASE_CODE_REPORT][0]
    secondBaseCodeMatches.value = r.data[FileContentTypes.BASE_CODE_REPORT][1]
  } else if (r.result == 'versionError') {
    redirectToOldVersion(r.reportVersion)
  } else {
    redirectOnError(r.error, 'Could not load comparison:\n', 'ComparisonView', 'Back to overview')
  }
})
</script>
