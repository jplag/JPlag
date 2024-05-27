<template>
  <div
    class="absolute bottom-0 left-0 right-0 top-0 flex flex-row space-x-5 p-5 pb-7 print:flex-col print:space-x-0 print:space-y-2 print:p-0"
  >
    <Container class="infoContainer print:!border-none">
      <h2>Run Options:</h2>

      <ScrollableComponent class="flex-grow px-4 pt-2">
        <div class="space-y-2">
          <TextInformation label="Language">{{ options.language }}</TextInformation>
          <TextInformation label="Min Token Match">{{ options.minTokenMatch }}</TextInformation>
          <TextInformation label="Submission Directories">{{
            options.submissionDirectories.join(', ')
          }}</TextInformation>
          <TextInformation label="Old Directories">{{
            options.oldDirectories.join(', ')
          }}</TextInformation>
          <TextInformation label="Base Directory">{{ options.baseDirectory }}</TextInformation>
          <TextInformation label="Subdirectory Name">{{
            options.subDirectoryName
          }}</TextInformation>
          <TextInformation label="File Suffixes">{{
            options.fileSuffixes.join(', ')
          }}</TextInformation>
          <TextInformation label="Exclusion File Name">{{
            options.exclusionFileName
          }}</TextInformation>
          <TextInformation label="Similarity Metric">{{
            metricToolTips[options.similarityMetric].longName
          }}</TextInformation>
          <TextInformation label="Similarity Threshold">{{
            options.similarityThreshold
          }}</TextInformation>
          <TextInformation label="Max Comparison Count">{{
            options.maxNumberComparisons
          }}</TextInformation>
          <TextInformation label="Result File Name">{{
            store().state.uploadedFileName
          }}</TextInformation>

          <div class="!mt-5 space-y-2" v-if="options.clusterOptions.enabled">
            <h3 class="font-bold">Clustering:</h3>
            <TextInformation label="Similarity Metric">{{
              metricToolTips[options.clusterOptions.similarityMetric].longName
            }}</TextInformation>
            <TextInformation label="Algorithm">{{
              options.clusterOptions.algorithm
            }}</TextInformation>

            <div
              v-if="options.clusterOptions.algorithm.toLowerCase() == 'spectral'"
              class="space-y-2"
            >
              <TextInformation label="Spectral Bandwidth">{{
                options.clusterOptions.spectralBandwidth
              }}</TextInformation>
              <TextInformation label="Spectral Gausssian Process Variance">{{
                options.clusterOptions.spectralGaussianProcessVariance
              }}</TextInformation>
              <TextInformation label="Spectral Min Runs">{{
                options.clusterOptions.spectralMinRuns
              }}</TextInformation>
              <TextInformation label="Spectral Max Runs">{{
                options.clusterOptions.spectralMaxRuns
              }}</TextInformation>
              <TextInformation label="K-Means Iterations">{{
                options.clusterOptions.spectralMaxKMeansIterations
              }}</TextInformation>
            </div>

            <TextInformation v-else label="Agglomerative Treshold">{{
              options.clusterOptions.agglomerativeThreshold
            }}</TextInformation>

            <TextInformation label="Preprocessor">{{
              options.clusterOptions.preprocessor
            }}</TextInformation>
            <TextInformation label="Preprocessor Threshold">{{
              options.clusterOptions.preprocessorThreshold
            }}</TextInformation>
            <TextInformation label="Preprocessor Percentile">{{
              options.clusterOptions.preprocessorPercentile
            }}</TextInformation>
            <TextInformation label="Inter Cluster Similarity">{{
              options.clusterOptions.interClusterSimilarity
            }}</TextInformation>
          </div>

          <div class="mt-5 space-y-2" v-if="options.mergingOptions.enabled">
            <h3 class="font-bold">Match Merging:</h3>
            <TextInformation label="Min Neighbor Length">{{
              options.mergingOptions.minNeighborLength
            }}</TextInformation>
            <TextInformation label="Max Gap Size"
              >{{ options.mergingOptions.maxGapSize }} }}</TextInformation
            >
          </div>
        </div>
      </ScrollableComponent>
    </Container>

    <Container class="infoContainer print:!border-none">
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
import type { CliOptions } from '@/model/CliOptions'
import { metricToolTips } from '@/model/MetricType'

defineProps({
  overview: {
    type: Object as PropType<Overview>,
    required: true
  },
  options: {
    type: Object as PropType<CliOptions>,
    required: true
  }
})

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying information:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>

<style scoped lang="postcss">
.infoContainer {
  @apply flex max-h-0 min-h-full flex-1 flex-col overflow-hidden print:max-h-none print:min-h-0 print:flex-none;
}
</style>
