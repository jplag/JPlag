<template>
  <label class="switch">
    <input v-model="value" type="checkbox" class="h-0 w-0 opacity-0" />
    <span
      class="slider bg-background-light dark:bg-background-dark before:bg-accent-dark dark:before:bg-interactable-dark border-interactable-border-light dark:border-interactable-border-dark border-1"
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
  width: 30px;
  height: 17px;
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
  -webkit-transition: 0.2s;
  transition: 0.2s;
  border-radius: 9999px;
  box-sizing: border-box;
}

.slider:before {
  position: absolute;
  content: '';
  height: 12px;
  width: 12px;
  left: 2px;
  bottom: 2px;
  -webkit-transition: 0.2s;
  transition: 0.2s;
  border-radius: 50%;
}

input:checked + .slider:before {
  --shift: 13px;
  -webkit-transform: translateX(var(--shift));
  -ms-transform: translateX(var(--shift));
  transform: translateX(var(--shift));
}
</style>
