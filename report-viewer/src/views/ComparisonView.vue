<!--
  A view displaying the .json file of a comparison from a JPlag report.
-->
<template>
  <div
    class="grid grid-cols-1 grid-rows-[min-content_auto_auto] gap-5 md:grid-cols-2 md:grid-rows-[auto_1fr] md:overflow-hidden"
  >
    <Container class="col-start-1 row-start-1 md:col-end-3 md:row-end-2">
      <h2>
        Comparison:
        {{ store().getDisplayName(comparison.firstSubmissionId) }}
        -
        {{ store().getDisplayName(comparison.secondSubmissionId) }}
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
            <Button class="hidden h-10 w-10 md:block" @click="print()">
              <FontAwesomeIcon class="text-2xl" :icon="['fas', 'print']" />
            </Button>
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
          :label="`Similarity ${store().getDisplayName(comparison.firstSubmissionId)}`"
          tooltip-side="right"
        >
          <template #default>{{ (comparison.firstSimilarity * 100).toFixed(2) }}%</template>
          <template #tooltip
            ><div class="text-sm whitespace-pre">
              <p>
                Percentage of code from
                {{ store().getDisplayName(comparison.firstSubmissionId) }} that was found in the
                code of {{ store().getDisplayName(comparison.secondSubmissionId) }}.
              </p>
              <p>
                The numbers might not be symmetric, due to the submissions having different lengths.
              </p>
            </div></template
          >
        </TextInformation>
        <TextInformation
          :label="`Similarity ${store().getDisplayName(comparison.secondSubmissionId)}`"
          tooltip-side="right"
          ><template #default>{{ (comparison.secondSimilarity * 100).toFixed(2) }}%</template>
          <template #tooltip
            ><div class="text-sm whitespace-pre">
              <p>
                Percentage of code from
                {{ store().getDisplayName(comparison.secondSubmissionId) }} that was found in the
                code of {{ store().getDisplayName(comparison.firstSubmissionId) }}.
              </p>
              <p>
                The numbers might not be symmetric, due to the submissions having different lengths.
              </p>
            </div></template
          ></TextInformation
        >
      </div>
      <MatchList
        :id1="firstId"
        :id2="secondId"
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
        :default-selected="sortingOptions.indexOf(store().uiState.fileSorting)"
        @selection-changed="(index: number) => changeFileSorting(index)"
      />
    </Container>
    <div ref="styleholder" class="col-span-0 row-span-0"></div>
    <FilesContainer
      ref="panel1"
      :files="filesOfFirst"
      :matches="comparison.matchesInFirstSubmission"
      :file-owner-display-name="store().getDisplayName(comparison.firstSubmissionId)"
      :highlight-language="language"
      :base-code-matches="firstBaseCodeMatches"
      class="col-start-1 row-start-2 flex flex-col overflow-hidden"
      @match-selected="showMatchInSecond"
      @files-moved="filesMoved()"
    />
    <FilesContainer
      ref="panel2"
      :files="filesOfSecond"
      :matches="comparison.matchesInSecondSubmissions"
      :file-owner-display-name="store().getDisplayName(comparison.secondSubmissionId)"
      :highlight-language="language"
      :base-code-matches="secondBaseCodeMatches"
      class="col-start-1 row-start-3 flex overflow-hidden md:col-start-2 md:row-start-2"
      @match-selected="showMatchInFirst"
      @files-moved="filesMoved()"
    />
  </div>
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import Button from '@/components/ButtonComponent.vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faPrint } from '@fortawesome/free-solid-svg-icons'
import { onMounted, ref, watch, type Ref, computed, type PropType, onErrorCaptured } from 'vue'
import TextInformation from '@/components/TextInformation.vue'
import MatchList from '@/components/fileDisplaying/MatchList.vue'
import FilesContainer from '@/components/fileDisplaying/FilesContainer.vue'
import { store } from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'
import type { Language } from '@/model/Language'
import hljsLightMode from 'highlight.js/styles/vs.css?raw'
import hljsDarkMode from 'highlight.js/styles/vs2015.css?raw'
import { MetricTypes } from '@/model/MetricType'
import { Comparison } from '@/model/Comparison'
import { redirectOnError } from '@/router'
import ToolTipComponent from '@/components/ToolTipComponent.vue'
import { FileSortingOptions, fileSortingTooltips } from '@/model/ui/FileSortingOptions'
import OptionsSelectorComponent from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import type { BaseCodeMatch } from '@/model/BaseCodeReport'
import { MetricJsonIdentifier } from '@/model/MetricJsonIdentifier'
import MetricIcon from '@/components/MetricIcon.vue'

library.add(faPrint)

const props = defineProps({
  comparison: {
    type: Object as PropType<Comparison>,
    required: true
  },
  language: {
    type: String as PropType<Language>,
    required: true
  },
  firstBaseCodeMatches: {
    type: Array as PropType<BaseCodeMatch[]>,
    required: true
  },
  secondBaseCodeMatches: {
    type: Array as PropType<BaseCodeMatch[]>,
    required: true
  }
})

const firstId = computed(() => props.comparison.firstSubmissionId)
const secondId = computed(() => props.comparison.secondSubmissionId)
const filesOfFirst = computed(() => props.comparison.filesOfFirstSubmission)
const filesOfSecond = computed(() => props.comparison.filesOfSecondSubmission)

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
  store().uiState.fileSorting = sortingOptions[index]
  panel1.value?.sortFiles(store().uiState.fileSorting)
  panel2.value?.sortFiles(store().uiState.fileSorting)
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
  onThemeUpdate(store().uiState.useDarkMode)
})

const useDarkMode = computed(() => {
  return store().uiState.useDarkMode
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
