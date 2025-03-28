<!--
  Offers a un-styled scrollable container
-->
<template>
  <div ref="root" class="overflow-y-auto print:overflow-y-visible">
    <div class="max-h-0 min-h-full print:max-h-none">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, type Ref } from 'vue'

const root: Ref<HTMLElement | null> = ref(null)

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
