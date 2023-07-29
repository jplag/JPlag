<template>
  <Interactable
    class="mr-2 px-[12px] w-fit !rounded-2xl flex justify-center items-center text-center box-border h-6 hover:!border-[2px] hover:px-[11px]"
    :class="{ '!bg-accent !border-accent-dark !bg-opacity-40': selected }"
    @click="$emit('click')"
  >
    {{ label }}
  </Interactable>
</template>

<script setup lang="ts">
import Interactable from '../InteractableComponent.vue'

defineProps({
  label: {
    type: String,
    required: true
  },
  selected: {
    type: Boolean,
    required: true
  }
})

defineEmits(['click'])
</script>
