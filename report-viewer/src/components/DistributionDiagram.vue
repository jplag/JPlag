<!--
  Bar diagram, displaying the distribution for the selected metric.
-->
<template>
  <BarChart :chartData="chartData" :options="options" />
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { BarChart } from 'vue-chart-3'
import { Chart, registerables } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'
import { useDarkMode } from '@/main'

Chart.register(...registerables)
Chart.register(ChartDataLabels)

const props = defineProps({
  distribution: {
    type: Array<number>,
    required: true
  }
})

const tickColor = computed(() => {
  return useDarkMode.value ? '#ffffff' : '#000000'
})

//Highest count of submissions in a percentage range. We set the diagrams maximum shown value to maxVal + 5,
//otherwise maximum is set to the highest count of submissions and is one bar always reaches the end.
const maxVal = ref(Math.max(...props.distribution))
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
    backgroundColor: 'rgb(190, 22, 34, 0.5)',
    borderWidth: 2,
    borderColor: 'rgb(127, 15, 24)',
    tickColor: tickColor.value
  }
})

let chartData = ref({
  labels: labels,
  datasets: [
    {
      ...dataSetStyle.value,
      data: props.distribution
    }
  ]
})

const options = computed(() => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y',
    scales: {
      x: {
        suggestedMax: maxVal.value + 5,
        ticks: {
          color: tickColor.value
        }
      },
      y: {
        ticks: {
          color: tickColor.value
        }
      }
    },
    plugins: {
      datalabels: {
        color: tickColor.value,
        font: {
          weight: 'bold'
        },
        anchor: 'end',
        align: 'end',
        clamp: true
      },
      legend: {
        display: false
      }
    }
  }
})

/* We watch the given distributions parameter. When the distribution of another metric is passed, the diagram is
  updated with the new data. */
watch(
  () => props.distribution,
  (val) => {
    chartData.value = {
      labels: labels,
      datasets: [
        {
          ...dataSetStyle.value,
          data: val
        }
      ]
    }

    maxVal.value = Math.max(...val)
    options.value.scales.x.suggestedMax = maxVal.value + 5
  }
)
</script>
