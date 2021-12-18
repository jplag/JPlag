<template>
  <div class="code-panel-container">
    <div class="file-title">
      <p style="width: 90%">{{ title }}</p>
      <button style="width: 10%" class="collapse-button" @click="$emit('toggleCollapse')">
        <img v-if="collapse" src="../assets/keyboard_double_arrow_up_white_18dp.svg" alt="hide info">
        <img v-else src="../assets/keyboard_double_arrow_down_white_18dp.svg" alt="additional info">
      </button>
    </div>
    <div class="code-container" :class="{ hidden : !collapse }">
      <LineOfCode v-for="(line, index) in lines"
                :key="index"
                :color="coloringArray[index]"
                :line-number="index"
                :text="line"
                :id="panelId.concat(title).concat(index)"
                :match-link="linksArray[index]"
                :is-last="isLast[index]"
                :is-first="isFirst[index]"
                  @click="$emit('lineSelected', $event, fileIndex, linksArray[index] )"/>
    </div>
  </div>
</template>

<script>
import {ref, defineComponent, watchEffect} from "vue";
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
      type: String
    },
    collapse: {
      type: Boolean
    }
  },
  setup(props) {
    const isCollapsed = ref(false)
    const toggleIsCollapsed = () => isCollapsed.value = !isCollapsed.value


    const coloringArray = ref({})
    const linksArray = ref({})
    const isLast = ref({})
    const isFirst = ref({})
    props.matches.forEach(m => {
      for (let i = m.start; i <= m.end; i++) {
        coloringArray.value[i] = m.color
        linksArray.value[i] = m.link
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
      isCollapsed,
      coloringArray,
      linksArray,
      isFirst,
      isLast,
      toggleIsCollapsed
    }
  }
})
</script>

<style scoped>
.code-panel-container {
  display: flex;
  flex-direction: column;
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
  color: var(--on-primary-color-accent);
  font-weight: bold;
  font-size: large;
}
.code-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 1%;
  padding: 1%;
  height: 100vw;
  background: var(--background-color-accent);
  box-shadow: inset var(--shadow-color) 0 0 3px 1px;
  overflow: scroll;
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