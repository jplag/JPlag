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
import { start } from 'repl'

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
  Array.from(keys.value).map((m) => store().submissionDisplayName(m) ?? m)
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

const graphData = computed(() => {
  return {
    labels: labels.value,
    datasets: [
      {
        pointRadius: 10,
        pointHoverRadius: 10,
        pointBackgroundColor: graphColors.contentFill,
        pointHoverBackgroundColor: graphColors.contentFill,
        pointBorderColor: graphColors.ticksAndFont.value,
        pointHoverBorderColor: graphColors.ticksAndFont.value,
        data: Array.from(keys.value).map((_, index) => ({
          x: Math.cos((2 * Math.PI * index) / keys.value.length) + 1,
          y: Math.sin((2 * Math.PI * index) / keys.value.length) + 1
        })),
        edges: edges.value,
        edgeLineBorderColor: (ctx: any) =>
          graphColors.contentFillAlpha(getSimilarityFromKeyIndex(ctx.raw.source, ctx.raw.target)),
        edgeLineBorderWidth: (ctx: any) =>
          5 * getSimilarityFromKeyIndex(ctx.raw.source, ctx.raw.target) + 1
      }
    ]
  }
})

const yPadding = 40
const xPadding = computed(() => {
  const avgCharacterLength = 8

  const widths = labels.value.map((label) => label.length * avgCharacterLength)
  const maxWidth = Math.max(...widths)
  // Space needed for the longest name, but at most 200
  console.log(maxWidth)
  return Math.min(200, maxWidth)
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
        align: (ctx: any) => (-360 * ctx.dataIndex) / keys.value.length,
        offset: 8,
        color: graphColors.ticksAndFont.value
      },
      tooltip: {
        enabled: false
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
