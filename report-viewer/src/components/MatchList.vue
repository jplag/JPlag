<template>
<div class="wrapper">
  <select v-model="selected" @change="this.$emit('selectionChanged', $event, selected.split(SEPARATOR)[0], selected.split(SEPARATOR)[1])">
    <option v-for="option in generatedSelectOptions" :value="option" :key="option">
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
      <th>Tokens</th>
    </tr>
    <tr class="selectable-row" v-for="match in matches[selectedSplit[0]][selectedSplit[1]]"
        :key="match.start_in_first + match.end_in_first"
        @click="this.$emit('matchSelected', $event, match.start_in_first, match.end_in_first, match.start_in_second, match.end_in_second)"
        :style="{background : match.color}"
    >
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
    const SEPARATOR = " - "

    const generatedSelectOptions = ref([])
    Object.keys(props.matches).forEach( (key) => {
      Object.keys(props.matches[key]).forEach( (subkey) => {
        generatedSelectOptions.value.push(key + SEPARATOR + subkey)
      })
    })

    let selected = ref(generatedSelectOptions.value[0]);
    let selectedSplit = ref(selected.value.split(SEPARATOR))


    watchEffect(() => {
      selectedSplit.value = selected.value.split(SEPARATOR)
    })

    return {
      selected,
      selectedSplit,
      generatedSelectOptions,
      SEPARATOR
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

option {
  text-align: center;
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
  border-radius: 10px;
}

td {
  padding: 2% 0;
}

.selectable-row:hover {
  color: white;
  cursor: pointer;
}

</style>