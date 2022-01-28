<template>
<table>
  <tr class="head-row">
    <th>No.</th>
    <th>Submission 1</th>
    <th></th>
    <th>Submission 2</th>
    <th>Match %</th>
  </tr>
  <tr v-for="(comparison, index) in topComparisons"
      :key="comparison.firstSubmissionId
            + comparison.secondSubmissionId
            + comparison.matchPercentage"
      :class="{ 'even-row' : index % 2 === 0, 'odd-row' : index % 2 !== 0 }"
      class="selectable"
      @click="navigateToComparisonView(comparison.firstSubmissionId, comparison.secondSubmissionId)"
      >
    <td>{{ index + 1 }}.</td>
    <td :class="{ anonymous : !notAnonymized.includes(comparison.firstSubmissionId)}">{{ notAnonymized.includes(comparison.firstSubmissionId) ? comparison.firstSubmissionId : "Hidden" }}</td>
    <td><img src="@/assets/double_arrow_black_18dp.svg" alt=">>"/></td>
    <td :class="{ anonymous : !notAnonymized.includes(comparison.secondSubmissionId)}">{{ notAnonymized.includes(comparison.secondSubmissionId) ? comparison.secondSubmissionId : "Hidden" }}</td>
    <td>{{ formattedMatchPercentage(comparison.matchPercentage) }}</td>
  </tr>
</table>
</template>

<script>
import { defineComponent } from "vue";
import router from "@/router";

export default defineComponent({
  name: "ComparisonsTable",
  props: {
   topComparisons : {
     type: Array,
     required: true
   },
    notAnonymized : {
     type: Array
    }
  },
  setup(props) {
    let formattedMatchPercentage = ( number ) => number.toFixed(2)

    const navigateToComparisonView = (id1, id2) => {
      router.push({
        name: "ComparisonView",
        query: {id1: id1, id2: id2},
        params: { notAnonymized: props.notAnonymized}
      })
    }
    return {
      formattedMatchPercentage,
      navigateToComparisonView,
    }
  }
})
</script>

<style scoped>
table {
  border-collapse: collapse;
  font-size: larger;
  text-align: center;
}

th {
  margin: 0;
  padding-top: 2%;
  padding-bottom: 2%;
  color: var(--on-primary-color);
}

td {
  padding-top: 3%;
  padding-bottom: 3%;
}

.anonymous {
  color: #777777;
  filter: blur(1px);
}

.head-row {
  background: var(--primary-color-light);
}

.even-row {
  background: var(--secondary-color);
}

.odd-row {
  background: var(--primary-color-light);
}

.selectable:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}
</style>