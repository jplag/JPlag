<template>
  <div class="code-container">
    <LineOfCode v-for="(line, index) in lines" :key="index" :is-blurred="isBlurred(index)" :line-number="index" :text="line"
    :id="panelId.concat(index)"/>
  </div>
</template>

<script>
import {computed, defineComponent} from "vue";
import LineOfCode from "./LineOfCode";

export default defineComponent({
  name: "CodePanel",
  components: {LineOfCode},
  props: {
    lines: {
      type: Array,
      required: true
    },
    notBlurred: {
      type: Array
    },
    panelId: {
      type: String
    }
  },
  setup(props) {
    const isBlurred = (index) => {
      return !props.notBlurred.includes(index)
    }
    return {
      isBlurred
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