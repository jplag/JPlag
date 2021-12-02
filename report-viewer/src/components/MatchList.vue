<template>
<div class="wrapper">
  <select v-model="selected" @change="this.$emit('selectionChanged', $event, selected)">
    <option v-for="option in Object.keys(matches)" :value="option" :key="option">
      {{ option }}
    </option>
  </select>
  <table>
    <tr>
      <th>
        <p>{{ submission1 }}</p>
        <p>{{ selectedSplit[0] }}</p>
      </th>
      <th>
        <p>{{ submission2 }}</p>
        <p>{{ selectedSplit[1] }}</p>
      </th>
      <th>Matched tokens</th>
    </tr>
    <tr v-for="(match, index) in matches[selected]" :key="match.start_in_first + match.end_in_first"
        @click="this.$emit('matchSelected', $event, index)">
      <td>{{ match.start_in_first }} - {{ match.end_in_first }}</td>
      <td>{{ match.start_in_second }} - {{ match.end_in_second }}</td>
      <td>TODO</td>
    </tr>
  </table>
</div>
</template>

<script>
import { defineComponent, ref, watchEffect } from "vue";

export default defineComponent({
  name: "MatchList",
  props: {
    matches: {
      type: Array,
      required: true
    },
    submission1: {
      type: String,
      required: true
    },
    submission2: {
      type: String,
      required: true
    }
  },
  setup(props) {
    let selected = ref(Object.keys(props.matches)[0]);
    let selectedSplit = ref(selected.value.split(" - "))

    watchEffect(() => {
      selectedSplit.value = selected.value.split(" - ")
    })

    return {
      selected,
      selectedSplit,
    }
  }
})
</script>

<style scoped>
.wrapper {
  background: #ECECEC;
  border-radius: 10px;
  padding: 5%;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
  overflow-y: scroll;
  -ms-overflow-style: none;
}

.wrapper::-webkit-scrollbar {
  display: none;
}

select {
  margin-bottom: 5%;
  background: #ECECEC;
  border: none;
  border-bottom: solid rgba(119, 119, 119, 0.20);
}

table {
  border-collapse: collapse;
}

th {
  font-size: small;
}

th > p {
  margin: 0
}

tr {
  border-bottom: solid rgba(119, 119, 119, 0.20);
}

td {
  padding: 2% 0;
}

tr:hover {
  background: #FF5353;
  color: white;
}
</style>