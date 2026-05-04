<!--
  Bar diagram, displaying the distribution for the selected metric.
-->
<template>
  <div class="flex flex-col">
    <div class="h-3/4 w-full print:h-fit print:w-fit">
      <canvas ref="graphCanvas"></canvas>
    </div>

    <DistributionDiagramOptions v-model="config" class="grow print:grow-0" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch, type PropType, type Ref } from 'vue'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { graphColors } from '../../style/graphColor'
import type { DistributionMap } from '@jplag/model'
import DistributionDiagramOptions from './DistributionDiagramOptions.vue'
import { DistributionChartConfig } from './DistributionChartConfig'
import { binomialCoefficients } from './BinomialCoefficients'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

const props = defineProps({
  distributions: {
    type: Object as PropType<DistributionMap>,
    required: true
  },
  useDarkMode: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits<{
  (e: 'click:upperPercentile', UpperPercentile: number): void
}>()

const config = defineModel<DistributionChartConfig>('config', {
  default: {
    metric: 'averageSimilarity',
    xScale: 'linear',
    bucketCount: 10
  }
})

const distribution = computed(() => props.distributions[config.value.metric])
const distributionData = computed(() =>
  distribution.value.splitIntoBuckets(config.value.bucketCount)
)

const maxVal = computed(() => Math.max(...distributionData.value))

const labels = computed(() => {
  let labels = []
  for (let i = 0; i < distributionData.value.length; i++) {
    labels.push(getDataPointLabel(i))
  }
  return labels.reverse()
})

const colors = computed(() => graphColors(props.useDarkMode))

const dataSetStyle = computed(() => {
  return {
    label: 'Comparisons in bucket',
    backgroundColor: colors.value.contentFill,
    borderWidth: 1,
    borderColor: colors.value.contentBorder,
    tickColor: colors.value.ticksAndFont
  }
})

const curveData = computed(() => {
  const totalComparisons = distributionData.value.reduce((a, b) => a + b, 0)
  const bucketSum = distributionData.value
    .map((value, index) => value * index)
    .reduce((a, b) => a + b, 0)
  const E = bucketSum / totalComparisons
  const p = E / config.value.bucketCount
  const q = 1 - p
  let binomialDistribution = binomialCoefficients[config.value.bucketCount].map(
    (value, idx) => value * p ** idx * q ** (config.value.bucketCount - idx) * totalComparisons
  )
  return binomialDistribution
})

const isLogScale = computed(() => config.value.xScale === 'logarithmic')

const chartData = computed(() => {
  return {
    labels: labels.value,
    datasets: [
      {
        ...dataSetStyle.value,
        data: distributionData.value.map((v) => (isLogScale.value ? v + 1 : v))
      },
      {
        label: 'Binomial Curve',
        backgroundColor: 'rgba(0, 0, 255, 1)',
        borderWidth: 2,
        borderColor: 'rgba(0, 0, 255, 1)',
        type: 'line',
        cubicInterpolationMode: 'monotone',
        data: curveData.value.map((v) => (isLogScale.value ? v + 1 : v))
      }
    ]
  }
})

const options = computed(() => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y' as const,
    elements: {
      line: {
        tension: 0.4
      },
      point: {
        radius: 0
      }
    },
    scales: {
      x: {
        //Highest count of submissions in a percentage range. We set the diagrams maximum shown value to maxVal + 5,
        //otherwise maximum is set to the highest count of submissions and is one bar always reaches the end.
        suggestedMax:
          config.value.xScale === 'linear'
            ? maxVal.value + 5
            : 10 ** Math.ceil(Math.log10(maxVal.value + 5)),
        type: config.value.xScale,
        suggestedMin: config.value.xScale === 'logarithmic' ? 1 : undefined,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        afterBuildTicks: (axis: any) => {
          if (config.value.xScale === 'logarithmic') {
            axis.ticks = []
            let tickValue = 10
            while (tickValue < axis.max) {
              axis.ticks.push({ value: tickValue + 1 })
              tickValue *= 10
            }
            axis.ticks.push({ value: tickValue + 1 })
          }
        },
        ticks: {
          // ensures that in log mode tick labels are not overlapping
          minRotation: config.value.xScale === 'logarithmic' ? 30 : 0,
          autoSkipPadding: 10,

          color: colors.value.ticksAndFont,
          // ensures that in log mode ticks are placed evenly apart
          /* eslint-disable @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in chart.js
          callback: function (value: any) {
            if (config.value.xScale === 'logarithmic') {
              return value - 1
            }
            return value
          }
        },
        grid: {
          color: colors.value.gridLines
        }
      },
      y: {
        ticks: {
          color: colors.value.ticksAndFont,
          /* eslint-disable @typescript-eslint/no-explicit-any */ // needs to be any since it is defined like that in chart.js
          callback: function (reversedValue: any) {
            const value = distributionData.value.length - reversedValue - 1
            if (config.value.bucketCount <= 10) {
              return getDataPointLabel(value)
            } else {
              let labelBreakPoint: number
              if (config.value.bucketCount <= 25) {
                labelBreakPoint = 5
              } else {
                labelBreakPoint = Math.floor(config.value.bucketCount / 10)
              }
              if (value == config.value.bucketCount - 1 || value % labelBreakPoint == 0) {
                return getDataPointLabel(value)
              }
            }
          }
        },
        grid: {
          color: colors.value.gridLines
        }
      }
    },
    animation: false as const,
    plugins: {
      datalabels: {
        color: colors.value.ticksAndFont,
        font: {
          weight: 'bold' as const,
          size: getDataLabelFontSize()
        },
        anchor: 'end' as const,
        align: 'end' as const,
        clamp: true,
        text: 'test',
        display: (context: any) => {
          return context.datasetIndex === 0
        },
        formatter: (value) => {
          if (isLogScale.value) {
            return value - 1
          }
          return value
        }
      },
      legend: {
        display: true,
        position: 'bottom' as const,
        align: 'end' as const,
        onClick: () => {}
      }
    },
    // @ts-expect-error As there is not type to satisfy both the getElementsAtEventForMode function and Chart creations we leave the type out
    onClick: (e) => {
      if (!chart.value) {
        return
      }

      const points = chart.value.getElementsAtEventForMode(e, 'nearest', { intersect: true }, true)
      if (points.length <= 0 || points.length > 1) {
        return
      }
      const index = points[0].index
      const clickedBucket = distributionData.value.length - index
      const clickedUpperPercentile = clickedBucket * (100 / config.value.bucketCount)
      emit('click:upperPercentile', clickedUpperPercentile)
    }
  }
})

function getDataPointLabel(index: number) {
  let perBucket = 100 / config.value.bucketCount
  return index * perBucket + '-' + (index * perBucket + perBucket) + '%'
}

function getDataLabelFontSize() {
  if (config.value.bucketCount == 100) {
    return 7
  }
  if (config.value.bucketCount == 50) {
    return 10
  }
  return 12
}

const chart: Ref<Chart | null> = ref(null)
const loaded: Ref<boolean> = ref(false)
const graphCanvas: Ref<HTMLCanvasElement | null> = ref(null)

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
    type: 'bar',
    data: chartData.value,
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
      d: chartData.value,
      o: options.value
    }
  }),
  () => {
    drawGraph()
  }
)
</script>
