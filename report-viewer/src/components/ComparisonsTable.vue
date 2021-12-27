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
      @click="navigateToComparisonView(comparison.firstSubmissionId, comparison.secondSubmissionId)">
    <td>{{ index + 1 }}.</td>
    <td>{{ comparison.firstSubmissionId }}</td>
    <td><img src="@/assets/double_arrow_black_18dp.svg" alt=">>"/></td>
    <td>{{ comparison.secondSubmissionId }}</td>
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
   }
  },
  setup() {
    let formattedMatchPercentage = ( number ) => number.toFixed(2)

    const navigateToComparisonView = (id1, id2) => {
      router.push({
        name: "ComparisonView",
        params: { id1 : id1, id2 : id2 }
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
table {
  border-collapse: collapse;
  font-size: larger;
  text-align: center;
  color: var(--on-primary-color-accent);
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

.head-row {
  background: var(--primary-color-light);
}

.even-row {
  background: var(--secondary-color);
}

.even-row:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}

.odd-row {
  background: var(--primary-color-light);
}

.odd-row:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}
</style>