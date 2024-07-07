<!--
  Container containing CodePanels for all of the files in a submission.
-->
<template>
  <Container class="flex flex-col print:!px-1">
    <div class="mb-2 mr-2 flex items-center space-x-5">
      <h3 class="flex-grow text-left text-lg font-bold">
        Files of
        {{ fileOwnerDisplayName }}:
      </h3>
      <div class="text-gray-600 dark:text-gray-300">{{ tokenCount }} total tokens</div>
      <Button @click="collapseAll()" class="space-x-2 print:hidden"
        ><FontAwesomeIcon :icon="['fas', 'compress-alt']" />
        <p>Collapse All</p></Button
      >
    </div>

    <ScrollableComponent class="flex-grow">
      <VueDraggableNext @update="emitFileMoving()">
        <CodePanel
          v-for="file in sortedFiles"
          :key="file.fileName"
          ref="codePanels"
          :file="file"
          :matches="matchesPerFile[file.fileName]"
          :highlight-language="highlightLanguage"
          @match-selected="(match) => $emit('matchSelected', match)"
          class="mt-1 first:mt-0"
        />
      </VueDraggableNext>
    </ScrollableComponent>
  </Container>
</template>

<script setup lang="ts">
import type { SubmissionFile } from '@/model/File'
import CodePanel from './CodePanel.vue'
import Container from '../ContainerComponent.vue'
import Button from '../ButtonComponent.vue'
import ScrollableComponent from '../ScrollableComponent.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { computed, ref, type ComputedRef, type PropType, type Ref } from 'vue'
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCompressAlt } from '@fortawesome/free-solid-svg-icons'
import { library } from '@fortawesome/fontawesome-svg-core'
import type { Language } from '@/model/Language'
import { FileSortingOptions } from '@/model/ui/FileSortingOptions'

library.add(faCompressAlt)

const props = defineProps({
  /**
   * Files of the submission.
   */
  files: {
    type: Array<SubmissionFile>,
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
   * Sorting to use
   */
  sorting: {
    type: Number as PropType<FileSortingOptions>,
    required: false,
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

const sortedFiles: ComputedRef<SubmissionFile[]> = computed(() => {
  switch (props.sorting) {
    case FileSortingOptions.ALPHABETICAL:
      return Array.from(props.files).sort((a, b) => a.fileName.localeCompare(b.fileName))

    case FileSortingOptions.MATCH_SIZE: {
      const largestMatch: Record<string, number> = {}
      for (const file of props.files) {
        largestMatch[file.fileName] = Math.max(
          ...matchesPerFile.value[file.fileName].map((match) => match.match.tokens)
        )
      }
      return Array.from(props.files).sort(
        (a, b) => largestMatch[b.fileName] - largestMatch[a.fileName]
      )
    }

    case FileSortingOptions.MATCH_COUNT: {
      const matchCount: Record<string, number> = {}
      for (const file of props.files) {
        matchCount[file.fileName] = matchesPerFile.value[file.fileName].length
      }
      return Array.from(props.files).sort((a, b) => matchCount[b.fileName] - matchCount[a.fileName])
    }

    case FileSortingOptions.MATCH_COVERAGE: {
      const matchCoverage: Record<string, number> = {}
      for (const file of props.files) {
        const matches = matchesPerFile.value[file.fileName]
        const totalTokens = matches.reduce((acc, match) => acc + match.match.tokens, 0)
        matchCoverage[file.fileName] =
          totalTokens / (file.tokenCount > 0 ? file.tokenCount : Infinity)
      }
      return Array.from(props.files).sort(
        (a, b) => matchCoverage[b.fileName] - matchCoverage[a.fileName]
      )
    }

    default: {
      if (sortedFiles.value && sortedFiles.value.length > 0) {
        return sortedFiles.value
      }
      return props.files
    }
  }
})

const shouldEmitFileMoving = ref(true)

function emitFileMoving() {
  if (!shouldEmitFileMoving.value) {
    return
  }
  emit('filesMoved')
}

const codePanels: Ref<(typeof CodePanel)[]> = ref([])

const tokenCount = computed(() => {
  return props.files.reduce((acc, file) => (file.tokenCount ?? 0) + acc - 1, 0)
})

/**
 * Scrolls to the given file and line in the container.
 * @param file Name of the file to scroll to.
 * @param line Line to scroll to.
 */
function scrollTo(file: string, line: number) {
  const fileIndex = Array.from(props.files).findIndex((f) => f.fileName === file)
  if (fileIndex !== -1) {
    codePanels.value[fileIndex].scrollTo(line)
  }
}

/**
 * Collapses all of the code panels.
 */
function collapseAll() {
  codePanels.value.forEach((panel) => panel.collapse())
}

defineExpose({
  scrollTo
})
</script>
