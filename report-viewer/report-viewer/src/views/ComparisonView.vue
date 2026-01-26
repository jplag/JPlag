<!--
  A view displaying the .json file of a comparison from a JPlag report.
-->
<template>
  <div
    class="grid grid-cols-1 grid-rows-[min-content_auto_auto] gap-5 md:grid-cols-2 md:grid-rows-[auto_1fr] md:overflow-hidden"
  >
    <ContainerComponent class="col-start-1 row-start-1 md:col-end-3 md:row-end-2">
      <h2>
        Comparison:
        {{ reportStore().getDisplayName(comparison.firstSubmissionId) }}
        -
        {{ reportStore().getDisplayName(comparison.secondSubmissionId) }}
        <ToolTipComponent
          direction="left"
          class="float-right hidden md:block print:hidden"
          :show-info-symbol="false"
        >
          <template #tooltip>
            <p class="text-sm whitespace-pre">
              Printing works best in landscape mode on Chromium based browsers
            </p>
          </template>
          <template #default>
            <ButtonComponent
              class="hidden h-10 w-10 items-center justify-center md:flex"
              @click="print()"
            >
              <FontAwesomeIcon class="text-2xl" :icon="faPrint" />
            </ButtonComponent>
          </template>
        </ToolTipComponent>
      </h2>
      <div class="flex flex-col gap-x-10 gap-y-2 md:flex-row">
        <span class="flex items-center gap-x-1">
          <MetricIcon class="h-3" :metric="MetricJsonIdentifier.AVERAGE_SIMILARITY" />
          <TextInformation label="Average Similarity" class="font-bold">{{
            MetricTypes.AVERAGE_SIMILARITY.format(
              comparison.similarities[MetricTypes.AVERAGE_SIMILARITY.identifier]
            )
          }}</TextInformation>
        </span>

        <TextInformation
          :label="`Similarity ${reportStore().getDisplayName(comparison.firstSubmissionId)}`"
          tooltip-side="right"
        >
          <template #default>{{ (comparison.firstSimilarity * 100).toFixed(2) }}%</template>
          <template #tooltip
            ><div class="text-sm whitespace-pre">
              <p>
                Percentage of code from
                {{ reportStore().getDisplayName(comparison.firstSubmissionId) }} that was found in
                the code of {{ reportStore().getDisplayName(comparison.secondSubmissionId) }}.
              </p>
              <p>
                The numbers might not be symmetric, due to the submissions having different lengths.
              </p>
            </div></template
          >
        </TextInformation>
        <TextInformation
          :label="`Similarity ${reportStore().getDisplayName(comparison.secondSubmissionId)}`"
          tooltip-side="right"
          ><template #default>{{ (comparison.secondSimilarity * 100).toFixed(2) }}%</template>
          <template #tooltip
            ><div class="text-sm whitespace-pre">
              <p>
                Percentage of code from
                {{ reportStore().getDisplayName(comparison.secondSubmissionId) }} that was found in
                the code of {{ reportStore().getDisplayName(comparison.firstSubmissionId) }}.
              </p>
              <p>
                The numbers might not be symmetric, due to the submissions having different lengths.
              </p>
            </div></template
          ></TextInformation
        >
      </div>
      <MatchList
        :display-name1="reportStore().getDisplayName(comparison.firstSubmissionId)"
        :display-name2="reportStore().getDisplayName(comparison.secondSubmissionId)"
        :matches="comparison.allMatches"
        :basecode-in-first="firstBaseCodeMatches"
        :basecode-in-second="secondBaseCodeMatches"
        @match-selected="showMatch"
      />
      <OptionsSelectorComponent
        ref="sortingOptionSelector"
        class="mt-2 print:hidden"
        title="File Sorting:"
        :labels="sortingOptions.map((o) => fileSortingTooltips[o])"
        :default-selected="sortingOptions.indexOf(uiStore().fileSorting)"
        @selection-changed="(index: number) => changeFileSorting(index)"
      />
    </ContainerComponent>
    <div ref="styleholder" class="col-span-0 row-span-0"></div>
    <FilesContainer
      ref="panel1"
      :files="comparison.filesOfFirstSubmission"
      :matches="comparison.matchesInFirstSubmission"
      :file-owner-display-name="reportStore().getDisplayName(comparison.firstSubmissionId)"
      :highlight-language="reportStore().getCliOptions().language"
      :base-code-matches="firstBaseCodeMatches"
      class="col-start-1 row-start-2 flex flex-col overflow-hidden"
      @match-selected="showMatchInSecond"
      @files-moved="filesMoved()"
    />
    <FilesContainer
      ref="panel2"
      :files="comparison.filesOfSecondSubmission"
      :matches="comparison.matchesInSecondSubmissions"
      :file-owner-display-name="reportStore().getDisplayName(comparison.secondSubmissionId)"
      :highlight-language="reportStore().getCliOptions().language"
      :base-code-matches="secondBaseCodeMatches"
      class="col-start-1 row-start-3 flex overflow-hidden md:col-start-2 md:row-start-2"
      @match-selected="showMatchInFirst"
      @files-moved="filesMoved()"
    />
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faPrint } from '@fortawesome/free-solid-svg-icons'
import { onMounted, ref, watch, type Ref, computed, onErrorCaptured } from 'vue'
import hljsLightMode from 'highlight.js/styles/vs.css?raw'
import hljsDarkMode from 'highlight.js/styles/vs2015.css?raw'
import { redirectOnError } from '@/router'
import {
  ContainerComponent,
  ButtonComponent,
  TextInformation,
  ToolTipComponent
} from '@jplag/ui-components/base'
import {
  MatchList,
  FilesContainer,
  OptionsSelectorComponent,
  MetricIcon,
  FileSortingOptions,
  fileSortingTooltips,
  MetricTypes
} from '@jplag/ui-components/widget'
import { reportStore } from '@/stores/reportStore'
import { Match, MetricJsonIdentifier } from '@jplag/model'
import { uiStore } from '@/stores/uiStore'

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

