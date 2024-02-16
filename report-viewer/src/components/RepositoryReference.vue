<template>
  <div
    :class="{
      'absolute bottom-1 left-5 text-xs text-black dark:text-white print:hidden': overrideStyle
    }"
  >
    JPlag is open source. Bug reports and feature requests can be submitted on
    <a href="https://github.com/jplag/JPlag/issues" class="text-link-dark underline dark:text-link"
      ><FontAwesomeIcon :icon="faGithub" /> GitHub</a
    >
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faGithub } from '@fortawesome/free-brands-svg-icons'

defineProps({
  overrideStyle: {
    type: Boolean,
    required: false,
    default: true
  }
})
</script>
