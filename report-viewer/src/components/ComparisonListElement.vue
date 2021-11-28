<template>
  <div class="wrapper-list-element">
    <p class="index">{{ index }}</p>
      <p class="submission-id">{{ submission1 }}</p>
      <img src="@/assets/double_arrow_black_18dp.svg" alt=">>"/>
      <p class="submission-id">{{ submission2 }}</p>
    <p class="match-percentage">{{ matchPercentage }}%</p>
  </div>
</template>

<script>
import {defineComponent, ref } from "vue";

export default defineComponent({
  name: "ComparisonListElement",
  props: {
    index: {
      type: Number,
      required: true
    },
    submission1: {
      type: String,
      required: true
    },
    submission2: {
      type: String,
      required: true
    },
    matchPercentage: {
      type: String,
      required: true
    }
  },
  setup() {

    return {

    }
  }
})
</script>

<style scoped>
.wrapper-list-element {
  background: white;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0 1%;
  margin-bottom: 1%;
  box-shadow: #777777 0 3px 3px;
  border-radius: 40px;
  width: 25%;
}

.wrapper-list-element > * {
  margin-top: 0;
  margin-bottom: 0;
}

img {
  flex-shrink: 2;
}

p {
  font-weight: bold;
  font-size: small;
  text-align: center;
  text-anchor: middle;
}
</style>