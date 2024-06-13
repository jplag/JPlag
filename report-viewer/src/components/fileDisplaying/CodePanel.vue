<!--
  Panel which displays a submission files with its line of code.
-->
<template>
  <Interactable class="mx-2 !shadow print:!mx-0 print:!border-0 print:!p-0">
    <div @click="collapsed = !collapsed" class="flex px-2 font-bold print:whitespace-pre-wrap">
      <span class="flex-1">{{ getFileDisplayName(file) }}</span>
      <ToolTipComponent direction="left" class="font-normal">
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
            ref="lineRefs"
            :line="line.line"
            :lineNumber="index + 1"
            :matches="line.matches"
            @matchSelected="(match: Match) => matchSelected(match)"
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
import { ref, nextTick, type PropType, computed, type Ref } from 'vue'
import Interactable from '../InteractableComponent.vue'
import type { SubmissionFile } from '@/model/File'
import { highlight } from '@/utils/CodeHighlighter'
import type { Language } from '@/model/Language'
import ToolTipComponent from '../ToolTipComponent.vue'
import CodeLine from './CodeLine.vue'
import type { Match } from '@/model/Match'
import type { BaseCodeMatch } from '@/model/BaseCodeReport'

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
  baseCodeMatches: {
    type: Array<BaseCodeMatch>,
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
const lineRefs = ref<(typeof CodeLine)[]>([])

const codeLines: Ref<{ line: string; matches: MatchInSingleFile[] }[]> = computed(() =>
  highlight(props.file.data, props.highlightLanguage).map((line, index) => {
    const matches = props.matches.filter((m) => m.start <= index + 1 && index + 1 <= m.end)
    const baseCodeMatches = props.baseCodeMatches.filter(
      (m) => m.start <= index + 1 && index + 1 <= m.end
    )
    matches.push(...baseCodeMatches)
    return { line, matches }
  })
)

function matchSelected(match: Match) {
  emit('matchSelected', match)
}

/**
 * Scrolls to the line number in the file.
 * @param lineNumber line number in the file
 */
function scrollTo(lineNumber: number) {
  collapsed.value = false
  nextTick(function () {
    lineRefs.value[lineNumber - 1].scrollTo()
  })
}

/**
 * Collapses the container.
 */
function collapse() {
  collapsed.value = true
}

defineExpose({
  scrollTo,
  collapse
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
