<!--
  Container which display a single line of code of a file.
-->
<template>
  <div :class="{'first-line' : isFirst, 'last-line' : isLast, 'visible' : visible}" :style="{background : color}"
       class="line-wrap">
    <pre :id="text" ref="lineRef" :class="{ 'match-line' : color !== '#ECECEC' }" class="java">{{ lineNumber }} {{
        text
      }}</pre>
  </div>
</template>

<script>
import {defineComponent, onUpdated, ref} from "vue";
import hljs from 'highlight.js';

export default defineComponent({
  name: "LineOfCode",
  props: {
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
      required: true
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

  setup(props) {
    let highlighted = false
    let lineRef = ref(null)
    //Trigger highlighting when code panel is collapsed.
    onUpdated(() => {
      if (props.visible && !highlighted) {
        hljs.highlightElement(lineRef.value)
        highlighted = true
      }
    })
    return {
      lineRef
    }
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

pre code.hljs {
  padding: 0 !important;
}

code.hljs {
  padding: 0 !important;
}

.hljs {
  background: transparent !important;
  font-family: "JetBrains Mono NL", serif !important;
  font-weight: bold;    font-size: x-small !important;
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