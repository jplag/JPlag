<!--
  Panel which displays a submission files with its line of code.
-->
<template>
  <Interactable class="!shadow mx-2">
    <div @click="collapsed = !collapsed" class="text-center font-bold">
      {{ title }}
    </div>
    <div class="mx-1 overflow-x-auto">
      <div v-if="!collapsed" class="w-fit min-w-full !text-xs">
        <table v-if="!isEmpty(lines)" class="w-full">
          <tr
            v-for="(line, index) in result"
            :key="index"
            class="w-full cursor-default"
            :class="{ 'cursor-pointer': lineMatch[index] !== null }"
            @click="lineSelected(index + 1)"
          >
            <td class="float-right pr-3">{{ index + 1 }}</td>
            <td
              class="w-full"
              :style="{
                background:
                  lineMatch[index] !== null ? lineMatch[index]?.color : 'hsla(0, 0%, 0%, 0)'
              }"
            >
              <pre v-html="line" class="hljs" ref="lineRefs"></pre>
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
import { ref, nextTick } from 'vue'
import Interactable from './InteractableComponent.vue'
import hljs from 'highlight.js'
import type { Match } from '@/model/Match'

const props = defineProps({
  /**
   * Title of the displayed file.
   */
  title: {
    type: String,
    required: true
  },
  /**
   * Index of file amongst other files in submission.
   */
  fileIndex: {
    type: Number,
    required: true
  },
  /**
   * Code lines of the file.
   * type: Array<string>
   */
  lines: {
    type: Array<string>,
    required: true
  },
  /**
   * Matches in the file
   * type: Array<MatchInSingleFile>
   */
  matches: {
    type: Array<MatchInSingleFile>,
    required: false
  }
})

const emit = defineEmits(['lineSelected'])

const collapsed = ref(true)

/**
 * @param lines Array of lines to check.
 * @returns true if all lines are empty, false otherwise.
 */
function isEmpty(lines: string[]) {
  return lines.length === 0 || lines.every((line) => !line.trim())
}

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

const value = hljs.highlight(props.lines.join('\n'), { language: 'java' }).value
const openTags: string[] = []
const result = value
  .replace(/(<span [^>]+>)|(<\/span>)|(\n)/g, (match) => {
    if (match === '\n') {
      return '</span>'.repeat(openTags.length) + '\n' + openTags.join('')
    }

    if (match === '</span>') {
      openTags.pop()
    } else {
      openTags.push(match)
    }

    return match
  })
  .split('\n')

const lineMatch: (null | Match)[] = new Array(result.length).fill(null)
props.matches?.forEach((m) => {
  for (let i = m.start; i <= m.end; i++) {
    //assign match color to line
    lineMatch[i - 1] = m.match
  }
})

function lineSelected(line: number) {
  if (lineMatch[line - 1] !== null) {
    emit('lineSelected', lineMatch[line - 1])
  }
}
</script>

<style scoped>
.hljs {
  font-family: 'JetBrains Mono NL', monospace !important;
  background: transparent !important;
}
</style>
