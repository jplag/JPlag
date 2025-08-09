<!--
  Panel which displays a submission files with its line of code.
-->
<template>
  <InteractableComponent class="shadow-sm! md:mx-2 print:mx-0! print:border-0! print:p-0!">
    <div class="flex px-2 font-bold print:whitespace-pre-wrap" @click="collapsed = !collapsed">
      <ToolTipComponent v-if="getFileDisplayName(file) != file.fileName" direction="right">
        <template #default
          ><span>{{ getFileDisplayName(file) }}</span></template
        >
        <template #tooltip
          ><p class="whitespace max-w-[22rem] text-sm font-normal">
            {{ file.fileName }}
          </p></template
        >
      </ToolTipComponent>
      <span v-else>{{ file.fileName }}</span>

      <span class="flex-1"></span>

      <ToolTipComponent direction="left" class="font-normal">
        <template #default
          ><span class="text-gray-600 dark:text-gray-300"
            >{{ Math.round((file.matchedTokenCount / (file.tokenCount - 1)) * 100) }}%</span
          ></template
        >
        <template #tooltip
          ><p class="text-sm whitespace-nowrap">
            The file has {{ file.tokenCount - 1 }} tokens. {{ file.matchedTokenCount }} are part of
            a match.
          </p></template
        >
      </ToolTipComponent>
    </div>

    <div class="mx-1 overflow-x-auto print:mx-0! print:overflow-x-hidden">
      <div class="print:display-initial w-fit min-w-full text-xs!" :class="{ hidden: collapsed }">
        <div
          v-if="file.data.trim() !== ''"
          class="grid w-full grid-cols-[auto_1fr] gap-x-2 print:table-auto"
        >
          <div
            v-for="(_, index) in codeLines"
            :key="index"
            ref="lineRefs"
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
            :line="line.line"
            :line-number="index + 1"
            :matches="line.matches"
            @match-selected="(match: Match) => matchSelected(match)"
          />
        </div>

        <div v-else class="flex flex-col items-start overflow-x-auto">
          <i>Empty File</i>
        </div>
      </div>
    </div>
  </InteractableComponent>
</template>

<script setup lang="ts">
import type {
  MatchInSingleFile,
  ComparisonSubmissionFile,
  Language,
  Match,
  BaseCodeMatch
} from '@jplag/model'
import { ref, type PropType, computed, type Ref } from 'vue'
import { InteractableComponent, ToolTipComponent } from '../../base'
import { highlight } from './CodeHighlighter'
import CodeLine from './CodeLine.vue'

const props = defineProps({
  /**
   * Code lines of the file.
   */
  file: {
    type: Object as PropType<ComparisonSubmissionFile>,
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

const collapsed = ref(false)
const lineRefs = ref<HTMLElement[]>([])

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
 * Collapses the container.
 */
function collapse() {
  collapsed.value = true
}

function expand() {
  collapsed.value = false
}

function getLineRect(lineNumber: number): DOMRect {
  return lineRefs.value[lineNumber - 1].getBoundingClientRect()
}

function isCollapsed(): boolean {
  return collapsed.value
}

defineExpose({
  collapse,
  expand,
  isCollapsed,
  getLineRect
})

/**
 * converts the submissionId to the name in the path of file. If the length of path exceeds 40, then the file path displays the abbreviation.
 * @param file submission file
 * @return new path of file
 */
function getFileDisplayName(file: ComparisonSubmissionFile): string {
  const fileDisplayName = file.displayFileName ?? file.fileName
  const filePathLength = fileDisplayName.length
  return filePathLength > 40
    ? '...' + fileDisplayName.substring(filePathLength - 40, filePathLength)
    : fileDisplayName
}
</script>
