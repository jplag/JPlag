<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <ContainerComponent class="flex flex-col print:px-1!">
    <div class="mb-2 flex flex-col gap-x-5 gap-y-2 md:mr-2 md:flex-row md:items-center">
      <h3 class="grow text-left text-lg font-bold">
        Files of
        {{ fileOwnerDisplayName }}:
      </h3>
      <div class="flex items-center gap-x-1 text-gray-600 dark:text-gray-300">
        <MetricIcon class="h-4" :metric="MetricJsonIdentifier.MAXIMUM_LENGTH" /><span
          >{{ tokenCount }} total tokens</span
        >
      </div>
      <ButtonComponent v-if="allCollapsed" class="space-x-2 print:hidden" @click="expandAll()"
        ><FontAwesomeIcon :icon="faExpandAlt" />
        <p>Expand All</p></ButtonComponent
      >
      <ButtonComponent
        v-else
        class="w-full space-x-2 md:max-w-fit print:hidden"
        @click="collapseAll()"
        ><FontAwesomeIcon :icon="faCompressAlt" />
        <p>Collapse All</p></ButtonComponent
      >
    </div>

    <ScrollableComponent ref="scrollContainer" class="grow">
      <VueDraggableNext @update="emitFileMoving()">
        <CodePanel
          v-for="file in sortedFiles"
          :key="file.fileName"
          ref="codePanels"
          :file="file"
          :matches="matchesPerFile[file.fileName]"
          :highlight-language="highlightLanguage"
          class="mt-1 first:mt-0"
          :base-code-matches="baseCodeMatches.filter((match) => match.fileName === file.fileName)"
          @match-selected="(match: Match) => $emit('matchSelected', match)"
        />
      </VueDraggableNext>
    </ScrollableComponent>
  </ContainerComponent>
</template>

<script setup lang="ts">
import {
  type ComparisonSubmissionFile,
  type MatchInSingleFile,
  type Language,
  type Match,
  type BaseCodeMatch,
  MetricJsonIdentifier
} from '@jplag/model'
import CodePanel from './CodePanel.vue'
import { ContainerComponent, ScrollableComponent, ButtonComponent } from '../../base'
import { VueDraggableNext } from 'vue-draggable-next'
import { computed, nextTick, ref, type PropType, type Ref } from 'vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCompressAlt, faExpandAlt } from '@fortawesome/free-solid-svg-icons'
import { FileSortingOptions } from './FileSortingOptions'
import MetricIcon from '../MetricIcon.vue'

const props = defineProps({
  /**
   * Files of the submission.
   */
  files: {
    type: Array<ComparisonSubmissionFile>,
    required: true
  },
  /**
   * Matches of submission.
   */
  matches: {
    type: Map<string, MatchInSingleFile[]>,
    required: true
  },
  /**
   * Name of the owner of the files.
   */
  fileOwnerDisplayName: {
    type: String,
    required: true
  },
  /**
   * Language of the files.
   */
  highlightLanguage: {
    type: String as PropType<Language>,
    required: true
  },
  /**
   * Base code matches of the submission.
   */
  baseCodeMatches: {
    type: Array as PropType<BaseCodeMatch[]>,
    required: true
  },
  fileSorting: {
    type: Number as PropType<FileSortingOptions>,
    default: FileSortingOptions.ALPHABETICAL
  }
})

const emit = defineEmits(['matchSelected', 'filesMoved'])

const matchesPerFile = computed(() => {
  const matches: Record<string, MatchInSingleFile[]> = {}
  for (const file of props.files) {
    matches[file.fileName] = !props.matches.get(file.fileName)
      ? []
      : (props.matches.get(file.fileName) as MatchInSingleFile[])
  }
  return matches
})

const sortedFiles: Ref<ComparisonSubmissionFile[]> = ref([])
// eslint-disable-next-line vue/no-setup-props-reactivity-loss
sortFiles(props.fileSorting)

function sortFiles(fileSorting: FileSortingOptions) {
  switch (fileSorting) {
    case FileSortingOptions.ALPHABETICAL: {
      sortedFiles.value = Array.from(props.files).sort((a, b) =>
        a.fileName.localeCompare(b.fileName)
      )
      break
    }

    case FileSortingOptions.MATCH_SIZE: {
      const largestMatch: Record<string, number> = {}
      for (const file of props.files) {
        largestMatch[file.fileName] = Math.max(
          ...matchesPerFile.value[file.fileName].map((match) => match.length)
        )
      }
      sortedFiles.value = Array.from(props.files).sort(
        (a, b) => largestMatch[b.fileName] - largestMatch[a.fileName]
      )
      break
    }

    case FileSortingOptions.MATCH_COUNT: {
      const matchCount: Record<string, number> = {}
      for (const file of props.files) {
        matchCount[file.fileName] = matchesPerFile.value[file.fileName].length
      }
      sortedFiles.value = Array.from(props.files).sort(
        (a, b) => matchCount[b.fileName] - matchCount[a.fileName]
      )
      break
    }

    case FileSortingOptions.MATCH_COVERAGE: {
      const matchCoverage: Record<string, number> = {}
      for (const file of props.files) {
        const matches = matchesPerFile.value[file.fileName]
        const totalTokens = matches.reduce((acc, match) => acc + match.length, 0)
        matchCoverage[file.fileName] =
          totalTokens / (file.tokenCount > 0 ? file.tokenCount : Infinity)
      }
      sortedFiles.value = Array.from(props.files).sort(
        (a, b) => matchCoverage[b.fileName] - matchCoverage[a.fileName]
      )
      break
    }
  }
}

const shouldEmitFileMoving = ref(true)

function emitFileMoving() {
  if (!shouldEmitFileMoving.value) {
    return
  }
  emit('filesMoved')
}

const codePanels: Ref<(typeof CodePanel)[]> = ref([])
const scrollContainer: Ref<typeof ScrollableComponent | null> = ref(null)

const tokenCount = computed(() => {
  return props.files.reduce((acc, file) => (file.tokenCount ?? 0) + acc - 1, 0)
})

/**
 * Scrolls to the given file and line in the container.
 * @param file Name of the file to scroll to.
 * @param line Line to scroll to.
 */
function scrollTo(file: string, line: number) {
  const fileIndex = sortedFiles.value.findIndex((f) => f.fileName === file)
  if (fileIndex !== -1) {
    codePanels.value[fileIndex].expand()
    nextTick(() => {
      if (!scrollContainer.value) {
        return
      }
      const childToScrollTo = codePanels.value[fileIndex].getLineRect(line) as DOMRect
      const scrollBox = scrollContainer.value.getRoot() as HTMLElement
      scrollBox.scrollTo({
        top: childToScrollTo.top + scrollBox.scrollTop - (scrollBox.clientHeight * 2) / 3
      })
    })
  }
}

/**
 * Collapses all the code panels.
 */
function collapseAll() {
  codePanels.value.forEach((panel) => panel.collapse())
}

/**
 * Expands all the code panels.
 */
function expandAll() {
  codePanels.value.forEach((panel) => panel.expand())
}

const allCollapsed = computed(() => {
  return codePanels.value.every((panel) => panel.isCollapsed())
})

defineExpose({
  scrollTo,
  sortFiles
})
</script>
