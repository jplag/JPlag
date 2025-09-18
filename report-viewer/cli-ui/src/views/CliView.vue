<template>
  <div class="flex flex-col gap-1 overflow-hidden">
    <div class="h-full max-h-full flex-1 overflow-hidden">
      <div class="flex h-full max-h-full flex-col gap-5 overflow-hidden">
        <ContainerComponent class="row-start-1 h-fit">
          <div class="grid grid-cols-[1fr_auto] grid-rows-[auto_1fr] gap-2">
            <h1 class="col-start-1 row-start-1 text-2xl">JPlag</h1>

            <InteractableComponent
              class="col-start-2 row-span-2 row-start-1 h-fit border-green-900! bg-green-700! text-white"
              @click="run()"
            >
              <div class="flex items-center gap-x-2 px-5">
                <FontAwesomeIcon :icon="faPlay" />
                Run
              </div>
            </InteractableComponent>

            <p class="col-start-1 row-start-2 text-sm">
              More information on each parameter can be found here:
              <a
                href="https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag"
                class="dark:text-link text-link-dark underline"
                >Parameter Description</a
              >
            </p>
          </div>
        </ContainerComponent>

        <div
          ref="scrollContainer"
          class="grid flex-1 grid-cols-2 gap-3 overflow-auto"
          @scroll="updateScrollOffset()"
        >
          <ComparisonOptionsCategory
            v-model:min-token-match="cliOptions.minimumTokenMatch"
            v-model:language="cliOptions.language"
            v-model:do-normalization="cliOptions.normalize"
            :scroll-offset-y="scrollOffsetY"
            class="col-start-1 row-start-1 h-full min-h-fit"
          />

          <ReportOptionsCategory
            v-model:result-file-name="cliOptions.resultFileName"
            v-model:overwrite-result-file="cliOptions.overwriteResultFile"
            v-model:generate-csv-file="cliOptions.generateCsvFile"
            v-model:shown-comparisons="cliOptions.maximumNumberOfComparisons"
            v-model:similarity-threshold="cliOptions.similarityThreshold"
            :scroll-offset-y="scrollOffsetY"
            class="col-start-2 row-start-1 h-full min-h-fit"
          />

          <SubmissionFilesCategory
            v-model:submission-directories="cliOptions.submissionDirectories"
            v-model:old-submission-directories="cliOptions.oldSubmissionDirectories"
            v-model:base-code-submission-directory="cliOptions.baseCodeSubmissionDirectory"
            :scroll-offset-y="scrollOffsetY"
            class="col-span-2 row-start-2 h-fit"
          />

          <ClusteringCategory
            v-model:enable-clustering="cliOptions.clusteringOptions.enabled"
            v-model:cluster-metric="cliOptions.clusteringOptions.similarityMetric"
            v-model:clustering-algorithm="cliOptions.clusteringOptions.algorithm"
            :scroll-offset-y="scrollOffsetY"
            class="col-start-1 row-start-3 min-h-fit"
          />

          <MatchMergingCategory
            v-model:enable-match-merging="cliOptions.mergingOptions.enabled"
            v-model:min-neighbor-length="cliOptions.mergingOptions.minimumNeighborLength"
            v-model:max-gap-size="cliOptions.mergingOptions.maximumGapSize"
            v-model:min-required-merges="cliOptions.mergingOptions.minimumRequiredMerges"
            :minimum-token-match="cliOptions.minimumTokenMatch"
            :scroll-offset-y="scrollOffsetY"
            class="col-start-2 row-start-3 min-h-fit"
          />

          <AdvancedOptionsCategory
            v-model:do-debug="cliOptions.debugParser"
            v-model:log-level="cliOptions.clusteringOptions.preprocessor"
            :scroll-offset-y="scrollOffsetY"
            class="col-span-2 row-start-4 h-fit"
          />
        </div>
      </div>
    </div>

    <VersionRepositoryReference :report-viewer-version="reportViewerVersion" />
  </div>
</template>

<script setup lang="ts">
import { faPlay } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { ref } from 'vue'
import {
  VersionRepositoryReference,
  ContainerComponent,
  InteractableComponent
} from '@jplag/ui-components/base'
import { reportViewerVersion } from '@jplag/version'
import ComparisonOptionsCategory from '@/components/categories/ComparisonOptionsCategory.vue'
import ReportOptionsCategory from '@/components/categories/ReportOptionsCategory.vue'
import SubmissionFilesCategory from '@/components/categories/SubmissionFilesCategory.vue'
import ClusteringCategory from '@/components/categories/ClusteringCategory.vue'
import MatchMergingCategory from '@/components/categories/MatchMergingCategory.vue'
import AdvancedOptionsCategory from '@/components/categories/AdvancedOptionsCategory.vue'
import { MetricJsonIdentifier, ParserLanguage } from '@jplag/model'
import { ExpandedOptions } from '@/model/ExpandedOptions'

const scrollContainer = ref<HTMLElement | null>(null)
const scrollOffsetY = ref(0)
function updateScrollOffset() {
  if (scrollContainer.value) {
    scrollOffsetY.value = scrollContainer.value.scrollTop
  }
}

const cliOptions = ref<ExpandedOptions>({
  resultFileName: 'results.jplag',
  minimumTokenMatch: 'default',
  language: ParserLanguage.JAVA,
  submissionDirectories: [],
  oldSubmissionDirectories: [],
  baseCodeSubmissionDirectory: '',
  subdirectoryName: '',
  fileSuffixes: [],
  exclusionFileName: '',
  similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
  similarityThreshold: 0,
  maximumNumberOfComparisons: 2500,
  debugParser: false,
  normalize: false,
  analyzeComments: false,
  overwriteResultFile: false,
  generateCsvFile: false,
  clusteringOptions: {
    enabled: true,
    similarityMetric: MetricJsonIdentifier.AVERAGE_SIMILARITY,
    spectralKernelBandwidth: 0,
    spectralGaussianProcessVariance: 0,
    spectralMinRuns: 0,
    spectralMaxRuns: 0,
    spectralMaxKMeansIterationPerRun: 0,
    agglomerativeThreshold: 0,
    preprocessor: '',
    algorithm: '',
    agglomerativeInterClusterSimilarity: '',
    preprocessorThreshold: 0,
    preprocessorPercentile: 0
  },
  mergingOptions: {
    enabled: false,
    minimumNeighborLength: 5,
    maximumGapSize: 2,
    minimumRequiredMerges: 7
  }
})

function run() {
  console.info('Running with options:', cliOptions.value)
}
</script>
