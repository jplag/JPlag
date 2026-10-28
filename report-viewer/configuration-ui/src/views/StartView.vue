<template>
  <div class="flex w-full pt-[20vh] justify-center align-middle h-full">
    <div class="grid grid-cols-2 grid-rows-[1fr_auto] gap-5 h-fit">
      <img
        class="col-span-2 mx-auto mt-8 h-auto w-60"
        height="168"
        width="240"
        src="../assets/jplag-dark-transparent.png"
        alt="JPlag Logo"
      />
      <button @click="emit('start')">
        <InteractableComponent class="border-accent-dark! bg-accent-light/25!"
          >Start</InteractableComponent
        >
      </button>

      <button @click="emit('start')">
        <InteractableComponent>Open previous configuration</InteractableComponent>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { InteractableComponent } from '@jplag/ui-components/base'

const emit = defineEmits(['start'])
</script>
