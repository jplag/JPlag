<template>
  <div class="line-wrap" :style="{background : color}" :class="{'first-line' : isFirst, 'last-line' : isLast, 'visible' : visible}">
    <pre ref="lineRef" :id="text" class="java" :class="{ 'match-line' : color !== '#ECECEC' }">{{ lineNumber }} {{ text }}</pre>
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
      required:true
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
    onUpdated( () => {
      if ( props.visible && !highlighted ) {
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