<template>
  <div>
    <div v-for="category in categories" :key="category">
      <FailedSubmissionCategory :category="category" :submission-ids="categorized[category]" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { FailedSubmission, SubmissionState } from '@jplag/model'
import { computed } from 'vue'
import FailedSubmissionCategory from './FailedSubmissionCategory.vue'

const props = defineProps({
  submissions: {
    type: Array<FailedSubmission>,
    required: true
  }
})

const categorized = computed(() => {
  const categories: Record<SubmissionState, string[]> = {
    [SubmissionState.CANNOT_PARSE]: [],
    [SubmissionState.NOTHING_TO_PARSE]: [],
    [SubmissionState.TOO_SMALL]: [],
    [SubmissionState.UNPARSED]: [],
    [SubmissionState.VALID]: []
  }
  for (const submission of props.submissions) {
    const state = submission.submissionState
    if (!categories[state]) {
      categories[state] = []
    }
    categories[state].push(submission.submissionId)
  }
  return categories
})

const categories = computed(() =>
  [
    SubmissionState.CANNOT_PARSE,
    SubmissionState.NOTHING_TO_PARSE,
    SubmissionState.TOO_SMALL,
    SubmissionState.UNPARSED
  ].filter((category) => categorized.value[category].length > 0)
)
</script>
