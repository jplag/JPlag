<!--
  Bar diagram, displaying the distribution for the selected metric.
-->
<template>
  <div class="wrapper">
    <BarChart :chartData="chartData" :options="options" class="chart" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from "vue";
import { BarChart } from "vue-chart-3";
import { Chart, registerables } from "chart.js";
import ChartDataLabels from "chartjs-plugin-datalabels";

Chart.register(...registerables);
Chart.register(ChartDataLabels);

export default defineComponent({
  name: "DistributionDiagram",
  components: { BarChart },
  props: {
    distribution: {
      type: Array<number>,
      required: true,
    },
  },
  setup(props) {
    //Highest count of submissions in a percentage range. We set the diagrams maximum shown value to maxVal + 5,
    //otherwise maximum is set to the highest count of submissions and is one bar always reaches the end.
    let maxVal = ref(Math.max(...props.distribution));
    const labels = [
      "91-100%",
      "81-90%",
      "71-80%",
      "61-70%",
      "51-60%",
      "41-50%",
      "31-40%",
      "21-30%",
      "11-20%",
      "0-10%",
    ];
    const dataSetStyle = {
      label: "Count",
      backgroundColor: "rgba(149, 168, 241, 0.5)",
      borderWidth: 2,
      borderColor: "rgba(149, 168, 241, 1)",
      tickColor: "#000000",
    };

    let chartData = ref({
      labels: labels,
      datasets: [
        {
          ...dataSetStyle,
          data: props.distribution,
        },
      ],
    });

    const options = ref({
      responsive: true,
      maintainAspectRatio: false,
      indexAxis: "y",
      scales: {
        x: {
          suggestedMax: maxVal.value + 5,
          ticks: {
            color: "#000000",
          },
        },
        y: {
          ticks: {
            color: "#000000",
          },
        },
      },
      plugins: {
        datalabels: {
          color: "#000000",
          font: {
            weight: "bold",
          },
          anchor: "end",
          align: "end",
          clamp: true,
        },
        legend: {
          display: false,
        },
      },
    });

    //We watch the given distributions parameter. When the distribution of another metric is passed, the diagram is
    //updated with the new data.
    watch(
      () => props.distribution,
      (val) => {
        chartData.value = {
          labels: labels,
          datasets: [
            {
              ...dataSetStyle,
              data: val,
            },
          ],
        };

        maxVal.value = Math.max(...val);
        options.value.scales.x.suggestedMax = maxVal.value + 5;
      }
    );

    return {
      chartData,
      options,
    };
  },
});
</script>

<style scoped>
.wrapper {
  background: var(--background-color);
  border-radius: 10px;
  box-shadow: #777777 2px 3px 3px;
  display: flex;
  height: 100%;
}

.chart {
  width: 100%;
}
</style>
