<template>
  <div class="line-wrap" :style="{background : color}" :class="{'first-line' : isFirst, 'last-line' : isLast}">
    <pre :id="text" v-bind:class="{ 'match-line' : color !== '#ECECEC' }">{{ lineNumber }} {{ text }}</pre>
  </div>
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
  margin: 0;
  padding: 0;
  float: left;
  font-family: "JetBrains Mono",serif;
  font-size: x-small;
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