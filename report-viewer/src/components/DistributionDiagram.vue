<!--
  Bar diagram, displaying the distribution for the selected metric.
-->
<template>
  <div>
    <Bar :data="chartData" :options="options" />
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { graphColors } from '@/utils/ColorUtils'
import type { Distribution } from '@/model/Distribution'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

const props = defineProps({
  distribution: {
    type: Object as PropType<Distribution>,
    required: true
  }
})

const maxVal = computed(() => Math.max(...props.distribution.splitIntoTenBuckets()))
const labels = [
  '91-100%',
  '81-90%',
  '71-80%',
  '61-70%',
  '51-60%',
  '41-50%',
  '31-40%',
  '21-30%',
  '11-20%',
  '0-10%'
]
const dataSetStyle = computed(() => {
  return {
    label: 'Comparison Count',
    backgroundColor: graphColors.contentFill,
    borderWidth: 1,
    borderColor: graphColors.contentBorder,
    tickColor: graphColors.ticksAndFont.value
  }
})

const chartData = computed(() => {
  return {
    labels: labels,
    datasets: [
      {
        ...dataSetStyle.value,
        data: props.distribution.splitIntoTenBuckets()
      }
    ]
  }
})

const options = computed(() => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y' as 'y',
    scales: {
      x: {
        //Highest count of submissions in a percentage range. We set the diagrams maximum shown value to maxVal + 5,
        //otherwise maximum is set to the highest count of submissions and is one bar always reaches the end.
        suggestedMax: maxVal.value + 5,
        ticks: {
          color: graphColors.ticksAndFont.value
        },
        grid: {
          color: graphColors.gridLines.value
        }
      },
      y: {
        ticks: {
          color: graphColors.ticksAndFont.value
        },
        grid: {
          color: graphColors.gridLines.value
        }
      }
    },
    plugins: {
      datalabels: {
        color: graphColors.ticksAndFont.value,
        font: {
          weight: 'bold' as 'bold'
        },
        anchor: 'end' as 'end',
        align: 'end' as 'end',
        clamp: true
      },
      legend: {
        display: false
      }
    }
  }
})
</script>
