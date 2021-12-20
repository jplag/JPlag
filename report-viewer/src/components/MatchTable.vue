<template>
<div class="match-table-container">
  <table>
    <tr>
      <th>{{ id1 }}</th>
      <th>{{ id2 }}</th>
    </tr>
    <tr>
      <th>File 1</th>
      <th>File 2</th>
      <th>Tokens</th>
    </tr>
    <tr v-for="( match, index ) in matches" :key="String(index).concat(match.start_in_first).concat(match.start_in_second)"
        @click="$emit('matchSelected', $event, match)"
        :style="{ background : match.color }">
      <td>
        <div class="td-content">
          <p>{{ match.first_file_name }}</p>
          <p>({{ match.start_in_first }} - {{ match.end_in_first }})</p>
        </div>
      </td>
      <td>
        <div class="td-content">
          <p>{{ match.second_file_name }}</p>
          <p>({{ match.start_in_second }} - {{ match.start_in_second }})</p>
        </div>
      </td>
      <td>TODO</td>
    </tr>
  </table>
</div>
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent({
  name: "MatchTable",
  props: {
    matches : {
      type: Array
    },
    id1: {
      type: String
    },
    id2: {
      type: String
    }
  },
  setup() {
    return {}
  }
})
</script>

<style scoped>
.match-table-container {
  width: 100%;
  margin-top: 2%;
  padding: 1%;
  background: var(--quaternary-color);
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
  border-bottom: solid var(--quaternary-color);
  border-top: solid var(--quaternary-color);
}
</style>