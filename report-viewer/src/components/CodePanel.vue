<!--
  Panel which displays a submission files with its line of code.
-->
<template>
  <Interactable class="!shadow mx-2">
    <div @click="collapsed = !collapsed" class="text-center font-bold">
      {{ getFileDisplayName(file) }}
    </div>
    <div class="mx-1 overflow-x-auto">
      <div v-if="!collapsed" class="w-fit min-w-full !text-xs">
        <table v-if="file.data.trim() !== ''" class="w-full">
          <tr
            v-for="(line, index) in codeLines"
            :key="index"
            class="w-full cursor-default"
            :class="{ 'cursor-pointer': line.match !== null }"
            @click="lineSelected(index)"
          >
            <td class="float-right pr-3">{{ index + 1 }}</td>
            <td
              class="w-full"
              :style="{
                background: line.match !== null ? line.match.color : 'hsla(0, 0%, 0%, 0)'
              }"
            >
              <pre v-html="line.line" class="code-font !bg-transparent" ref="lineRefs"></pre>
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
import { ref, nextTick, type PropType } from 'vue'
import Interactable from './InteractableComponent.vue'
import type { Match } from '@/model/Match'
import type { SubmissionFile } from '@/stores/state'
import { highlight } from '@/utils/CodeHighlighter'
import type { HighlightLanguage } from '@/model/Language'

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
    type: String as PropType<HighlightLanguage>,
    required: true
  }
})

const emit = defineEmits(['lineSelected'])
const collapsed = ref(true)
const lineRefs = ref<HTMLElement[]>([])

function scrollTo(lineNumber: number) {
  collapsed.value = false
  nextTick(function () {
    lineRefs.value[lineNumber - 1].scrollIntoView({ behavior: 'smooth', block: 'center' })
  })
}

defineExpose({
  scrollTo
})

const codeLines: { line: string; match: null | Match }[] = highlight(
  props.file.data,
  props.highlightLanguage
).map((line, index) => {
  return {
    line,
    match: props.matches?.find((m) => m.start <= index + 1 && index + 1 <= m.end)?.match ?? null
  }
})

function lineSelected(lineIndex: number) {
  if (codeLines[lineIndex].match !== null) {
    emit('lineSelected', codeLines[lineIndex].match)
  }
}

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
</style>
