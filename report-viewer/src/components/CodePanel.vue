<template>
  <div class="code-container">
    <LineOfCode v-for="(line, index) in lines"
                :key="index"
                :color="coloringArray[index]"
                :line-number="index"
                :text="line"
                :id="panelId.concat(index)"
                :match-link="linksArray[index]"
                :is-last="isLast[index]"
                :is-first="isFirst[index]"/>
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
    const linksArray = ref({})
    const isLast = ref({})
    const isFirst = ref({})
    props.coloring.forEach(m => {
      for (let i = m.start; i <= m.end; i++) {
        coloringArray.value[i] = m.color
        linksArray.value[i] = props.panelId === "1" ? "2".concat(m.link) : "1".concat(m.link)
        if(i === m.start) {
          isFirst.value[i] = true
        }
        if(i === m.end) {
          isLast.value[i] = true
        }
      }
    })
    for(let i = 0; i < props.lines.length; i++) {
      if(!coloringArray.value[i]) {
        coloringArray.value[i] = "#ECECEC"
        linksArray.value[i] = "-1"
        isFirst.value[i] = false
        isLast.value[i] = false
      }
    }
    watchEffect(() => {
      coloringArray.value = {}
      props.coloring.forEach(m => {
        for (let i = m.start; i <= m.end; i++) {
          coloringArray.value[i] = m.color
          linksArray.value[i] = props.panelId === "1" ? "2".concat(m.link) : "1".concat(m.link)
          if(i === m.start) {
            isFirst.value[i] = true
          }
          if(i === m.end) {
            isLast.value[i] = true
          }
        }
      })
      for(let i = 0; i < props.lines.length; i++) {
        if(!coloringArray.value[i]) {
          coloringArray.value[i] = "#ECECEC"
          linksArray.value[i] = "-1"
          isFirst.value[i] = false
          isLast.value[i] = false
        }
      }
    })
    return {
      coloringArray,
      linksArray,
      isFirst,
      isLast
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