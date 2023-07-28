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
            >{{ (comparison.similarity * 100).toFixed(2) }}%</TextInformation
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
        ref="panel1"
        :submission-id="firstId"
        :files="filesOfFirst"
        :matches="comparison.matchesInFirstSubmission"
        :anonymous="isAnonymous(firstId)"
        anonymous-files-owner-default="Submission 1"
        @line-selected="showMatchInSecond"
        class="max-h-0 min-h-full flex-1 overflow-hidden"
      />
      <FilesContainer
        ref="panel2"
        :submission-id="secondId"
        :files="filesOfSecond"
        :matches="comparison.matchesInSecondSubmissions"
        :anonymous="isAnonymous(secondId)"
        anonymous-files-owner-default="Submission 2"
        @line-selected="showMatchInFirst"
        class="max-h-0 min-h-full flex-1 overflow-hidden"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'

import { onMounted, ref, watch, type Ref, computed, onErrorCaptured } from 'vue'
import TextInformation from '@/components/TextInformation.vue'
import MatchList from '@/components/MatchList.vue'
import { ComparisonFactory } from '@/model/factories/ComparisonFactory'
import FilesContainer from '@/components/FilesContainer.vue'
import store from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'

import hljsLightMode from 'highlight.js/styles/vs.css?raw'
import hljsDarkMode from 'highlight.js/styles/vs2015.css?raw'
import router from '@/router'

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
