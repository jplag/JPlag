<template>
  <div class="line-wrap" :style="{background : color}" :class="{'first-line' : isFirst, 'last-line' : isLast}" @click="linkMatch">
    <pre :id="text" v-bind:class="{ blurred : isBlurred, 'match-line' : color !== '#ECECEC' }">{{ lineNumber }} {{ text }}</pre>
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
    },
    matchLink: {
      type: String
    },
    isFirst: {
      type: Boolean,
    },
    isLast: {
      type: Boolean,
    }
  },
  setup(props) {
    const linkMatch = () => {
      if(props.matchLink !== "-1") {
        document.getElementById(props.matchLink).scrollIntoView()
      }
    }
    return {
      linkMatch
    }
  }
})
</script>

<style scoped>
pre {
  margin: 0;
  padding: 0;
  float: left;
  font-family: "JetBrains Mono";
  font-size: x-small;
}
.blurred {
  filter: blur(2px);
  -webkit-filter: blur(2px);
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