const comparison = computed(() =>
  reportStore().getComparison(props.firstSubmissionId, props.secondSubmissionId)
)
const firstBaseCodeMatches = computed(() =>
  reportStore().getBaseCodeReport(props.firstSubmissionId)
)
const secondBaseCodeMatches = computed(() =>
  reportStore().getBaseCodeReport(props.secondSubmissionId)
)

const panel1: Ref<typeof FilesContainer | null> = ref(null)
const panel2: Ref<typeof FilesContainer | null> = ref(null)

/**
 * Shows a match in the first files container when clicked on a line in the second file container.
 * @param match The match to scroll to
 */
function showMatchInFirst(match: Match) {
  panel1.value?.scrollTo(match.firstFileName, match.startInFirst.line)
}

/**
 * Shows a match in the second files container, when clicked on a line in the second file container.
 * @param match The match to scroll to
 */
function showMatchInSecond(match: Match) {
  panel2.value?.scrollTo(match.secondFileName, match.startInSecond.line)
}

/**
 * Shows a match in the first and second files container.
 * @param match The match to show
 */
function showMatch(match: Match) {
  showMatchInFirst(match)
  showMatchInSecond(match)
}

const sortingOptions = [
  FileSortingOptions.ALPHABETICAL,
  FileSortingOptions.MATCH_COVERAGE,
  FileSortingOptions.MATCH_COUNT,
  FileSortingOptions.MATCH_SIZE
]
const movedAfterSorting = ref(false)
const sortingOptionSelector: Ref<typeof OptionsSelectorComponent | null> = ref(null)

function changeFileSorting(index: number) {
  movedAfterSorting.value = false
  if (index < 0) {
    return
  }
  uiStore().fileSorting = sortingOptions[index]
  panel1.value?.sortFiles(uiStore().fileSorting)
  panel2.value?.sortFiles(uiStore().fileSorting)
}

function filesMoved() {
  movedAfterSorting.value = true
  if (sortingOptionSelector.value) {
    sortingOptionSelector.value.select(-2)
  }
}

function print() {
  window.print()
}

// This code is responsible for changing the theme of the highlighted code depending on light/dark mode
// Changing the used style itself is the desired solution (https://github.com/highlightjs/highlight.js/issues/2115)
const styleholder: Ref<Node | null> = ref(null)

onMounted(() => {
  onThemeUpdate(uiStore().useDarkMode)
})

const useDarkMode = computed(() => {
  return uiStore().useDarkMode
})

watch(useDarkMode, (newValue) => {
  onThemeUpdate(newValue)
})

function onThemeUpdate(useDarkMode: boolean) {
  if (styleholder.value == null) {
    console.warn('Could not find styleholder. Syntax highlighting will not work.')
    return
  }
  const styleHolderDiv = styleholder.value as Node
  while (styleHolderDiv.hasChildNodes()) {
    styleHolderDiv.removeChild(styleHolderDiv.firstChild as Node)
  }
  const styleElement = document.createElement('style')
  styleElement.innerHTML = useDarkMode ? hljsDarkMode : hljsLightMode
  styleHolderDiv.appendChild(styleElement)
}

onErrorCaptured((error) => {
  redirectOnError(error, 'Error displaying comparison:\n', 'OverviewView', 'Back to overview')
  return false
})
</script>

<style>
@media print {
  @page {
    size: landscape;
  }
}
</style>
