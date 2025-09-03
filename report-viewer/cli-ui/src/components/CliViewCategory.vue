<template>
  <ContainerComponent>
    <h2 class="text-lg font-bold"><slot name="heading" /></h2>

    <div v-if="showContent" class="mt-3 grid grid-cols-[auto_auto_1fr] gap-2">
      <slot />
    </div>
  </ContainerComponent>
</template>

<script setup lang="ts">
import { ContainerComponent } from '@jplag/ui-components/base'

defineProps({
  showContent: {
    type: Boolean,
    required: false,
    default: true
  }
})
</script>
