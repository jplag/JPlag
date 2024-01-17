<!--
  A view displaying the .json file of a comparison from a JPlag report.
-->
<template>
  <div class="absolute bottom-0 left-0 right-0 top-0 flex flex-col">
    <div class="relative left-0 right-0 top-0 flex space-x-5 p-5 pb-0 print:p-0">
      <Container class="flex-grow overflow-hidden print:min-h-fit">
        <h2>
          Comparison:
          {{
            isAnonymous(comparison.firstSubmissionId)
              ? 'Submission 1'
              : store().submissionDisplayName(comparison.firstSubmissionId)
          }}
          -
          {{
            isAnonymous(comparison.secondSubmissionId)
              ? 'Submission 2'
              : store().submissionDisplayName(comparison.secondSubmissionId)
          }}
          <ToolTipComponent direction="left" class="float-right print:hidden">
            <template #tooltip>
              <p class="whitespace-pre text-sm">
                Printing works best in landscape mode on Chromium based browsers
              </p>
            </template>
            <template #default>
              <Button class="h-10 w-10" @click="print()">
                <FontAwesomeIcon class="text-2xl" :icon="['fas', 'print']" />
              </Button>
            </template>
          </ToolTipComponent>
        </h2>
        <div class="flex flex-row">
          <TextInformation label="Average Similarity"
            >{{ (comparison.similarities[MetricType.AVERAGE] * 100).toFixed(2) }}%</TextInformation
          >
        </div>
        <MatchList
          :id1="firstId"
          :id2="secondId"
          :matches="comparison.allMatches"
          @match-selected="showMatch"
        />
      </Container>
    </div>
    <div ref="styleholder"></div>
    <div
      class="relative bottom-0 left-0 right-0 flex flex-grow justify-between space-x-5 p-5 pt-5 print:space-x-1 print:p-0"
    >
      <FilesContainer
        ref="panel1"
        :files="filesOfFirst"
        :matches="comparison.matchesInFirstSubmission"
        :file-owner-display-name="
          isAnonymous(comparison.firstSubmissionId)
            ? 'Submission 1'
            : (store().submissionDisplayName(comparison.firstSubmissionId) as string)
        "
        :highlight-language="language"
        @line-selected="showMatchInSecond"
        class="max-h-0 min-h-full flex-1 overflow-hidden print:max-h-none print:overflow-y-visible"
      />
      <FilesContainer
        ref="panel2"
        :files="filesOfSecond"
        :matches="comparison.matchesInSecondSubmissions"
        :file-owner-display-name="
          isAnonymous(comparison.secondSubmissionId)
            ? 'Submission 2'
            : (store().submissionDisplayName(comparison.secondSubmissionId) as string)
        "
        :highlight-language="language"
        @line-selected="showMatchInFirst"
        class="max-h-0 min-h-full flex-1 overflow-hidden print:max-h-none print:overflow-y-visible"
      />
    </div>
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
import { HighlightLanguage } from '@/model/Language'
import hljsLightMode from 'highlight.js/styles/vs.css?raw'
import hljsDarkMode from 'highlight.js/styles/vs2015.css?raw'
import { MetricType } from '@/model/MetricType'
import { Comparison } from '@/model/Comparison'
import { redirectOnError } from '@/router'
import ToolTipComponent from '@/components/ToolTipComponent.vue'

library.add(faPrint)

const props = defineProps({
  firstId: {
    type: String,
    required: true
  },
  secondId: {
    type: String,
    required: true
  },
  comparison: {
    type: Object as PropType<Comparison>,
    required: true
  },
  language: {
    type: Object as PropType<HighlightLanguage>,
    required: true
  }
})

const filesOfFirst = computed(() => props.comparison.filesOfFirstSubmission)
const filesOfSecond = computed(() => props.comparison.filesOfSecondSubmission)

const panel1: Ref<typeof FilesContainer | null> = ref(null)
const panel2: Ref<typeof FilesContainer | null> = ref(null)

/**
 * Shows a match in the first files container when clicked on a line in the second files container.
 * @param file (file name)
 * @param line (line number)
 */
function showMatchInFirst(match: Match) {
  panel1.value?.scrollTo(match.firstFile, match.startInFirst)
}

/**
 * Shows a match in the second files container, when clicked on a line in the second files container.
 * @param file (file name)
 * @param line (line number)
 */
function showMatchInSecond(match: Match) {
  panel2.value?.scrollTo(match.secondFile, match.startInSecond)
}

/**
 * Shows a match in the first and second files container.
 * @param e The click event
 * @param match The match to show
 */
function showMatch(match: Match) {
  showMatchInFirst(match)
  showMatchInSecond(match)
}

function isAnonymous(id: string) {
  return store().state.anonymous.has(id)
}

function print() {
  window.print()
}

// This code is responsible for changing the theme of the highlighted code depending on light/dark mode
// Changing the used style itsself is the desired solution (https://github.com/highlightjs/highlight.js/issues/2115)
const styleholder: Ref<Node | null> = ref(null)

onMounted(() => {
  if (styleholder.value == null) {
    return
  }
  const styleHolderDiv = styleholder.value as Node
  const styleElement = document.createElement('style')
  styleElement.innerHTML = store().uiState.useDarkMode ? hljsDarkMode : hljsLightMode
  styleHolderDiv.appendChild(styleElement)
})

const useDarkMode = computed(() => {
  return store().uiState.useDarkMode
})

watch(useDarkMode, (newValue) => {
  if (styleholder.value == null) {
    return
  }
  const styleHolderDiv = styleholder.value as Node
  styleHolderDiv.removeChild(styleHolderDiv.firstChild as Node)
  const styleElement = document.createElement('style')
  styleElement.innerHTML = newValue ? hljsDarkMode : hljsLightMode
  styleHolderDiv.appendChild(styleElement)
})

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
