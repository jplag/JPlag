<template>
  <div class="text-xs text-black dark:text-white print:hidden">
    Bug reports and feature requests can be submitted on
    <a href="https://github.com/jplag/JPlag/issues" class="text-link-dark underline dark:text-link"
      ><FontAwesomeIcon :icon="faGithub" /> GitHub</a
    >
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faGithub } from '@fortawesome/free-brands-svg-icons'
</script>
