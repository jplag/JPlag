<template>
  <!-- eslint-disable-next-line vue/no-v-html -->
  <span class="aspect-square" v-html="svgMap[metric]"></span>
</template>

<script setup lang="ts">
import { MetricJsonIdentifier } from '@jplag/model'
import type { PropType } from 'vue'
import avgSvg from '../assets/AVG.svg?raw'
import maxSvg from '../assets/MAX.svg?raw'
import longSvg from '../assets/LONG.svg?raw'
import lenSvg from '../assets/LEN.svg?raw'

defineProps({
  metric: {
    type: String as PropType<MetricJsonIdentifier>,
    required: true
  }
})

const svgMap: Record<MetricJsonIdentifier, string> = {
  [MetricJsonIdentifier.AVERAGE_SIMILARITY]: avgSvg,
  [MetricJsonIdentifier.MAXIMUM_SIMILARITY]: maxSvg,
  [MetricJsonIdentifier.LONGEST_MATCH]: longSvg,
  [MetricJsonIdentifier.MAXIMUM_LENGTH]: lenSvg
}
</script>

<style scoped>
span {
  display: flex;
  overflow: hidden;
}

svg {
  max-height: 100%;
  height: 100%;
}
</style>
