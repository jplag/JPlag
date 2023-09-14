<template>
  <div class="group relative inline-block">
    <slot></slot>
    <div
      class="delay-visible invisible absolute z-10 rounded-md px-1 text-center text-white group-hover:visible"
      :style="tooltipPosition"
    >
      <slot name="tooltip"></slot>
      <div class="absolute border-4 border-solid" :style="arrowStyle"><!-- Arrow --></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType, type StyleValue } from 'vue'

const props = defineProps({
  direction: {
    type: String as PropType<'top' | 'bottom' | 'left' | 'right'>,
    required: false,
    default: 'top'
  }
})

const opacity = 0.8

const tooltipPosition = computed(() => {
  const style: StyleValue = {}

  if (props.direction == 'left' || props.direction == 'right') {
    style.top = '50%'
    style.transform = 'translateY(-50%)'
    if (props.direction == 'left') {
      style.right = '105%'
    } else {
      style.left = '105%'
    }
  } else {
    style.left = '50%'
    style.transform = 'translateX(-50%)'
    if (props.direction == 'top') {
      style.bottom = '105%'
    } else {
      style.top = '105%'
    }
  }
  style.backgroundColor = `rgba(0,0,0,${opacity})`

  return style
})

const arrowStyle = computed(() => {
  const style: StyleValue = {}
  style.content = ' '

  style.borderColor = ''
  for (const dir of ['top', 'right', 'bottom', 'left']) {
    style.borderColor += dir == props.direction ? `rgba(0,0,0,${opacity}) ` : 'transparent '
  }

  if (props.direction == 'left' || props.direction == 'right') {
    style.top = '50%'
    style.marginTop = '-4px'
  } else {
    style.left = '50%'
    style.marginLeft = '-4px'
  }

  style[props.direction] = '100%'

  return style
})
</script>

<style scoped>
.delay-visible {
  transition-delay: 0s;
}

*:hover > .delay-visible {
  transition-delay: 0.25s;
}
</style>
