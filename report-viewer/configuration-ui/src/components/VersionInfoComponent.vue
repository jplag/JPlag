<template>
  <div v-if="!isDemo">
    <div v-if="reportViewerVersion.isInvalid()">Could not load version information</div>
    <div v-else-if="reportViewerVersion.isDevVersion()">
      Dev
      <span v-if="commitHash">
        (
        <a
          class="text-link underline"
          :href="`https://github.com/jplag/JPlag/commit/${commitHash}`"
          target="_blank"
          >{{ commitHash.substring(0, 7) }}</a
        >)
      </span>
    </div>

    <div v-else>
      JPlag v{{ reportViewerVersion.toString() }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { reportViewerVersion, commitHash } from '@jplag/report-viewer'

const isDemo = import.meta.env.MODE == 'demo'
</script>
