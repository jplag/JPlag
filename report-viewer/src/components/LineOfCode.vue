<template>
<pre id="line" v-bind:class="{ blurred : isBlurred }">{{ lineNumber }} {{ text }}</pre>
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent({
  name: "LineOfCode",
  props: {
    text: {
      type: String,
      required: true
    },
    lineNumber: {
      type: Number,
      required: true
    },
    isBlurred: {
      type: Boolean,
      required: true
    },
  },
  setup(props) {
    return {

    }
  }
})
</script>

<style scoped>
pre {
  margin: 0;
  padding: 0;
  font-size: large;
}
.blurred {
  filter: blur(2px);
  -webkit-filter: blur(2px);
}

</style>