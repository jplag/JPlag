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
  Array.from(keys.value).map((m) => store().submissionDisplayName(m) ?? m)
)
console.log(labels.value)
const edges = computed(() => {
  const edges: { source: number; target: number }[] = []
  props.cluster.members.forEach((member, key) => {
    member.forEach((match) => {
      const firstIndex = keys.value.indexOf(key)
      const secondIndex = keys.value.indexOf(match.matchedWith)
      if (firstIndex == -1 || secondIndex == -1) {
        console.log(`Could not find index for ${key} or ${match.matchedWith}`)
      }
      if (firstIndex < secondIndex) {
        edges.push({ source: firstIndex, target: secondIndex })
      }
    })
  })
  return edges
})

const data = computed(() => {
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
        data: Array.from(keys.value).map((_, index) => ({ x: getX(index), y: getY(index) })),
        edges: edges.value,
        edgeLineBorderColor: (ctx: any) =>
          borderColor(keys.value[ctx.raw.source], keys.value[ctx.raw.target]),
        edgeLineBorderWidth: (ctx: any) =>
          borderWidth(keys.value[ctx.raw.source], keys.value[ctx.raw.target])
      }
    ]
  }
})

const options = computed(() => {
  return {
    animation: false as false,
    plugins: {
      legend: { display: false },
      datalabels: {
        display: true,
        font: {
          weight: 'bold' as 'bold',
          size: 16
        },
        formatter: (value: any, ctx: any) => {
          return labels.value[ctx.dataIndex]
        },
        offset: 12,
        color: graphColors.ticksAndFont.value
      },
      tooltip: {
        enabled: false
      }
    }
  }
})

function borderColor(firstSubmissionId: string, secondSubmissionId: string) {
  const firstSubmission = props.cluster.members.get(firstSubmissionId)
  if (!firstSubmission) {
    return 'rgba(0,0,0,0)'
  }
  const match = firstSubmission.find((m) => m.matchedWith == secondSubmissionId)
  if (!match) {
    return 'rgba(0,0,0,0)'
  }
  return graphColors.contentFillAlpha(match.similarity)
}

function borderWidth(firstSubmissionId: string, secondSubmissionId: string) {
  const firstSubmission = props.cluster.members.get(firstSubmissionId)
  if (!firstSubmission) {
    return 'rgba(0,0,0,0)'
  }
  const match = firstSubmission.find((m) => m.matchedWith == secondSubmissionId)
  if (!match) {
    return 'rgba(0,0,0,0)'
  }
  return 4 * match.similarity + 1
}

function getX(index: number) {
  return Math.sin((2 * Math.PI * index) / keys.value.length) + 1
}

function getY(index: number) {
  return Math.cos((2 * Math.PI * index) / keys.value.length) + 1
}

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
  console.log(graphCanvas.value)
  chart.value = new Chart(ctx, {
    type: 'graph',
    data: data.value,
    options: options.value
  })
  loaded.value = true
}

onMounted(() => {
  drawGraph()
})

watch(
  computed(() => {
    return {
      d: data.value,
      o: options.value
    }
  }),
  () => {
    drawGraph()
  }
)
</script>
