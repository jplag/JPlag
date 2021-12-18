<template>
  <div class="wrapper-list-element" @click="navigateToComparisonView()">
      <p class="index">{{ index }}.</p>
      <p class="submission-id">{{ submission1 }}</p>
      <img src="@/assets/double_arrow_black_18dp.svg" alt=">>"/>
      <p class="submission-id">{{ submission2 }}</p>
      <p class="match-percentage">{{ formattedMatchPercentage }}%</p>
  </div>
</template>

<script>
import {defineComponent, ref } from "vue";
import router from "@/router";

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
      type: Number,
      required: true
    }
  },
  setup(props) {
    let formattedMatchPercentage = props.matchPercentage.toFixed(2)

    const navigateToComparisonView = () => {
      router.push({
        name: "ComparisonView",
        params: {id1 : props.submission1, id2 : props.submission2}
      })
    }

    return {
      formattedMatchPercentage,
      navigateToComparisonView
    }
  }
})
</script>

<style scoped>
.wrapper-list-element {
  background: var(--quaternary-color);
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 2% 3%;
  margin-bottom: 2%;
  box-shadow: #777777 0 3px 3px;
  border-radius: 40px;
}

.wrapper-list-element > * {
  margin-top: 0;
  margin-bottom: 0;
}

.wrapper-list-element:hover {
  background: #FF5353;
  cursor: pointer;
}

.wrapper-list-element:hover p {
  color: white;
}

img {
  flex-shrink: 2;
}

p {
  font-weight: bold;
  font-size: large;
  text-align: center;
  text-anchor: middle;
}
</style>