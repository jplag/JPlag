<!--
  Button which displays the information for a used metric and is used to select for which
  metric the distribution and comparisons are displayed.
-->
<template>
  <div class="wrapper" v-bind:class="{ selected: isSelected }">
    <div>
      <p class="metric-name">{{ metric.metricName }}</p>
      <div class="tooltip">
        <img alt="?" src="@/assets/help_outline_black_18dp.svg" />
        <span class="tooltiptext">{{metric.description}}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { Metric } from "@/model/Metric";

export default defineComponent({
  name: "MetricButton",
  props: {
    metric: {
      type: Object as PropType<Metric>,
      required: true,
    },
    isSelected: {
      type: Boolean,
      default: false,
    },
  },
  setup() {
    return {};
  },
});
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 5px;
  box-shadow: #777777 2px 3px 3px;
  padding: 5%;
  margin-right: 5%;
}

.wrapper > div > * {
  margin: 0;
  padding: 0;
}

.wrapper > div {
  display: flex;
  justify-content: space-between;
}

.wrapper:hover {
  background: var(--secondary-color-dark);
  color: white;
}



.metric-name {
  font-size: xx-large;
  font-weight: bold;
  margin: 0;
}

.threshold {
  font-size: small;
  font-weight: bold;
}

.selected {
  background: var(--secondary-color);
  box-shadow: var(--shadow-color) 2px 3px 3px;
}

.selected p {
  color: white;
}
.tooltip {
  position: relative;
  display: inline-block;
}

.tooltip .tooltiptext {
  visibility: hidden;
  width: 240px;
  background-color: black;
  color: #fff;
  text-align: center;
  padding: 10px ;
  border-radius: 6px;
  position: absolute;
  z-index: 1;
}

.tooltip:hover .tooltiptext {
  visibility: visible;
}
</style>
