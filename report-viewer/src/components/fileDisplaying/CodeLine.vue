<template>
  <div
    class="col-span-1 col-start-2 row-span-1 flex w-full cursor-default"
    :class="{ 'cursor-pointer': matches.length > 0 }"
    :style="{
      gridRowStart: lineNumber
    }"
    ref="lineRef"
  >
    <div
      v-for="(part, index) in textParts"
      :key="index"
      class="print-excact h-full last:flex-1"
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
    emit('matchSelected', match.match)
  }
}

const lineRef = ref<HTMLElement | null>(null)

interface TextPart {
  line: string
  match?: MatchInSingleFile
}
let lineIndex = ref(0)
let colIndex = ref(0)

function computeTextParts() {
  if (props.matches.length == 0) {
    return [{ line: props.line }]
  }

  const sortedMatches = Array.from(props.matches)
    .sort((a, b) => a.startColumn - b.startColumn)
    .sort((a, b) => a.start - b.start)
  let lineParts: {
    start: number
    end: number
    match?: MatchInSingleFile
  }[] = []

  if (sortedMatches[0].start == props.lineNumber && sortedMatches[0].startColumn > 0) {
    const end = sortedMatches[0].startColumn - 1
    lineParts.push({ start: 0, end: end })
  }

  const start = sortedMatches[0].start == props.lineNumber ? sortedMatches[0].startColumn : 0
  const end =
    sortedMatches[0].end == props.lineNumber ? sortedMatches[0].endColumn : props.line.length
  lineParts.push({ start: start, end: end, match: sortedMatches[0] })

  let matchIndex = 1
  while (matchIndex < sortedMatches.length) {
    const match = sortedMatches[matchIndex]
    const prevMatchPart = lineParts[matchIndex - 1]
    if (prevMatchPart.end + 1 < match.startColumn) {
      const end = match.startColumn - 1
      lineParts.push({ start: prevMatchPart.end + 1, end: end })
    }
    const end = match.end == props.lineNumber ? match.endColumn : props.line.length
    lineParts.push({ start: match.startColumn, end: end, match })
    matchIndex++
  }

  if (lineParts[lineParts.length - 1].end < props.line.length) {
    lineParts.push({ start: lineParts[lineParts.length - 1].end + 1, end: props.line.length })
  }

  let textParts: TextPart[] = []
  lineIndex.value = 0
  colIndex.value = 0

  for (const matchPart of lineParts) {
    const line = getNextLinePartTillColumn(matchPart.end)
    textParts.push({ line, match: matchPart.match })
  }

  return textParts
}

const textParts = computeTextParts()

function getNextLinePartTillColumn(endCol: number) {
  let part = ''
  while (colIndex.value <= endCol && lineIndex.value < props.line.length) {
    // spans from highlighting do not count as characters in the code
    if (props.line[lineIndex.value] == '<') {
      while (props.line[lineIndex.value] != '>') {
        part += props.line[lineIndex.value]
        lineIndex.value++
      }
      part += props.line[lineIndex.value]
      lineIndex.value++
    } else if (props.line[lineIndex.value] == '\t') {
      // display tabs properly
      part += '    '
      lineIndex.value++
      colIndex.value += 1
    } else if (props.line[lineIndex.value] == '&') {
      // html escape characters for e.g. <,>,&
      while (props.line[lineIndex.value] != ';') {
        part += props.line[lineIndex.value]
        lineIndex.value++
      }
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
