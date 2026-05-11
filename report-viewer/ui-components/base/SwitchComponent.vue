<template>
  <label class="flex cursor-pointer items-center gap-1 select-none">
    <span><slot /></span>
    <input v-model="modelValue" type="checkbox" class="h-0 w-0 opacity-0" />
    <span
      class="slider bg-background-light dark:bg-background-dark before:bg-accent-dark dark:before:bg-interactable-dark border-interactable-border-light dark:border-interactable-border-dark border"
      :class="{ 'before:bg-accent!': modelValue }"
    ></span>
  </label>
</template>

<script setup lang="ts">
const modelValue = defineModel({
  type: Boolean,
  required: true
})
</script>

<style scoped>
.slider {
  position: relative;
  display: inline-block;
  width: 36px;
  height: 20px;
  border-radius: 9999px;
}

.slider::before {
  position: absolute;
  content: '';
  height: 14px;
  width: 14px;
  left: 2px;
  bottom: 2px;
  -webkit-transition: 0.2s;
  transition: 0.2s;
  border-radius: 9999px;
  box-sizing: border-box;
}

input:checked + .slider::before {
  --shift: 16px;
  -webkit-transform: translateX(var(--shift));
  -ms-transform: translateX(var(--shift));
  transform: translateX(var(--shift));
}
</style>
