<!--
  Radar chart which displays a cluster member with their similarity to other
  participants in the cluster.
-->
<template>
  <div class="wrapper">
    <select v-model="selectedMember">
      <option v-for="(member, index) in cluster.members.keys()" :key="index">
        {{ member }}
      </option>
    </select>
    <RadarChart
      :chartData="chartData"
      :options="options"
      class="chart"
    ></RadarChart>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, Ref, ref, watch } from "vue";
import { RadarChart } from "vue-chart-3";
import { Chart, ChartData, registerables } from "chart.js";
import ChartDataLabels from "chartjs-plugin-datalabels";
import { ClusterListElement } from "@/model/ClusterListElement";

Chart.register(...registerables);
Chart.register(ChartDataLabels);

export default defineComponent({
  name: "ClusterRadarChart",
  components: { RadarChart },
  props: {
    cluster: {
      type: Object as PropType<ClusterListElement>,
      required: true,
    },
  },
  setup(props) {
    const getIdOfFirstSubmission = () =>
      props.cluster.members.keys().next().value;
    const selectedMember = ref(getIdOfFirstSubmission());

    const createLabelsFor = (member: string): Array<string> => {
      let matchedWith = new Array<string>();
      props.cluster.members
        .get(member)
        ?.forEach((m) => matchedWith.push(m.matchedWith));
      return matchedWith;
    };
    const createDataSetFor = (member: string) => {
      let data = new Array<number>();
      props.cluster.members
        .get(member)
        ?.forEach((m) => data.push(roundToTwoDecimals(m.percentage)));
      return data;
    };
    const roundToTwoDecimals = (num: number): number =>
      Math.round((num + Number.EPSILON) * 100) / 100;

    const chartStyle = {
      fill: true,
      backgroundColor: "rgba(149, 168, 241, 0.5)",
      borderColor: "rgba(149, 168, 241, 1)",
      pointBackgroundColor: "rgba(149, 168, 241, 1)",
      pointBorderColor: "#fff",
      pointHoverBackgroundColor: "#fff",
      pointHoverBorderColor: "rgb(255, 99, 132)",
    };

    const chartData: Ref<ChartData<"radar", (number | null)[], unknown>> = ref({
      labels: createLabelsFor(getIdOfFirstSubmission()),
      datasets: [
        {
          ...chartStyle,
          label: getIdOfFirstSubmission(),
          data: createDataSetFor(getIdOfFirstSubmission()),
        },
      ],
    });

    const options = ref({
      legend: {
        display: false,
      },
      scales: {
        r: {
          suggestedMin: 50,
          suggestedMax: 100,
        },
      },
    });

    watch(
      () => selectedMember.value,
      (val) => {
        chartData.value = {
          labels: createLabelsFor(val),
          datasets: [
            {
              ...chartStyle,
              label: val,
              data: createDataSetFor(val),
            },
          ],
        };
      }
    );

    return {
      selectedMember,
      chartData,
      options,
    };
  },
});
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
}

.chart {
  height: 50vw;
}
</style>
