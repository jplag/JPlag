<template>
  <div class="relative inline-block group">
    <slot></slot>
    <div
      class="absolute hidden group-hover:block bg-black bg-opacity-60 px-1 rounded-md text-white text-center z-10"
      :style="tooltipPosition"
    >
      <slot name="tooltip"></slot>
      <div class="border-4 border-solid absolute" :style="arrowStyle"><!-- Arrow --></div>
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

  return style
})

const arrowStyle = computed(() => {
  const style: StyleValue = {}
  style.content = ' '

  function getBorderColor(current: String, other: String) {
    return other == current ? 'rgba(0,0,0,0.6)' : 'transparent'
  }

  style.borderColor = ''
  for (const dir of ['top', 'right', 'bottom', 'left']) {
    style.borderColor += getBorderColor(dir, props.direction) + ' '
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
