<template>
  <div class="group pointer-events-none inline">
    <div ref="contentRef" class="pointer-events-auto flex items-center gap-x-1">
      <slot></slot>
      <InfoIcon v-if="showInfoSymbol && $slots.tooltip" class="ml-0!" />
    </div>
    <span
      v-if="$slots.tooltip"
      ref="tooltipRef"
      class="invisible absolute box-border delay-0 group-hover:visible group-hover:delay-200"
    >
      <span
        class="arrowBase bg-tooltip pointer-events-auto relative z-10 block rounded-md px-1 text-center text-white after:absolute after:border-4 after:border-solid after:border-transparent"
        :style="tooltipPosition"
        :class="{
          'after:top-1/2 after:-mt-1': props.direction == 'left' || props.direction == 'right',
          'after:left-1/2! after:-ml-1': props.direction == 'top' || props.direction == 'bottom',
          'after:border-t-tooltip! after:top-full': props.direction == 'top',
          'after:border-b-tooltip! after:bottom-full': props.direction == 'bottom',
          'after:border-l-tooltip! after:left-full': props.direction == 'left',
          'after:border-r-tooltip! after:right-full': props.direction == 'right'
        }"
      >
        <slot name="tooltip"></slot>
      </span>
    </span>
  </div>
</template>

<script setup lang="ts">
import type { ToolTipDirection } from './ToolTip'
import { computed, ref, type PropType, type Ref, type StyleValue } from 'vue'
import InfoIcon from './InfoIcon.vue'

const props = defineProps({
  direction: {
    type: String as PropType<ToolTipDirection>,
    required: false,
    default: 'top'
  },
  /** Sometimes the absolute div is centered horizontally on the content. Set this to true if that is the case. */
  toolTipContainerWillBeCentered: {
    type: Boolean,
    required: false,
    default: false
  },
  showInfoSymbol: {
    type: Boolean,
    required: false,
    default: true
  },
  /** Can be set if the tooltip is inside a scrollable container */
  scrollOffsetX: {
    type: Number,
    required: false,
    default: 0
  },
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  }
})

const contentRef: Ref<HTMLElement | null> = ref(null)
const tooltipRef: Ref<HTMLElement | null> = ref(null)
const arrowOffset = 4

const tooltipPosition = computed(() => {
  const style: StyleValue = {}
  const contentDiv = contentRef.value
  const tooltipDiv = tooltipRef.value
  if (!contentDiv || !tooltipDiv) {
    return style
  }
  // zeros the tooltip on the top-left of the content
  let top = -contentDiv.offsetHeight - props.scrollOffsetY
  let left =
    (props.toolTipContainerWillBeCentered ? -contentDiv.offsetWidth / 2 : 0) - props.scrollOffsetX
  if (props.direction == 'right' || props.direction == 'left') {
    top += (contentDiv.offsetHeight - tooltipDiv.offsetHeight) / 2
  } else {
    left -= (tooltipDiv.offsetWidth - contentDiv.offsetWidth) / 2
  }

  if (props.direction == 'right') {
    left += contentDiv.offsetWidth + arrowOffset
  } else if (props.direction == 'left') {
    left -= tooltipDiv.offsetWidth + arrowOffset
  } else if (props.direction == 'bottom') {
    top += contentDiv.offsetHeight + arrowOffset
  } else {
    top -= tooltipDiv.offsetHeight + arrowOffset
  }

  style.top = top + 'px'
  style.left = left + 'px'
  return style
})
</script>

<style scoped>
.arrowBase::after {
  content: ' ';
}
</style>
