<!--
  Container which display a single line of code of a file.
-->
<template>
  <div :style="{ background: color }" class="flex-grow">
    <pre :id="text" ref="lineRef" class="java float-left m-0 p-0">{{ lineNumber }} {{ text }}</pre>
  </div>
</template>

<script setup lang="ts">
import { onUpdated, ref } from 'vue'
import hljs from 'highlight.js'

const props = defineProps({
  /**
   * Indicates whether the line is shown on screen. Used for highlighting on demand.
   */
  visible: {
    type: Boolean,
    required: true
  },
  text: {
    type: String,
    required: true
  },
  lineNumber: {
    type: Number,
    required: true
  },
  color: {
    required: true,
    type: String
  },
  fileIndex: {
    type: Number
  }
})

let highlighted = false
const lineRef = ref(null)

//Trigger highlighting when code panel is collapsed.
onUpdated(() => {
  if (props.visible && !highlighted && lineRef.value != null) {
    hljs.highlightElement(lineRef.value)
    highlighted = true
  }
})
</script>

<style scoped>
pre code.hljs {
  padding: 0 !important;
}

code.hljs {
  padding: 0 !important;
}

.hljs {
  background: transparent !important;
  font-family: 'JetBrains Mono NL', monospace !important;
  font-size: 0.75rem !important;
}
</style>
