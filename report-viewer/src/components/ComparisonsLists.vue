<template>
  <div class="wrapper">
    <ul id="list">
      <li v-for="(comparison, index) in comparisons" :key="comparison.first_submission
      + comparison.second_submission
      + comparison.match_percentage">
        <ComparisonListElement :index="index + 1"
                               :submission1="comparison.first_submission"
                               :submission2="comparison.second_submission"
                               :match-percentage="comparison.match_percentage"/>
      </li>
    </ul>
  </div>
</template>

<script>
import { defineComponent, ref, watch} from "vue";
import ComparisonListElement from "./ComparisonListElement";

export default defineComponent({
  name: "ComparisonsLists",
  components: {ComparisonListElement},
  props: {
    comparisons: {
      required: true
    }
  },
  setup(props) {

    watch( () => props.comparisons, (val, oldVal) => {
      console.log("New val is: " + JSON.stringify(val))
    })

    return {

    }
  }
})
</script>

<style scoped>
.wrapper {
  display: flex;
  background: whitesmoke;
  border-radius: 10px;
  align-items: center;
  box-shadow: #777777 2px 3px 3px;
  height: 300px;
}

ul {
  width: 100%;
  height: 100%;
  list-style-type: none;
  padding: 2% 1% 0;
  columns: 3;
  -webkit-columns: 3;
  -moz-columns: 3;
}
</style>