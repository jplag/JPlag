<template>
  <Interactable
    class="print-exact mr-2 box-border flex h-6 w-fit items-center justify-center rounded-2xl! px-[12px] text-center whitespace-nowrap hover:border-[2px]! hover:px-[11px] print:border-2"
    :class="{ 'border-accent-dark! bg-accent/40!': selected }"
    @click="$emit('click')"
  >
    {{ label }}
    <InfoIcon v-if="hasToolTip" class="text-[0.5rem]" />
  </Interactable>
</template>

<script setup lang="ts">
import InfoIcon from '../InfoIcon.vue'
import Interactable from '../InteractableComponent.vue'

defineProps({
  label: {
    type: String,
    required: true
  },
  selected: {
    type: Boolean,
    required: false,
    default: false
  },
  hasToolTip: {
    type: Boolean,
    required: false,
    default: false
  }
})

defineEmits(['click'])
</script>
