<template>
  <label class="switch">
    <input v-model="value" type="checkbox" class="h-0 w-0 opacity-0" />
    <span
      class="slider bg-interactable-light dark:bg-interactable-dark before:bg-container-light dark:before:bg-container-dark border-interactable-border-light dark:border-interactable-border-dark border"
      :class="{ 'before:bg-accent!': value }"
    ></span>
  </label>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const value = computed({
  get: () => _value.value ?? props.modelValue,
  set: (val) => {
    _value.value = val
    emit('update:modelValue', val)
  }
})

const _value = ref<boolean | undefined>(undefined)

watch(
  () => props.modelValue,
  (val) => {
    _value.value = val
  }
)
</script>

<style scoped>
/* Modified from: https://www.w3schools.com/howto/howto_css_switch.asp */
/* The switch - the box around the slider */
.switch {
  position: relative;
  display: inline-block;
  width: 2.2em;
  height: 1.2em;
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  -webkit-transition: 0.4s;
  transition: 0.4s;
  border-radius: 10em;
}

.slider:before {
  position: absolute;
  content: '';
  height: 0.85em;
  width: 0.85em;
  left: 0.1em;
  bottom: 0.1em;
  -webkit-transition: 0.2s;
  transition: 0.2s;
  border-radius: 50%;
}

input:checked + .slider:before {
  -webkit-transform: translateX(1em);
  -ms-transform: translateX(1em);
  transform: translateX(1em);
}
</style>
