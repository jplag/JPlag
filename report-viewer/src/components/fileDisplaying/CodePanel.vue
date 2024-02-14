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
        <table
          v-if="file.data.trim() !== ''"
          class="w-full print:table-auto"
          :aria-describedby="`Content of file ${file.fileName}`"
        >
          <!-- One row in table per code line -->
          <tr
            v-for="(line, index) in codeLines"
            :key="index"
            class="w-full cursor-default"
            :class="{ 'cursor-pointer': line.match !== null }"
            @click="lineSelected(index)"
          >
            <!-- Line number -->
            <td class="float-right pr-3">{{ index + 1 }}</td>
            <!-- Code line -->
            <td
              class="print-excact w-full"
              :style="{
                background:
                  line.match !== null
                    ? getMatchColor(0.3, line.match.colorIndex)
                    : 'hsla(0, 0%, 0%, 0)'
              }"
            >
              <pre
                v-html="line.line"
                class="code-font print-excact break-child !bg-transparent print:whitespace-pre-wrap"
                ref="lineRefs"
              ></pre>
            </td>
          </tr>
        </table>

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
import type { Match } from '@/model/Match'
import type { SubmissionFile } from '@/stores/state'
import { highlight } from '@/utils/CodeHighlighter'
import type { ParserLanguage } from '@/model/Language'
import { getMatchColor } from '@/utils/ColorUtils'
import ToolTipComponent from '../ToolTipComponent.vue'

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
    type: String as PropType<ParserLanguage>,
    required: true
  }
})

const emit = defineEmits(['lineSelected'])

const collapsed = ref(true)
const lineRefs = ref<HTMLElement[]>([])

const codeLines: Ref<{ line: string; match: null | Match }[]> = computed(() =>
  highlight(props.file.data, props.highlightLanguage).map((line, index) => {
    return {
      line,
      match: props.matches?.find((m) => m.start <= index + 1 && index + 1 <= m.end)?.match ?? null
    }
  })
)

function lineSelected(lineIndex: number) {
  if (codeLines.value[lineIndex].match !== null) {
    emit('lineSelected', codeLines.value[lineIndex].match)
  }
}

/**
 * Scrolls to the line number in the file.
 * @param lineNumber line number in the file
 */
function scrollTo(lineNumber: number) {
  collapsed.value = false
  nextTick(function () {
    lineRefs.value[lineNumber - 1].scrollIntoView({ block: 'center' })
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

<style scoped>
.code-font {
  font-family: 'JetBrains Mono NL', monospace !important;
}

@media print {
  .break-child *,
  .break-child {
    word-break: break-word;
  }
}
</style>
