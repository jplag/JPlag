<template>
  <div>
    <canvas ref="graphCanvas"></canvas>
    <div v-show="!loaded">Could not display graph</div>
  </div>
</template>

<script setup lang="ts">
import type { ClusterListElement } from '@/model/ClusterListElement'
import { Chart, registerables } from 'chart.js'
import { ref, type PropType, type Ref, onMounted, computed, watch } from 'vue'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { EdgeLine, GraphController, GraphChart } from 'chartjs-chart-graph'
import { store } from '@/stores/store'
import { graphColors } from '@/utils/ColorUtils'

const props = defineProps({
  cluster: {
    type: Object as PropType<ClusterListElement>,
    required: true
  }
})

const graphCanvas: Ref<HTMLCanvasElement | null> = ref(null)
const loaded = ref(false)

Chart.register(...registerables)
Chart.register(ChartDataLabels)
Chart.register(EdgeLine)
Chart.register(GraphController)
Chart.register(GraphChart)

const keys = computed(() => Array.from(props.cluster.members.keys()))
const labels = computed(() =>
  Array.from(keys.value).map((m) =>
    store().state.anonymous.has(m) ? 'Hidden' : store().submissionDisplayName(m) ?? m
  )
)
const edges = computed(() => {
  const edges: { source: number; target: number }[] = []
  props.cluster.members.forEach((member, key) => {
    member.forEach((match) => {
      const firstIndex = keys.value.indexOf(key)
      const secondIndex = keys.value.indexOf(match.matchedWith)
      if (firstIndex < secondIndex) {
        edges.push({ source: firstIndex, target: secondIndex })
      }
    })
  })
  return edges
})

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

function getClampedSimilarityFromKeyIndex(firstIndex: number, secondIndex: number) {
  const similarity = getSimilarityFromKeyIndex(firstIndex, secondIndex)
  return (
    (similarity - minimumSimilarity.value) / (maximumSimilarity.value - minimumSimilarity.value)
  )
}

function getEdgeAlphaFromKeyIndex(firstIndex: number, secondIndex: number) {
  return getClampedSimilarityFromKeyIndex(firstIndex, secondIndex) * 0.7 + 0.3
}

const graphData = computed(() => {
  return {
    labels: labels.value,
    datasets: [
      {
        pointRadius: 10,
        pointHoverRadius: 10,
        pointBackgroundColor: graphColors.pointFill,
        pointHoverBackgroundColor: graphColors.pointFill,
        pointBorderColor: graphColors.ticksAndFont.value,
        pointHoverBorderColor: graphColors.ticksAndFont.value,
        data: Array.from(keys.value).map((_, index) => ({
          x: calculateXPosition(index),
          y: calculateYPosition(index)
        })),
        edges: edges.value,
        edgeLineBorderColor: (ctx: any) =>
          graphColors.contentFillAlpha(getEdgeAlphaFromKeyIndex(ctx.raw.source, ctx.raw.target)),
        edgeLineBorderWidth: (ctx: any) =>
          5 * getClampedSimilarityFromKeyIndex(ctx.raw.source, ctx.raw.target) + 1
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
    animation: false as false,
    plugins: {
      legend: { display: false },
      datalabels: {
        display: true,
        font: {
          weight: 'bold' as 'bold',
          size: 12
        },
        formatter: (value: any, ctx: any) => {
          return labels.value[ctx.dataIndex]
        },
        align: (ctx: any) => degreeAroundCircle(ctx.dataIndex),
        offset: 8,
        color: graphColors.ticksAndFont.value
      },
      tooltip: {
        enabled: true,
        displayColors: false,
        callbacks: {
          title: () => {
            return ''
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
    options: graphOptions.value
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
</script>
