<template>
  <ContainerComponent>
    <h2 class="text-lg font-bold"><slot name="heading" /></h2>

    <div v-if="showContent" class="mt-3 grid grid-cols-1 gap-2 md:grid-cols-[auto_1fr]">
      <slot />
    </div>
  </ContainerComponent>
</template>

<script setup lang="ts">
import ContainerComponent from './ContainerComponent.vue'

defineProps({
  showContent: {
    type: Boolean,
    required: false,
    default: true
  }
})
</script>
