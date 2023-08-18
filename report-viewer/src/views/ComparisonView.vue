<!--
  A view displaying the .json file of a comparison from a JPlag report.
-->
<template>
  <div class="absolute top-0 bottom-0 left-0 right-0 flex flex-col">
    <div class="relative top-0 left-0 right-0 p-5 pb-0 flex space-x-5">
      <Container class="flex-grow overflow-hidden">
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
    <div class="relative bottom-0 right-0 left-0 flex flex-grow space-x-5 p-5 pt-5 justify-between">
      <FilesContainer
        :container-id="1"
        :submission-id="firstId"
        :files="filesOfFirst"
        :matches="comparison.matchesInFirstSubmission"
        :files-owner="store().submissionDisplayName(firstId) || ''"
        :anonymous="isAnonymous(firstId)"
        files-owner-default="Submission 1"
        @toggle-collapse="toggleCollapseFirst"
        @line-selected="showMatchInSecond"
        class="max-h-0 min-h-full flex-1 overflow-hidden"
      />
      <FilesContainer
        :container-id="2"
        :submission-id="secondId"
        :files="filesOfSecond"
        :matches="comparison.matchesInSecondSubmissions"
        :files-owner="store().submissionDisplayName(secondId) || ''"
        :anonymous="isAnonymous(secondId)"
        files-owner-default="Submission 2"
        @toggle-collapse="toggleCollapseSecond"
        @line-selected="showMatchInFirst"
        class="max-h-0 min-h-full flex-1 overflow-hidden"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'

import { onMounted, ref, watch, type Ref, computed, onErrorCaptured } from 'vue'
import { generateLineCodeLink } from '@/utils/ComparisonUtils'
import TextInformation from '@/components/TextInformation.vue'
import MatchList from '@/components/MatchList.vue'
import { ComparisonFactory } from '@/model/factories/ComparisonFactory'
import FilesContainer from '@/components/FilesContainer.vue'
import store from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'

import hljsLightMode from 'highlight.js/styles/vs.css?raw'
import hljsDarkMode from 'highlight.js/styles/vs2015.css?raw'
import router from '@/router'
import MetricType from '@/model/MetricType'

const props = defineProps({
  firstId: {
    type: String,
    required: true
  },
  secondId: {
    type: String,
    required: true
  }
})

const comparison = ComparisonFactory.getComparison(props.firstId, props.secondId)

const filesOfFirst = ref(comparison.filesOfFirstSubmission)
const filesOfSecond = ref(comparison.filesOfSecondSubmission)

/**
 * Collapses a file in the first files container.
 * @param title title of the file
 */
function toggleCollapseFirst(title: string) {
  const file = filesOfFirst.value.get(title)
  if (file) {
    file.collapsed = !file.collapsed
  }
}

/**
 * Collapses a file in the second files container.
 * @param title title of the file
 */
function toggleCollapseSecond(title: string) {
  const file = filesOfSecond.value.get(title)
  if (file) {
    file.collapsed = !file.collapsed
  }
}

/**
 * Shows a match in the first files container when clicked on a line in the second files container.
 * @param e The click event
 * @param panel panel number (1 for left, 2 for right)
 * @param file (file name)
 * @param line (line number)
 */

function showMatchInFirst(e: unknown, panel: number, file: string, line: number) {
  if (!filesOfFirst.value.get(file)?.collapsed) {
    toggleCollapseFirst(file)
  }
  document.getElementById(generateLineCodeLink(panel, file, line))?.scrollIntoView()
}

/**
 * Shows a match in the second files container, when clicked on a line in the second files container.
 * @param e The click event
 * @param panel panel number (1 for left, 2 for right)
 * @param file (file name)
 * @param line (line number)
 */
function showMatchInSecond(e: unknown, panel: number, file: string, line: number) {
  if (!filesOfSecond.value.get(file)?.collapsed) {
    toggleCollapseSecond(file)
  }
  document.getElementById(generateLineCodeLink(panel, file, line))?.scrollIntoView()
}

/**
 * Shows a match in the first and second files container.
 * @param e The click event
 * @param match The match to show
 */
function showMatch(e: unknown, match: Match) {
  showMatchInFirst(e, 1, match.firstFile, match.startInFirst)
  showMatchInSecond(e, 2, match.secondFile, match.startInSecond)
}

function isAnonymous(id: string) {
  return store().state.anonymous.has(id)
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

onErrorCaptured((e) => {
  console.log(e)
  router.push({
    name: 'ErrorView',
    state: {
      message: 'Overview.json could not be found!',
      to: '/',
      routerInfo: 'back to FileUpload page'
    }
  })
  store().clearStore()
  return false
})
</script>
