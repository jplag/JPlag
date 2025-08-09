<template>
  <div
    class="grid grid-cols-1 grid-rows-[auto_auto] gap-5 md:grid-cols-2 md:grid-rows-1 md:overflow-hidden print:grid-cols-1 print:grid-rows-[auto_auto]"
  >
    <ContainerComponent class="infoContainer print:border-none!">
      <h2>Run Options:</h2>

      <ScrollableComponent class="grow px-4 pt-2">
        <div class="space-y-2">
          <TextInformation label="Language">{{ options.language }}</TextInformation>
          <TextInformation label="Min Token Match">{{ options.minimumTokenMatch }}</TextInformation>
          <TextInformation label="Submission Directories">{{
            options.submissionDirectories.join(', ')
          }}</TextInformation>
          <TextInformation label="Old Directories">{{
            options.oldSubmissionDirectories.join(', ')
          }}</TextInformation>
          <TextInformation label="Base Directory">{{
            options.baseCodeSubmissionDirectory
          }}</TextInformation>
          <TextInformation label="Subdirectory Name">{{
            options.subdirectoryName
          }}</TextInformation>
          <TextInformation label="File Extensions">{{
            options.fileSuffixes.join(', ')
          }}</TextInformation>
          <TextInformation label="Exclusion File Name">{{
            options.exclusionFileName
          }}</TextInformation>
          <TextInformation label="Similarity Metric">{{
            MetricTypes.METRIC_MAP[options.similarityMetric].longName
          }}</TextInformation>
          <TextInformation label="Similarity Threshold">{{
            options.similarityThreshold
          }}</TextInformation>
          <TextInformation label="Max Comparison Count">{{
            options.maximumNumberOfComparisons
          }}</TextInformation>
          <TextInformation label="Result File Name">{{
            reportStore().getReportFileName()
          }}</TextInformation>

          <div v-if="options.clusteringOptions.enabled" class="mt-5! space-y-2">
            <h3 class="font-bold">Clustering:</h3>
            <TextInformation label="Similarity Metric">{{
              MetricTypes.METRIC_MAP[options.clusteringOptions.similarityMetric].longName
            }}</TextInformation>
            <TextInformation label="Algorithm">{{
              options.clusteringOptions.algorithm
            }}</TextInformation>

            <div
              v-if="options.clusteringOptions.algorithm.toLowerCase() == 'spectral'"
              class="space-y-2"
            >
              <TextInformation label="Spectral Bandwidth">{{
                options.clusteringOptions.spectralKernelBandwidth
              }}</TextInformation>
              <TextInformation label="Spectral Gaussian Process Variance">{{
                options.clusteringOptions.spectralGaussianProcessVariance
              }}</TextInformation>
              <TextInformation label="Spectral Min Runs">{{
                options.clusteringOptions.spectralMinRuns
              }}</TextInformation>
              <TextInformation label="Spectral Max Runs">{{
                options.clusteringOptions.spectralMaxRuns
              }}</TextInformation>
              <TextInformation label="K-Means Iterations">{{
                options.clusteringOptions.spectralMaxKMeansIterationPerRun
              }}</TextInformation>
            </div>

            <TextInformation v-else label="Agglomerative Threshold">{{
              options.clusteringOptions.agglomerativeThreshold
            }}</TextInformation>

            <TextInformation label="Preprocessor">{{
              options.clusteringOptions.preprocessor
            }}</TextInformation>
            <TextInformation label="Preprocessor Threshold">{{
              options.clusteringOptions.preprocessorThreshold
            }}</TextInformation>
            <TextInformation label="Preprocessor Percentile">{{
              options.clusteringOptions.preprocessorPercentile
            }}</TextInformation>
            <TextInformation label="Inter Cluster Similarity">{{
              options.clusteringOptions.agglomerativeInterClusterSimilarity
            }}</TextInformation>
          </div>

          <div v-if="options.mergingOptions.enabled" class="mt-5 space-y-2">
            <h3 class="font-bold">Match Merging:</h3>
            <TextInformation label="Min Neighbor Length">{{
              options.mergingOptions.minimumNeighborLength
            }}</TextInformation>
            <TextInformation label="Max Gap Size"
              >{{ options.mergingOptions.maximumGapSize }} }}</TextInformation
            >
            <TextInformation label="Min Required Merges"
              >{{ options.mergingOptions.minimumRequiredMerges }} }}</TextInformation
            >
          </div>
        </div>
      </ScrollableComponent>
    </ContainerComponent>

    <ContainerComponent class="infoContainer print:border-none!">
      <h2>Run Data:</h2>

      <ScrollableComponent class="grow px-4 pt-2">
        <TextInformation label="Date of Execution" class="pb-1">{{
          runInformation.dateOfExecution
        }}</TextInformation>
        <TextInformation label="Execution Duration" class="pb-1"
          >{{ runInformation.executionTime }} ms</TextInformation
        >
        <TextInformation label="Total Submissions" class="pb-1">{{
          reportStore().getSubmissionCount()
        }}</TextInformation>
        <TextInformation label="Total Comparisons" class="pb-1">{{
          runInformation.totalComparisons
        }}</TextInformation>
        <TextInformation label="Shown Comparisons" class="pb-1">{{
          reportStore().includedComparisonCount()
        }}</TextInformation>
        <TextInformation label="Missing Comparisons" class="pb-1">{{
          missingComparisons
        }}</TextInformation>
        <TextInformation label="Failed Submissions" class="pb-1">{{
          runInformation.failedSubmissions.length > 0
            ? runInformation.failedSubmissions
                .map((s) => `${s.submissionId} (${stringifySubmissionState(s.submissionState)})`)
                .join(', ')
            : 'None'
        }}</TextInformation>
      </ScrollableComponent>
    </ContainerComponent>
  </div>
</template>

<script setup lang="ts">
import { ContainerComponent, TextInformation, ScrollableComponent } from '@jplag/ui-components/base'
import { MetricTypes } from '@jplag/ui-components/widget'
import { SubmissionState } from '@jplag/model'
import { reportStore } from '@/stores/reportStore'
import { computed, onErrorCaptured } from 'vue'
import { redirectOnError } from '@/router'

const runInformation = computed(() => reportStore().getRunInformation())
const options = computed(() => reportStore().getCliOptions())

const missingComparisons = computed(
  () => runInformation.value.totalComparisons - reportStore().includedComparisonCount()
)

function stringifySubmissionState(reason: SubmissionState) {
  return reason.toString().replace(/_/g, ' ').toLowerCase()
}

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying information:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>

<style scoped>
@reference "@jplag/ui-components/style/style.css";

.infoContainer {
  @apply flex flex-col overflow-hidden print:min-h-fit print:overflow-visible;
}
</style>
