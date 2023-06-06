<!--
  A view displaying the .json file of a comparison from a JPlag report.
-->
<template>
  <div class="absolute top-0 bottom-0 left-0 right-0 flex flex-col">
    <div class="relative top-0 left-0 right-0 p-5 pb-0 flex space-x-5">
      <Container class="flex-grow">
        <h2>
          Comparison: {{ comparison.firstSubmissionId }} - {{ comparison.secondSubmissionId }}
        </h2>
      </Container>
    </div>

    <div class="relative bottom-0 right-0 left-0 flex flex-grow space-x-5 p-5 pt-5 justify-between">
      <FilesContainer
        :container-id="1"
        :submission-id="firstId"
        :files="filesOfFirst"
        :matches="comparison.matchesInFirstSubmission"
        :files-owner="store().submissionDisplayName(firstId) || ''"
        :anonymous="store().anonymous.has(firstId)"
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
        :anonymous="store().anonymous.has(secondId) || false"
        files-owner-default="Submission 2"
        @toggle-collapse="toggleCollapseSecond"
        @line-selected="showMatchInFirst"
        class="max-h-0 min-h-full flex-1 overflow-hidden"
      />
    </div>
  </div>
  <!--div class="container">
    <button
      id="show-button"
      :class="{ hidden: !hideLeftPanel }"
      title="Show sidebar"
      @click="togglePanel"
    >
      <img alt="show" src="@/assets/double_arrow_black_24dp.svg" />
    </button>

    <div id="sidebar" :class="{ hidden: hideLeftPanel }">
      <div class="title-section">
        <h1>JPlag Comparison</h1>
        <button id="hide-button" title="Hide sidebar" @click="togglePanel">
          <img alt="hide" src="@/assets/keyboard_double_arrow_left_black_24dp.svg" />
        </button>
      </div>
      <div>
        <button class="animated-back-button" title="Back button" @click="back">back</button>
      </div>
      <TextInformation
        :anonymous="isAnonymous(firstId)"
        :value="store().submissionDisplayName(firstId) || ''"
        label="Submission 1"
      />
      <TextInformation
        :anonymous="store().anonymous.has(secondId)"
        :value="store().submissionDisplayName(secondId) || ''"
        label="Submission 2"
      />
      <TextInformation :value="(comparison.similarity * 100).toFixed(2)" label="Match %" />
      <MatchTable
        :id1="firstId"
        :id2="secondId"
        :matches="comparison.allMatches"
        @match-selected="showMatch"
      />
    </div>

  </!--div-->
</template>

<script setup lang="ts">
import type { Match } from '@/model/Match'

import { ref } from 'vue'
import { generateLineCodeLink } from '@/utils/Utils'
import TextInformation from '@/components/TextInformation.vue'
import MatchTable from '@/components/MatchTable.vue'
import { ComparisonFactory } from '@/model/factories/ComparisonFactory'
import FilesContainer from '@/components/FilesContainer.vue'
import { useRouter } from 'vue-router'
import { Comparison } from '@/model/Comparison'
import store from '@/stores/store'
import Container from '@/components/ContainerComponent.vue'

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

const router = useRouter()
console.log('Generating comparison {%s} - {%s}...', props.firstId, props.secondId)
let comparison = new Comparison('', '', 0)

//getting the comparison file based on the used mode (zip, local, single)
if (store().local) {
  const request = new XMLHttpRequest()
  request.open(
    'GET',
    `/files/${store().getComparisonFileName(props.firstId, props.secondId)}`,
    false
  )
  request.send()

  if (request.status == 200) {
    loadSubmissionFilesFromLocal(props.firstId)
    loadSubmissionFilesFromLocal(props.secondId)
    try {
      comparison = ComparisonFactory.getComparison(JSON.parse(request.response))
    } catch (e) {
      router.back()
    }
  } else {
    router.back()
  }
} else if (store().zip) {
  let comparisonFile = store().getComparisonFileForSubmissions(props.firstId, props.secondId)
  if (comparisonFile) {
    comparison = ComparisonFactory.getComparison(JSON.parse(comparisonFile))
  } else {
    console.log('Comparison file not found!')
    router.push({
      name: 'ErrorView',
      state: {
        message: 'Comparison file not found!',
        to: '/overview',
        routerInfo: 'back to overview page'
      }
    })
  }
} else if (store().single) {
  try {
    comparison = ComparisonFactory.getComparison(JSON.parse(store().fileString))
  } catch (e) {
    router.push({
      name: 'ErrorView',
      state: {
        message:
          'Source code of matches not found. To only see the overview, please drop the overview.json directly.',
        to: '/',
        routerInfo: 'back to FileUpload page'
      }
    })
    store().clearStore()
  }
}

function getSubmissionFileListFromLocal(submissionId: string): string[] {
  const request = new XMLHttpRequest()
  request.open('GET', `/files/submissionFileIndex.json`, false)
  request.send()
  if (request.status == 200) {
    return JSON.parse(request.response).submission_file_indexes[submissionId]
  } else {
    return []
  }
}

function loadSubmissionFilesFromLocal(submissionId: string) {
  const request = new XMLHttpRequest()
  const fileList = getSubmissionFileListFromLocal(submissionId)
  for (const file of fileList) {
    request.open('GET', `/files/files/${file.replace(/\\/, '/')}`, false)
    request.send()
    if (request.status == 200) {
      store().saveSubmissionFile({
        name: submissionId,
        file: {
          fileName: file,
          data: request.response
        }
      })
    }
  }
}

const filesOfFirst = ref(comparison.filesOfFirstSubmission)
const filesOfSecond = ref(comparison.filesOfSecondSubmission)

/**
 * Collapses a file in the first files container.
 * @param title
 */

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
  return store().anonymous.has(id)
}

//Left panel
const hideLeftPanel = ref(false)

/**
 * Toggles the left sidebar panel
 */
function togglePanel() {
  hideLeftPanel.value = !hideLeftPanel.value
}

function back() {
  router.back()
}
</script>
