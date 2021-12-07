<template>
  <div class="code-container">
    <LineOfCode v-for="(line, index) in lines" :key="index" :color="coloringArray[index]" :line-number="index" :text="line"
    :id="panelId.concat(index)"/>
  </div>
</template>

<script>
import {ref, defineComponent, watchEffect} from "vue";
import LineOfCode from "./LineOfCode";

export default defineComponent({
  name: "CodePanel",
  components: {LineOfCode},
  props: {
    lines: {
      type: Array,
      required: true
    },
    coloring: {
      type: Array
    },
    panelId: {
      type: String
    }
  },
  setup(props) {
    console.log(JSON.stringify(props.coloring))
    const coloringArray = ref({})
    props.coloring.forEach(m => {
      for (let i = m.start; i <= m.end; i++) {
        coloringArray.value[i] = m.color
      }
    })
    for(let i = 0; i < props.lines.length; i++) {
      if(!coloringArray.value[i]) {
        coloringArray.value[i] = "#ECECEC"
      }
    }
    watchEffect(() => {
      coloringArray.value = {}
      props.coloring.forEach(m => {
        for (let i = m.start; i <= m.end; i++) {
          coloringArray.value[i] = m.color
        }
      })
      for(let i = 0; i < props.lines.length; i++) {
        if(!coloringArray.value[i]) {
          coloringArray.value[i] = "#ECECEC"
        }
      }
    })
    return {
      coloringArray
    }
  }
})
</script>

<style scoped>
.code-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  overflow: scroll;
}
</style>