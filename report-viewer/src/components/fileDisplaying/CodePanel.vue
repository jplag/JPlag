<!--
  Panel which displays a submission files with its line of code.
-->
<template>
  <Interactable class="mx-2 !shadow print:!mx-0 print:!border-0 print:!p-0">
    <div @click="collapsed = !collapsed" class="flex px-2 font-bold print:whitespace-pre-wrap">
      <span class="flex-1">{{ getFileDisplayName(file) }}</span>
      <ToolTipComponent v-if="file.tokenCount != undefined" direction="left" class="font-normal">
        <template #default
          ><span class="text-gray-600 dark:text-gray-300"
            >{{ Math.round((file.matchedTokenCount / (file.tokenCount - 1)) * 100) }}%</span
          ></template
        >
        <template #tooltip
          ><p class="whitespace-nowrap text-sm">
            The file has {{ file.tokenCount - 1 }} tokens. {{ file.matchedTokenCount }} are part of
            a match.
          </p></template
        >
      </ToolTipComponent>
    </div>

    <div class="mx-1 overflow-x-auto print:!mx-0 print:overflow-x-hidden">
      <div class="print:display-initial w-fit min-w-full !text-xs" :class="{ hidden: collapsed }">
        <div
          v-if="file.data.trim() !== ''"
          class="grid w-full grid-cols-[auto_1fr] gap-x-2 print:table-auto"
        >
          <div
            v-for="(_, index) in codeLines"
            :key="index"
            class="col-span-1 col-start-1 row-span-1 text-right"
            ref="lineRefs"
            :style="{
              gridRowStart: index + 1
            }"
          >
            {{ index + 1 }}
          </div>
          <!-- One row in table per code line -->
          <CodeLine
            v-for="(line, index) in codeLines"
            :key="index"
            :line="line.line"
            :lineNumber="index + 1"
            :matches="line.matches"
            @matchSelected="(match) => matchSelected(match)"
          />
        </div>

        <div v-else class="flex flex-col items-start overflow-x-auto">
          <i>Empty File</i>
        </div>
      </div>
    </div>
  </Interactable>
</template>

<script setup lang="ts">
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import { ref, type PropType, computed, type Ref } from 'vue'
import Interactable from '../InteractableComponent.vue'
import type { SubmissionFile } from '@/model/File'
import { highlight } from '@/utils/CodeHighlighter'
import type { Language } from '@/model/Language'
import ToolTipComponent from '../ToolTipComponent.vue'
import CodeLine from './CodeLine.vue'
import type { Match } from '@/model/Match'

const props = defineProps({
  /**
   * Code lines of the file.
   */
  file: {
    type: Object as PropType<SubmissionFile>,
    required: true
  },
  /**
   * Matches in the file
   */
  matches: {
    type: Array<MatchInSingleFile>,
    required: true
  },
  /**
   * Language of the file.
   */
  highlightLanguage: {
    type: String as PropType<Language>,
    required: true
  }
})

const emit = defineEmits(['matchSelected'])

const collapsed = ref(true)
const lineRefs = ref<HTMLElement[]>([])

const codeLines: Ref<{ line: string; matches: MatchInSingleFile[] }[]> = computed(() =>
  highlight(props.file.data, props.highlightLanguage).map((line, index) => {
    return {
      line,
      matches: props.matches?.filter((m) => m.start <= index + 1 && index + 1 <= m.end) ?? []
    }
  })
)

function matchSelected(match: Match) {
  emit('matchSelected', match)
}

/**
 * Collapses the container.
 */
function collapse() {
  collapsed.value = true
}

function expand() {
  console.log('expand')
  collapsed.value = false
}

function getLineRect(lineNumber: number): DOMRect {
  return lineRefs.value[lineNumber - 1].getBoundingClientRect()
}

defineExpose({
  collapse,
  expand,
  getLineRect
})

/**
 * converts the submissionId to the name in the path of file. If the length of path exceeds 40, then the file path displays the abbreviation.
 * @param file submission file
 * @return new path of file
 */
function getFileDisplayName(file: SubmissionFile): string {
  const filePathLength = file.fileName.length
  return filePathLength > 40
    ? '...' + file.fileName.substring(filePathLength - 40, filePathLength)
    : file.fileName
}
</script>
