<template>
  <div class="flex">
    <canvas ref="canvas" class="h-full w-full flex-grow"> </canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, type PropType, onMounted, watch } from 'vue'
import { store } from '@/stores/store'
import type { BaseCodeMatch } from '@/model/BaseCodeReport'
import type { MatchInSingleFile } from '@/model/MatchInSingleFile'
import type { SubmissionFile } from '@/model/File'
import { getMatchColor, type MatchColorIndex } from '@/utils/ColorUtils'

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
   * Base code matches of the submission.
   */
  baseCodeMatches: {
    type: Array as PropType<BaseCodeMatch[]>,
    required: true
  }
})

const canvas = ref<HTMLCanvasElement | null>(null)

const nonMatchColor = computed(() => {
  return store().uiState.useDarkMode ? '#e3e3e3' : '#353535'
})
const canvasBgColor = computed(() => {
  return store().uiState.useDarkMode ? 'hsl(250, 10%, 15%)' : 'hsl(0, 0%, 98%)'
})

interface FilePart {
  colorIndex: MatchColorIndex
  match?: MatchInSingleFile
  length: number
}

const fileParts = computed(() => {
  const fileParts: Record<string, FilePart[]> = {}

  for (const file of props.files) {
    const parts: FilePart[] = []
    const matches = props.matches.get(file.fileName) ?? []
    for (const match of matches) {
      parts.push({
        colorIndex: match.match.colorIndex,
        match,
        length: match.match.tokens
      })
    }
    if (file.matchedTokenCount < file.tokenCount) {
      parts.push({
        colorIndex: undefined,
        length: file.tokenCount - file.matchedTokenCount - 1
      })
    }
    fileParts[file.fileName] = parts
  }

  return fileParts
})

function prepareCanvas(): [CanvasRenderingContext2D | null, number, number] {
  if (!canvas.value) {
    return [null, 0, 0]
  }
  const ctx = canvas.value.getContext('2d')
  if (!ctx) {
    return [null, 0, 0]
  }
  const { width, height } = canvas.value.getBoundingClientRect()
  ctx.canvas.width = width
  ctx.canvas.height = height
  ctx.fillStyle = canvasBgColor.value
  ctx.fillRect(0, 0, width, height)
  return [ctx, width, height]
}

function drawTest() {
  drawTokenSquares()
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function drawFileLines() {
  const [ctx, WIDTH, HEIGHT] = prepareCanvas()
  if (!ctx) {
    return
  }

  const padding = 4
  const lineHeight = (HEIGHT - padding) / (props.files.length + padding)
  for (const [i, file] of props.files.entries()) {
    const parts = fileParts.value[file.fileName]
    let x = 0
    for (const part of parts) {
      ctx.fillStyle =
        part.colorIndex !== undefined ? getMatchColor(1, part.colorIndex) : nonMatchColor.value
      const l = Math.round((part.length / file.tokenCount) * WIDTH)
      ctx.fillRect(x, i * (lineHeight + padding), l, lineHeight)
      x += l
    }
  }
}

function drawTokenSquares() {
  const allMatches = props.files
    .flatMap((file) => props.matches.get(file.fileName) ?? [])
    .sort((a, b) => b.match.tokens - a.match.tokens)
  const colors = [] as string[]
  for (const match of allMatches) {
    for (let i = 0; i < match.match.tokens; i++) {
      colors.push(getMatchColor(1, match.match.colorIndex))
    }
  }
  const totalTokens =
    props.files.reduce((acc, file) => acc + file.tokenCount, 0) - props.files.length
  const nonMatchTokens =
    totalTokens - allMatches.reduce((acc, match) => acc + match.match.tokens, 0)
  for (let i = 0; i < nonMatchTokens; i++) {
    colors.push(nonMatchColor.value)
  }
  drawSquares(colors)
}

function drawSquares(colors: string[]) {
  const [ctx, WIDTH, HEIGHT] = prepareCanvas()
  if (!ctx) {
    return
  }

  const padding = 2
  const totalSquares = colors.length
  const verticalSquareCount = Math.ceil(Math.sqrt((HEIGHT * totalSquares) / WIDTH))
  const horizontalSquareCount = Math.ceil(totalSquares / verticalSquareCount)
  const squareSize = Math.floor(
    HEIGHT / verticalSquareCount - padding + padding / verticalSquareCount
  )

  for (let x = 0; x < verticalSquareCount; x++) {
    for (let y = 0; y < horizontalSquareCount; y++) {
      const matchIndex = x * horizontalSquareCount + y
      if (matchIndex >= totalSquares) {
        break
      }
      ctx.fillStyle = colors[matchIndex]
      ctx.fillRect(
        y * squareSize + y * padding,
        x * squareSize + x * padding,
        squareSize,
        squareSize
      )
    }
  }
}

onMounted(() => {
  drawTest()
})

watch(
  () => store().uiState.useDarkMode,
  () => {
    drawTest()
  }
)
</script>
