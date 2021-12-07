<template>
  <div class="line-wrap" :style="{background : color}">
<pre :id="text" v-bind:class="{ blurred : isBlurred }" >{{ lineNumber }} {{ text }}</pre>
  </div>
</template>

<script>
import { defineComponent, onMounted } from "vue";

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
    color: {
      type: String,
      required:true
    },
    startOfMatch: {
      type: Number
    }
  },
  setup(props) {
    document.getEleme
    return {

    }
  }
})
</script>

<style scoped>
pre {
  margin: 0;
  padding: 0;
  float: left;

}
.blurred {
  filter: blur(2px);
  -webkit-filter: blur(2px);
}
.line-wrap {
  width: 200%;
}


</style>