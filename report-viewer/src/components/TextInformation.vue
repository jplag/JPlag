<!--
  A container displaying simple text information. It may include additional information which is being displayed
  in a drop down container.
-->
<template>
  <div class="flex-auto">
    {{ anonymousLabel ? 'Hidden' : label }}: <i><slot></slot></i>
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
