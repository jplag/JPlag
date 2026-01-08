<template>
  <div
    :class="{
      'space-x-2 pb-1 text-xs text-black dark:text-white print:hidden': overrideStyle
    }"
  >
    <span
      v-if="!reportViewerVersion.isDevVersion() && !reportViewerVersion.isInvalid() && showVersion"
      >JPlag v{{ reportViewerVersion.toString() }}</span
    >
    <span v-else-if="reportViewerVersion.isDevVersion() && showVersion && commitHash !== undefined">
      <a
        class="text-link-dark dark:text-link underline"
        target="_blank"
        :href="`https://github.com/jplag/JPlag/commit/${commitHash}`"
        >{{ commitHash }}</a
      >
    </span>
    <span>
      JPlag is open source. Bug reports and feature requests can be submitted on
      <a
        href="https://github.com/jplag/JPlag/issues"
        class="text-link-dark dark:text-link underline"
        ><FontAwesomeIcon :icon="faGithub" /> GitHub</a
      >
    </span>
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faGithub } from '@fortawesome/free-brands-svg-icons'
import { commitHash, reportViewerVersion } from '../version/versions'

defineProps({
  overrideStyle: {
    type: Boolean,
    required: false,
    default: true
  },
  showVersion: {
    type: Boolean,
    required: false,
    default: true
  }
})
</script>
