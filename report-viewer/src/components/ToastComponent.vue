<template>
  <div
    v-if="timePassed < timeToLive && visible"
    class="absolute bottom-5 left-5 max-w-96 rounded-md border-2 border-accent-dark bg-container-light dark:bg-container-dark"
  >
    <div class="flex">
      <div class="flex-grow p-2">
        <slot></slot>
      </div>
      <div class="cursor-pointer p-1" @click="visible = false">
        <FontAwesomeIcon :icon="faTimes" />
      </div>
    </div>
    <div
      class="h-1 bg-accent"
      :style="{ width: `${100 - (timePassed / timeToLive) * 100}%` }"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { ref } from 'vue'
import { faTimes } from '@fortawesome/free-solid-svg-icons'

const props = defineProps({
  timeToLive: {
    type: Number,
    required: false,
    default: 5000
  }
})

const timePassed = ref(0)
const visible = ref(true)

const intervalTime = 20

const intervalID = setInterval(() => {
  timePassed.value += intervalTime
  if (timePassed.value >= props.timeToLive) {
    clearInterval(intervalID)
  }
}, intervalTime)
</script>
