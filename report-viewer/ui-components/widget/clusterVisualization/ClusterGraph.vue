<template>
  <div class="flex max-h-full flex-col overflow-hidden print:max-w-full">
    <div class="flex max-h-full flex-col overflow-hidden print:max-w-full">
      <canvas ref="graphCanvas" class="min-h-0 grow print:max-h-full print:max-w-full"></canvas>
      <div class="mt-5 text-xs font-bold text-gray-500 dark:text-gray-400 print:hidden">
        <p>Hover over an edge to highlight it in the table.</p>
        <p v-if="!allComparisonsPresent" class="mt-2">
          Not all comparisons of this cluster are present. These comparisons are indicated by the
          dashed lines. <br />
          To include more comparisons, increase the number of increased comparisons in the CLI.
        </p>
      </div>
    </div>

    <div v-show="!loaded">Could not display graph</div>
  </div>
</template>

<script setup lang="ts">
import { Chart, registerables, type ChartEvent } from 'chart.js'
import { ref, type PropType, type Ref, onMounted, computed, watch } from 'vue'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { EdgeLine, GraphController, GraphChart } from 'chartjs-chart-graph'
import { type ClusterListElement } from '@jplag/model'
import { graphColors } from '../../style/graphColor'

const props = defineProps({
  cluster: {
    type: Object as PropType<ClusterListElement>,
    required: true
  },
  highlightedEdge: {
    type: Object as PropType<{ firstId: string; secondId: string } | null>,
    default: null
  },
  getDisplayName: {
    type: Function as PropType<(id: string) => string>,
    required: false,
    default: (id: string) => id
  },
  useDarkMode: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits<{
  (event: 'lineHovered', value: { firstId: string; secondId: string } | null): void
  (event: 'lineClicked', value: { firstId: string; secondId: string }): void
}>()

const graphCanvas: Ref<HTMLCanvasElement | null> = ref(null)
const loaded = ref(false)

Chart.register(...registerables)
Chart.register(ChartDataLabels)
Chart.register(EdgeLine)
Chart.register(GraphController)
Chart.register(GraphChart)

const keys = computed(() => Array.from(props.cluster.members.keys()))
const labels = computed(() => Array.from(keys.value).map((m) => props.getDisplayName(m)))
const edges = computed(() => {
  const edges: { source: number; target: number }[] = []
  props.cluster.members.forEach((member1, key1) => {
    props.cluster.members.forEach((member2, key2) => {
      const firstIndex = keys.value.indexOf(key1)
      const secondIndex = keys.value.indexOf(key2)
      if (firstIndex < secondIndex) {
        edges.push({ source: firstIndex, target: secondIndex })
      }
    })
  })
  return edges
})

const highlightedEdgeIndexes = computed(() => {
  if (props.highlightedEdge == null) {
    return null
  }
  const firstIndex = keys.value.indexOf(props.highlightedEdge.firstId)
  const secondIndex = keys.value.indexOf(props.highlightedEdge.secondId)
  if (firstIndex == -1 || secondIndex == -1) {
    console.warn(
      `Could not find index for ${props.highlightedEdge.firstId} or ${props.highlightedEdge.secondId}`
    )
    return null
  }
  return { firstIndex, secondIndex }
})

type HoverableEdge = {
  sourceId: string
  targetId: string
  x1: number
  y1: number
  x2: number
  y2: number
}

const minHoverDistance = 0.02

const hoverableEdges = computed(() => {
  const edges: HoverableEdge[] = []
  props.cluster.members.forEach((member, key) => {
    member.forEach((match) => {
      const firstIndex = keys.value.indexOf(key)
      const secondIndex = keys.value.indexOf(match.matchedWith)
      if (firstIndex == -1 || secondIndex == -1) {
        console.warn(`Could not find index for ${key} or ${match.matchedWith}`)
      }
      if (firstIndex < secondIndex) {
        edges.push({
          sourceId: key,
          targetId: match.matchedWith,
          x1: calculateXPosition(firstIndex),
          y1: calculateYPosition(firstIndex),
          x2: calculateXPosition(secondIndex),
          y2: calculateYPosition(secondIndex)
        })
      }
    })
  })
  return edges
})

function distanceToEdge(edge: HoverableEdge, p: { x: number; y: number }) {
  const numerator = (edge.x2 - edge.x1) * (edge.y1 - p.y) - (edge.x1 - p.x) * (edge.y2 - edge.y1)
  const denominator = Math.sqrt(Math.pow(edge.x2 - edge.x1, 2) + Math.pow(edge.y2 - edge.y1, 2))
  return Math.abs(numerator / denominator)
}

/**
 * Checks that the hover is actually on the edge and not on any point of the extension beyond the nodes
 */
function isHoverOnLine(edge: HoverableEdge, p: { x: number; y: number }) {
  const minX = Math.min(edge.x1, edge.x2)
  const maxX = Math.max(edge.x1, edge.x2)
  const minY = Math.min(edge.y1, edge.y2)
  const maxY = Math.max(edge.y1, edge.y2)
  return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY
}

function getClosestEdge(p: { x: number; y: number }) {
  let closestEdge = { sourceId: '', targetId: '', x1: -1, y1: -1, x2: -1, y2: -1 }
  let closestDistance = Infinity
  hoverableEdges.value.forEach((edge) => {
    if (!isHoverOnLine(edge, p)) {
      return
    }
    const distance = distanceToEdge(edge, p)
    if (distance < closestDistance) {
      closestDistance = distance
      closestEdge = edge
    }
  })
  return { ...closestEdge, d: closestDistance }
}

function getSimilarityFromKeyIndex(firstIndex: number, secondIndex: number) {
  const firstSubmission = props.cluster.members.get(keys.value[firstIndex])
  if (!firstSubmission) {
    return 0
  }
  const match = firstSubmission.find((m) => m.matchedWith == keys.value[secondIndex])
  if (!match) {
    return 0
  }
  return match.similarity
}

const allComparisonsPresent = computed(() => {
  let allComparisonsPresent = true
  props.cluster.members.forEach((member) => {
    if (member.length != props.cluster.members.size - 1) {
      allComparisonsPresent = false
    }
  })
  return allComparisonsPresent
})

const minimumSimilarity = computed(() => {
  let minimumSimilarity = Infinity
  props.cluster.members.forEach((member) => {
    member.forEach((match) => {
      if (match.similarity < minimumSimilarity) {
        minimumSimilarity = match.similarity
      }
    })
  })
  return minimumSimilarity
})

const maximumSimilarity = computed(() => {
  let maximumSimilarity = 0
  props.cluster.members.forEach((member) => {
    member.forEach((match) => {
      if (match.similarity > maximumSimilarity) {
        maximumSimilarity = match.similarity
      }
    })
  })
  return maximumSimilarity
})

function getClampedSimilarityFromKeyIndex(
  firstIndex: number,
  secondIndex: number,
  min: number,
  max: number
) {
  const similarity = getSimilarityFromKeyIndex(firstIndex, secondIndex)
  if (similarity == 0) {
    return 0
  }
  if (min == max) {
    return 1
  }
  return (similarity - min) / (max - min)
}

function getEdgeAlphaFromKeyIndex(firstIndex: number, secondIndex: number) {
  const similarity = getSimilarityFromKeyIndex(firstIndex, secondIndex)
  if (similarity == 0) {
    return 1
  }
  return (
    getClampedSimilarityFromKeyIndex(
      firstIndex,
      secondIndex,
      Math.min(minimumSimilarity.value, 0.5),
      maximumSimilarity.value
    ) *
      0.7 +
    0.3
  )
}

function getEdgeWidth(firstIndex: number, secondIndex: number) {
  const similarity = getSimilarityFromKeyIndex(firstIndex, secondIndex)
  if (similarity == 0) {
    return 0.5
  }
  return getClampedSimilarityFromKeyIndex(firstIndex, secondIndex, 0, 1) * 5 + 1
}

function getEdgeDashStyle(firstIndex: number, secondIndex: number) {
  const similarity = getSimilarityFromKeyIndex(firstIndex, secondIndex)
  if (similarity == 0) {
    return [5, 8]
  }
  return []
}

const colors = computed(() => graphColors(props.useDarkMode))

function getEdgeColor(firstIndex: number, secondIndex: number) {
  let isHighlightedRow = false
  if (highlightedEdgeIndexes.value != null) {
    if (
      (highlightedEdgeIndexes.value.firstIndex == firstIndex &&
        highlightedEdgeIndexes.value.secondIndex == secondIndex) ||
      (highlightedEdgeIndexes.value.firstIndex == secondIndex &&
        highlightedEdgeIndexes.value.secondIndex == firstIndex)
    ) {
      isHighlightedRow = true
    }
  }
  const similarity = getSimilarityFromKeyIndex(firstIndex, secondIndex)
  if (similarity == 0) {
    return colors.value.additionalLine
  }
  if (isHighlightedRow) {
    return colors.value.highlightedLine(1)
  }
  return colors.value.contentFillAlpha(getEdgeAlphaFromKeyIndex(firstIndex, secondIndex))
}

const graphData = computed(() => {
  return {
    labels: labels.value,
    datasets: [
      {
        pointRadius: 10,
        pointHoverRadius: 10,
        pointBackgroundColor: colors.value.pointFill,
        pointHoverBackgroundColor: colors.value.pointFill,
        pointBorderColor: colors.value.ticksAndFont,
        pointHoverBorderColor: colors.value.ticksAndFont,
        data: Array.from(keys.value).map((_, index) => ({
          x: calculateXPosition(index),
          y: calculateYPosition(index)
        })),
        edges: edges.value,
        /* eslint-disable-next-line @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in the library
        edgeLineBorderColor: (ctx: any) => getEdgeColor(ctx.raw.source, ctx.raw.target),
        /* eslint-disable-next-line @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in the library
        edgeLineBorderWidth: (ctx: any) => getEdgeWidth(ctx.raw.source, ctx.raw.target),
        /* eslint-disable-next-line @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in the library
        edgeLineBorderDash: (ctx: any) => getEdgeDashStyle(ctx.raw.source, ctx.raw.target)
      }
    ]
  }
})

const yPadding = 40
const xPadding = computed(() => {
  const avgCharacterLength = 9

  const widths = labels.value.map((label) => label.length * avgCharacterLength)
  const maxWidth = Math.max(...widths)
  // Makes sure there is always space to display a name but the padding does not get too big
  return Math.max(Math.min(200, maxWidth), 40)
})

const hoveredEdge: Ref<{ firstId: string; secondId: string } | null> = ref(null)

const EdgeHoverPlugin = {
  id: 'edgeTooltip',
  afterEvent(
    chart: Chart,
    args: {
      event: ChartEvent
      replay: boolean
      changed?: boolean
      cancelable: false
      inChartArea: boolean
    }
  ) {
    const tooltip = chart.tooltip
    if (!tooltip) {
      return
    }

    if (hoveredEdge.value == null) {
      tooltip.setActiveElements([], { x: 0, y: 0 })
      return
    }
    const e = args.event
    tooltip.setActiveElements(
      [
        { datasetIndex: 0, index: keys.value.indexOf(hoveredEdge.value.firstId) },
        { datasetIndex: 0, index: keys.value.indexOf(hoveredEdge.value.secondId) }
      ],
      { x: e.x ?? 0, y: e.y ?? 0 }
    )
    //tooltip.update(true)
    //tooltip.draw(chart.ctx)
  }
}

const graphOptions = computed(() => {
  return {
    layout: {
      padding: {
        top: yPadding,
        bottom: yPadding,
        left: xPadding.value,
        right: xPadding.value
      }
    },
    /* eslint-disable-next-line @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in the library
    onHover: (event: any, elements: any) => {
      if (!event) {
        hoveredEdge.value = null
      } else if (elements.length > 0) {
        // Hovering over a node
        hoveredEdge.value = null
      } else if (chart.value != null) {
        const closestEdge = getClosestEdge({
          x: (chart.value as Chart).scales.x.getValueForPixel(event.x) ?? 0,
          y: (chart.value as Chart).scales.y.getValueForPixel(event.y) ?? 0
        })
        if (closestEdge.d > minHoverDistance) {
          hoveredEdge.value = null
        } else {
          hoveredEdge.value = {
            firstId: closestEdge.sourceId,
            secondId: closestEdge.targetId
          }
        }
      }
      if (graphCanvas.value != null) {
        graphCanvas.value.style.cursor = hoveredEdge.value != null ? 'pointer' : 'default'
      }
      emit('lineHovered', hoveredEdge.value)
    },
    onClick: () => {
      if (hoveredEdge.value != null) {
        emit('lineClicked', hoveredEdge.value)
      }
    },
    animation: false as const,
    plugins: {
      legend: { display: false },
      datalabels: {
        display: true,
        font: {
          weight: 'bold' as const,
          size: 12
        },
        /* eslint-disable-next-line @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in the library
        formatter: (value: any, ctx: any) => {
          return labels.value[ctx.dataIndex]
        },
        /* eslint-disable-next-line @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in the library
        align: (ctx: any) => degreeAroundCircle(ctx.dataIndex),
        offset: 8,
        color: colors.value.ticksAndFont
      },
      tooltip: {
        enabled: true,
        displayColors: false,
        callbacks: {
          title: () => {
            if (hoveredEdge.value == null) {
              return ''
            }
            return hoveredEdge.value.firstId + ' — ' + hoveredEdge.value.secondId
          },
          label: () => {
            return ''
          },
          beforeBody: () => {
            if (hoveredEdge.value == null) {
              return ''
            }
            const avgSimilarity =
              getSimilarityFromKeyIndex(
                keys.value.indexOf(hoveredEdge.value.firstId),
                keys.value.indexOf(hoveredEdge.value.secondId)
              ) * 100
            return `Average similarity: ${avgSimilarity.toFixed(2)}%`
          }
        }
      }
    }
  }
})

const chart: Ref<Chart | null> = ref(null)

function drawGraph() {
  if (chart.value != null) {
    chart.value.destroy()
  }
  if (graphCanvas.value == null) {
    loaded.value = false
    return
  }
  const ctx = graphCanvas.value.getContext('2d')
  if (ctx == null) {
    loaded.value = false
    return
  }
  chart.value = new Chart(ctx, {
    type: 'graph',
    data: graphData.value,
    options: graphOptions.value,
    plugins: [
      EdgeHoverPlugin,
      {
        id: 'onMouseOut',
        beforeEvent(chart, args) {
          const event = args.event
          if (event.type === 'mouseout') {
            emit('lineHovered', null)
          }
        }
      }
    ]
  })
  loaded.value = true
}

/**
 * Calculates the x position of a key in the graph [0, 2]
 * @param index The index of the key in the keys array
 */
function calculateXPosition(index: number) {
  return Math.cos((2 * Math.PI * index) / keys.value.length) + 1
}

/**
 * Calculates the y position of a key in the graph [0, 2]
 * @param index The index of the key in the keys array
 */
function calculateYPosition(index: number) {
  return Math.sin((2 * Math.PI * index) / keys.value.length) + 1
}

function degreeAroundCircle(index: number) {
  return (-360 * index) / keys.value.length
}

onMounted(() => {
  drawGraph()
})

watch(
  computed(() => {
    return {
      d: graphData.value,
      o: graphOptions.value
    }
  }),
  () => {
    drawGraph()
  }
)

watch(
  () => props.highlightedEdge,
  () => {
    drawGraph()
  }
)
</script>
