<template>
  <div class="code-panel-container" :id="panelId.toString().concat(title).concat(fileIndex.toString())">
    <div class="file-title">
      <p style="width: 90%">{{ title }}</p>
      <button style="width: 10%" class="collapse-button" @click="$emit('toggleCollapse')">
        <img v-if="collapse" src="../assets/keyboard_double_arrow_up_white_18dp.svg" alt="hide info">
        <img v-else src="../assets/keyboard_double_arrow_down_white_18dp.svg" alt="additional info">
      </button>
    </div>
    <div class="code-container" :class="{ hidden : !collapse }">
      <LineOfCode v-for="(line, index) in lines"
                  :visible="collapse"
                :key="index"
                :color="coloringArray[index]"
                :line-number="index"
                :text="line"
                :id="String(panelId).concat(title).concat(index)"
                :is-last="isLast[index]"
                :is-first="isFirst[index]"
                  @click="$emit('lineSelected', $event, linksArray[index].panel, linksArray[index].file, linksArray[index].line )"/>
    </div>
  </div>
</template>

<script>
import {ref, defineComponent} from "vue";
import LineOfCode from "./LineOfCode";

export default defineComponent({
  name: "CodePanel",
  components: {LineOfCode},
  props: {
    title: {
      type: String,
    },
    fileIndex: {
      type: Number
    },
    lines: {
      type: Array,
      required: true
    },
    matches: {
      type: Array
    },
    panelId: {
      type: Number
    },
    collapse: {
      type: Boolean
    }
  },
  setup(props) {
    const coloringArray = ref({})
    const linksArray = ref({})
    const isLast = ref({})
    const isFirst = ref({})
    props.matches.forEach(m => {
      for (let i = m.start; i <= m.end; i++) {
        coloringArray.value[i] = m.color
        linksArray.value[i] = { panel : m.linked_panel, file : m.linked_file, line : m.linked_line }
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
        coloringArray.value[i] = "#FFFFFF"
        linksArray.value[i] = "-1"
        isFirst.value[i] = false
        isLast.value[i] = false
      }
    }

    return {
      coloringArray,
      linksArray,
      isFirst,
      isLast,
    }
  }
})
</script>

<style scoped>
.code-panel-container {
  display: flex;
  flex-direction: column;
  margin-left: 1%;
  margin-right: 1%;
  margin-bottom: 3%;
  border-radius: 10px;
  box-shadow: var(--shadow-color) 2px 3px 3px;
  background: var(--primary-color-light);
}
.file-title {
  display: flex;
}

.file-title > p {
  text-align: center;
  color: var(--on-primary-color);
  font-weight: bold;
  font-size: large;
}
.code-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 1%;
  padding: 1%;
  background: var(--background-color);
  box-shadow: inset var(--shadow-color) 0 0 3px 1px;
  overflow: auto;
}

.collapse-button {
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  border: none;
}

.collapse-button:hover {
  background: var(--primary-color-dark);
  border-radius: 10px;
}

.hidden {
  display: none !important;
}
</style>