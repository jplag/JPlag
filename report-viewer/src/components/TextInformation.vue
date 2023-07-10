<!--
  A container displaying simple text information
-->
<template>
  <div class="flex-auto">
    {{ anonymousLabel ? 'Hidden' : label }}:
    <i v-if="!anonymousSlot"><slot></slot></i>
    <i v-else>Hidden</i>
  </div>
</template>

<script setup lang="ts">
defineProps({
  label: {
    type: String,
    required: true
  },
  /**
   * Indicates whether the value should be hidden.
   */
  anonymousSlot: {
    type: Boolean,
    required: false,
    default: false
  },
  anonymousLabel: {
    type: Boolean,
    required: false,
    default: false
  }
})
</script>
