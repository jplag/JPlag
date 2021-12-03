<template>
  <div class="code-container">
    <LineOfCode v-for="(line, index) in lines" :key="line" :is-blurred="notBlurred.includes(index)" :line-number="index" :text="line"/>
  </div>
</template>

<script>
import { defineComponent } from "vue";
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
  },
  setup(props) {
    return {

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