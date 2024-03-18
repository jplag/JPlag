<template>
  <div
    class="col-span-1 col-start-2 row-span-1 flex w-full cursor-default"
    :class="{ 'cursor-pointer': matches.length > 0 }"
    :style="{
      gridRowStart: lineNumber
    }"
  >
    <div
      v-for="(part, index) in parts"
      :key="index"
      class="h-full last:flex-1"
      @click="matchSelected(part.match)"
      :style="{
        background:
          part.match != undefined
            ? getMatchColor(0.3, part.match.match.colorIndex)
            : 'hsla(0, 0%, 0%, 0)'
      }"
    >
      <pre
        v-html="part.line"
        class="code-font print-excact break-child !bg-transparent print:whitespace-pre-wrap"
      ></pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import { getMatchColor } from '@/utils/ColorUtils'
import { ref } from 'vue'

const props = defineProps({
  lineNumber: {
    type: Number,
    required: true
  },
  line: {
    type: String,
    required: true
  },
  matches: {
    type: Array<MatchInSingleFile>,
    required: true
  }
})

const emit = defineEmits(['matchSelected'])

function matchSelected(match?: MatchInSingleFile) {
  if (match) {
    emit('matchSelected', match)
  }
}

interface MatchPart {
  start: number
  end: number
  match?: MatchInSingleFile
}

interface Part extends MatchPart {
  line: string
}
let lineIndex = ref(0)
let colIndex = ref(0)

function compParts() {
  if (props.matches.length == 0) {
    return [{ line: props.line, start: 0, end: props.line.length }]
  }

  const sortedMatches = Array.from(props.matches)
    .sort((a, b) => a.startColumn - b.startColumn)
    .sort((a, b) => a.start - b.start)
  let matchParts: MatchPart[] = []

  if (sortedMatches[0].start == props.lineNumber && sortedMatches[0].startColumn > 0) {
    const end = sortedMatches[0].startColumn - 1
    matchParts.push({ start: 0, end: end })
  }

  const start = sortedMatches[0].start == props.lineNumber ? sortedMatches[0].startColumn : 0
  const end =
    sortedMatches[0].end == props.lineNumber ? sortedMatches[0].endColumn : props.line.length
  matchParts.push({ start: start, end: end, match: sortedMatches[0] })

  let matchIndex = 1
  while (matchIndex < sortedMatches.length) {
    const match = sortedMatches[matchIndex]
    const prevMatchPart = matchParts[matchIndex - 1]
    if (prevMatchPart.end + 1 < match.startColumn) {
      const end = match.startColumn - 1
      matchParts.push({ start: prevMatchPart.end + 1, end: end })
    }
    const end = match.end == props.lineNumber ? match.endColumn : props.line.length
    matchParts.push({ start: match.startColumn, end: end, match })
    matchIndex++
  }

  if (matchParts[matchParts.length - 1].end < props.line.length) {
    matchParts.push({ start: matchParts[matchParts.length - 1].end + 1, end: props.line.length })
  }

  let parts: Part[] = []
  lineIndex.value = 0
  colIndex.value = 0

  for (let i = 0; i < matchParts.length; i++) {
    const matchPart = matchParts[i]
    const line = getNextLinePartTill(matchPart.end)
    parts.push({ line, ...matchPart })
  }

  return parts
}

const parts = compParts()

function getNextLinePartTill(endCol: number) {
  let part = ''
  while (colIndex.value <= endCol && lineIndex.value < props.line.length) {
    if (props.line[lineIndex.value] == '<') {
      while (props.line[lineIndex.value] != '>') {
        part += props.line[lineIndex.value]
        lineIndex.value++
      }
      part += props.line[lineIndex.value]
      lineIndex.value++
    } else if (props.line[lineIndex.value] == '\t') {
      part += '    '
      lineIndex.value++
      colIndex.value++
    } else {
      part += props.line[lineIndex.value]
      lineIndex.value++
      colIndex.value++
    }
  }
  return part
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
