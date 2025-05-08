<!--
  A view displaying the overview file of a JPlag report.
-->
<template>
  <div
    class="grid grid-cols-1 grid-rows-[auto_800px_90vh] gap-5 md:grid-cols-2 md:grid-rows-[auto_1fr] md:overflow-hidden print:grid-cols-1 print:grid-rows-[auto_auto]"
  >
    <Container class="col-start-1 row-start-1 md:col-end-3 md:row-end-2">
      <h2>JPlag Report</h2>
      <div
        class="flex flex-col gap-x-5 gap-y-2 md:flex-row md:items-center print:flex-col print:items-start"
      >
        <TextInformation label="Submission Directory" class="flex-auto">{{
          submissionPathValue
        }}</TextInformation>
        <TextInformation label="Result name" class="flex-auto">{{
          store().state.uploadedFileName
        }}</TextInformation>
        <TextInformation label="Total Submissions" class="flex-auto">{{
          store().getSubmissionIds.length
        }}</TextInformation>

        <TextInformation label="Shown/Total Comparisons" class="flex-auto">
          <template #default
            >{{ overview.shownComparisons }} / {{ overview.totalComparisons }}</template
          >
          <template #tooltip>
            <div class="text-sm whitespace-pre">
              <TextInformation label="Shown Comparisons">{{
                overview.shownComparisons
              }}</TextInformation>
              <TextInformation label="Total Comparisons">{{
                overview.totalComparisons
              }}</TextInformation>
              <div v-if="overview.missingComparisons > 0">
                <TextInformation label="Missing Comparisons">{{
                  overview.missingComparisons
                }}</TextInformation>
                <p>
                  To include more comparisons in the report modify the number of shown comparisons
                  in the CLI.
                </p>
              </div>
            </div>
          </template>
        </TextInformation>

        <TextInformation label="Min Token Match" class="flex-auto">
          <template #default>
            {{ overview.matchSensitivity }}
          </template>
          <template #tooltip>
            <div class="text-sm whitespace-pre">
              <p>
                Tunes the comparison sensitivity by adjusting the minimum token required to be
                counted as a matching section.
              </p>
              <p>It can be adjusted in the CLI.</p>
            </div>
          </template>
        </TextInformation>

        <ToolTipComponent direction="left" class="grow-0 print:hidden" :show-info-symbol="false">
          <template #default>
            <Button @click="router.push({ name: 'InfoView' })"
              ><span class="flex">More <InfoIcon /></span
            ></Button>
          </template>
          <template #tooltip>
            <p class="text-sm whitespace-pre">More information about the CLI run of JPlag</p>
          </template>
        </ToolTipComponent>
      </div>
    </Container>

    <Container class="col-start-1 row-start-2 flex flex-col overflow-hidden print:overflow-visible">
      <h2>Distribution of Comparisons:</h2>
      <DistributionDiagram :distributions="overview.distribution" class="grow print:flex-none" />
    </Container>

    <Container
      class="col-start-1 row-start-3 flex overflow-hidden md:col-start-2 md:row-start-2 print:hidden"
    >
      <ComparisonsTable
        :clusters="overview.clusters"
        :top-comparisons="overview.topComparisons"
        class="min-h-0 max-w-full flex-1 print:min-h-full print:grow"
      >
        <template v-if="overview.topComparisons.length < overview.totalComparisons" #footer>
          <p class="w-full pt-1 text-center font-bold">
            Not all comparisons are shown. To see more, re-run JPlag with a higher maximum number
            argument.
          </p>
        </template>
      </ComparisonsTable>
    </Container>
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType, onErrorCaptured } from 'vue'
import { redirectOnError, router } from '@/router'
import DistributionDiagram from '@/components/distributionDiagram/DistributionDiagram.vue'
import ComparisonsTable from '@/components/ComparisonsTable.vue'
import { store } from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'
import Button from '@/components/ButtonComponent.vue'
import TextInformation from '@/components/TextInformation.vue'
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import { Overview } from '@/model/Overview'
import InfoIcon from '@/components/InfoIcon.vue'

const props = defineProps({
  overview: {
    type: Object as PropType<Overview>,
    required: true
  }
})

document.title = `${store().state.uploadedFileName} - JPlag Report Viewer`

const hasMoreSubmissionPaths = computed(() => props.overview.submissionFolderPath.length > 1)
const submissionPathValue = computed(() =>
  hasMoreSubmissionPaths.value
    ? 'Click More to see all paths'
    : props.overview.submissionFolderPath[0]
)

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying overview:\n')
  return false
})
</script>
