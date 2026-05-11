<!--
  Offers a un-styled scrollable container
-->
<template>
  <div
    ref="root"
    class="overflow-y-visible md:overflow-y-auto print:overflow-y-visible"
    @scroll="updateScrollOffset()"
  >
    <div class="md:max-h-0 md:min-h-full print:max-h-none print:min-h-fit">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, type Ref } from 'vue'

const root: Ref<HTMLElement | null> = ref(null)

const scrollOffset = defineModel('scrollOffset', {
  type: Number,
  required: false,
  default: 0
})

function updateScrollOffset() {
  if (root.value) {
    scrollOffset.value = root.value.scrollTop
  }
}

function getRoot() {
  if (!root.value) {
    throw new Error('Root element is not available')
  }
  return root.value
}

defineExpose({
  getRoot
})
</script>
