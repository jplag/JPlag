<template>
  <div class="wrapper">
    <ul id="list">
      <li v-for="(comparison, index) in comparisons" :key="index">
        <ComparisonListElement :index="index + 1"
                               :submission1="comparison.submission1"
                               :submission2="comparison.submission2"
                               :match-percentage="comparison.matchPercentage"/>
      </li>
    </ul>
  </div>
</template>

<script>
import { defineComponent, ref} from "vue";
import TopComparison from "../model/TopComparison";
import ComparisonListElement from "./ComparisonListElement";

export default defineComponent({
  name: "ComparisonsLists",
  components: {ComparisonListElement},
  props: {
    comparisons: {
      type: [TopComparison],
      required: true
    }
  },
})
</script>

<style scoped>
.wrapper {
  display: flex;
  background: white;
  border-radius: 10px;
  align-items: center;
  box-shadow: #777777 2px 3px 3px;
  flex-wrap: wrap;
}

ul {
  width: 100%;
  list-style-type: none;
  padding-right: 1%;
  padding-left: 1%;
  columns: 2;
  -webkit-columns: 2;
  -moz-columns: 2;
}
</style>