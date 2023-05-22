<!--
  Table which contains all of the matches for a comparison with navigation links.
-->
<template>
  <div class="match-table-container">
    <table>
      <tr>
        <th>Submission 1</th>
        <th>Submission 2</th>
      </tr>
      <tr>
        <th>File 1</th>
        <th>File 2</th>
        <th>Tokens</th>
      </tr>
      <tr
        v-for="(match, index) in matches"
        :key="
          String(index).concat(match.startInFirst.toString()).concat(match.startInSecond.toString())
        "
        :style="{ background: match.color }"
        @click="$emit('matchSelected', $event, match)"
      >
        <td>
          <div class="td-content">
            <p>{{ convertSubmissionIdToName(match.firstFile, id1 || '') }}</p>
            <p>({{ match.startInFirst }} - {{ match.endInFirst }})</p>
          </div>
        </td>
        <td>
          <div class="td-content">
            <p>{{ convertSubmissionIdToName(match.secondFile, id2 || '') }}</p>
            <p>({{ match.startInSecond }} - {{ match.endInSecond }})</p>
          </div>
        </td>
        <td>{{ match.tokens }}</td>
      </tr>
    </table>
  </div>
</template>

<script setup lang="ts">
import store from '@/stores/store'
import type { Match } from '@/model/Match'

defineProps({
  /**
   * Matches of the comparison.
   * type: Array<Match>
   */
  matches: {
    type: Array<Match>
  },
  /**
   * ID of first submission
   */
  id1: {
    type: String
  },
  /**
   * ID of second submission
   */
  id2: {
    type: String
  }
})

defineEmits(['matchSelected'])

/**
 * converts the submissionId to the name in the path of match.
 * @param match
 * @param submissionId
 * @return new path of match
 */
function convertSubmissionIdToName(match: string, submissionId: string): string {
  const displayName = store().submissionDisplayName(submissionId) || submissionId
  return match.replace(submissionId, displayName)
}
</script>

<style scoped>
.match-table-container {
  width: 100%;
  margin-top: 2%;
  padding: 1%;
  background: var(--primary-color-dark);
  border-radius: 10px;
  overflow: auto;
}

.td-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-wrap: nowrap;
  padding: 5%;
}

.td-content > p {
  margin: 0;
}

table {
  border-collapse: collapse;
  width: 100%;
}

th {
  color: var(--on-primary-color);
  text-align: center;
}

td {
  font-size: small;
  text-align: center;
  padding: 2%;
}
</style>
