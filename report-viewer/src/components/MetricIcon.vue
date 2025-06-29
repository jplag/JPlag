<template>
  <FontAwesomeIcon v-if="icon.type == 'FontAwesome'" :icon="icon.icon" />
  <svg
    v-else-if="icon.type == 'manual' && icon.name == 'average'"
    version="1.0"
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 448 512"
    preserveAspectRatio="xMidYMid meet"
  >
    <g
      transform="translate(0.000000,512.000000) scale(0.100000,-0.100000)"
      fill="currentColor"
      stroke="none"
    >
      <path
        d="M2966 4170 c-91 -11 -154 -27 -256 -63 -164 -57 -293 -131 -625 -362
-240 -166 -363 -240 -470 -281 -62 -25 -83 -28 -185 -28 -96 1 -130 5 -205 28
-104 31 -311 130 -505 240 -74 42 -159 84 -188 92 -128 38 -272 -31 -340 -161
-38 -74 -38 -178 1 -258 37 -74 81 -110 263 -214 561 -318 910 -396 1293 -287
177 50 339 144 816 470 215 147 331 205 449 224 173 27 396 -47 746 -249 69
-40 153 -82 188 -93 56 -19 71 -20 122 -10 77 14 118 34 167 81 61 59 87 121
88 211 0 62 -5 85 -27 130 -15 31 -40 68 -56 83 -83 80 -536 315 -726 376
-201 66 -395 90 -550 71z"
      />
      <path
        d="M2966 2270 c-91 -11 -154 -27 -256 -63 -164 -57 -293 -131 -625 -362
-240 -166 -363 -240 -470 -281 -62 -25 -83 -28 -185 -28 -96 1 -130 5 -205 28
-104 31 -311 130 -505 240 -74 42 -159 84 -188 92 -128 38 -272 -31 -340 -161
-38 -74 -38 -178 1 -258 37 -74 81 -110 263 -214 561 -318 910 -396 1293 -287
177 50 339 144 816 470 215 147 331 205 449 224 173 27 396 -47 746 -249 69
-40 153 -82 188 -93 56 -19 71 -20 122 -10 77 14 118 34 167 81 61 59 87 121
88 211 0 62 -5 85 -27 130 -15 31 -40 68 -56 83 -83 80 -536 315 -726 376
-201 66 -395 90 -550 71z"
      />
    </g>
  </svg>
</template>

<script setup lang="ts">
import type { MetricIcon } from '@/model/MetricType'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import type { PropType } from 'vue'

defineProps({
  icon: {
    type: Object as PropType<MetricIcon>,
    required: true
  }
})
</script>

<style scoped>
svg {
  height: 1em;
}
</style>
