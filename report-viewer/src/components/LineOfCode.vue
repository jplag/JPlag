<template>
  <div class="line-wrap" :style="{background : color}" :class="{'first-line' : isFirst, 'last-line' : isLast}">
      <hljs class="hljs" v-bind:class="{ 'match-line' : color !== '#ECECEC' }" language="java" :code="lineNumber.toString().concat(' ').concat(text)"/>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import hljsVuePlugin from "@highlightjs/vue-plugin";

export default defineComponent({
  name: "LineOfCode",
  components: { hljs: hljsVuePlugin.component},
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
      required:true
    },
    startOfMatch: {
      type: Number
    },
    fileIndex: {
      type: Number,
    },
    isFirst: {
      type: Boolean,
    },
    isLast: {
      type: Boolean,
    }
  },

  setup() {
    return {}
  }
})
</script>

<style scoped>
pre {
  /* In hljs pre tag is used. We need this style to override attributes in the styles of higlightjs to show code more
   compact */
  margin: 0;
  padding: 0;
  float: left;
}
.first-line {
  margin-top: 2%;
}
.last-line {
  margin-bottom: 2%;
  box-shadow: #777777 0 3px 3px;
}
.match-line {
}
.line-wrap {
  width: 200%;
}

</style>