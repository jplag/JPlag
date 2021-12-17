<template>
<div class="wrapper" v-bind:class="{ selected : isSelected }">
  <div class="metric">
    <p class="metric-name">{{ metricName }}</p>
    <img src="@/assets/help_outline_black_18dp.svg" alt="?"/>
  </div>
  <div class="threshold">
    <p>Threshold: </p>
    <p>{{ metricThreshold }}%</p>
  </div>
</div>
</template>

<script>
import {defineComponent} from "vue";

export default defineComponent({
  name: "MetricButton",
  props: {
    metricName: {
      type: String,
      required: true
    },
    metricThreshold: {
      type: Number,
      required: true
    },
    isSelected : {
      type: Boolean,
      default: false
    },
  },
  setup() {

    return {
    }
  }
})
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 5px;
  box-shadow: #777777 2px 3px 3px;
  padding: 3%;
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
  background: var(--tertiary-color-dark);
  color: var(--on-primary-color-accent);
}

.metric {
  margin-bottom: 2%;
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
  background: var(--tertiary-color);
  box-shadow: var(--shadow-color) 2px 3px 3px;
}

.selected p {
  color: white;
}
</style>