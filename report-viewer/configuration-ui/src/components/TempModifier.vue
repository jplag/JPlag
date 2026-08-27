<template>
  <ContainerComponent>
    <input
      v-model="m"
      class="border-interactable-border bg-interactable rounded border px-2 py-0 shadow-none!"
    />
  </ContainerComponent>
</template>

<script setup lang="ts">
import { ContainerComponent } from '@jplag/ui-components/base'

const m = defineModel<string>()
</script>
