<template>
  <div class="absolute bottom-0 left-0 right-0 top-0 flex flex-row space-x-5 p-5">
    <Container class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden">
      <h2>Run Options:</h2>

      <ScrollableComponent class="flex-grow px-4 pt-2">
        <TextInformation label="Submission Directory" class="pb-1">{{
          overview.submissionFolderPath.join(', ')
        }}</TextInformation>
        <TextInformation label="Basecode Directory" class="pb-1">{{
          overview.baseCodeFolderPath
        }}</TextInformation>
        <TextInformation label="Language" class="pb-1">{{ overview.language }}</TextInformation>
        <TextInformation label="File Extentions" class="pb-1">{{
          overview.fileExtensions.join(', ')
        }}</TextInformation>
        <TextInformation label="Min Token Match" class="pb-1">{{
          overview.matchSensitivity
        }}</TextInformation>
        <TextInformation label="Result File Name">{{
          store().state.uploadedFileName
        }}</TextInformation>
      </ScrollableComponent>
    </Container>

    <Container class="flex max-h-0 min-h-full flex-1 flex-col overflow-hidden">
      <h2>Run Data:</h2>

      <ScrollableComponent class="flex-grow px-4 pt-2">
        <TextInformation label="Date of Execution" class="pb-1">{{
          overview.dateOfExecution
        }}</TextInformation>
        <TextInformation label="Execution Duration" class="pb-1"
          >{{ overview.durationOfExecution }} ms</TextInformation
        >
        <TextInformation label="Total Submissions" class="pb-1">{{
          store().getSubmissionIds.length
        }}</TextInformation>
        <TextInformation label="Total Comparisons" class="pb-1">{{
          overview.totalComparisons
        }}</TextInformation>
        <TextInformation label="Shown Comparisons" class="pb-1">{{
          overview.shownComparisons
        }}</TextInformation>
        <TextInformation label="Missing Comparisons" class="pb-1">{{
          overview.missingComparisons
        }}</TextInformation>
      </ScrollableComponent>
    </Container>
  </div>
</template>

<script setup lang="ts">
import Container from '@/components/ContainerComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import ScrollableComponent from '@/components/ScrollableComponent.vue'
import { store } from '@/stores/store'
import { Overview } from '@/model/Overview'
import { onErrorCaptured, type PropType } from 'vue'
import { redirectOnError } from '@/router'

defineProps({
  overview: {
    type: Object as PropType<Overview>,
    required: true
  }
})

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying information:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>
