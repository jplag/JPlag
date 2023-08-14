<template>
  <div class="text-sm font-bold">
    <div v-if="reportViewerVersion.isDevVersion()">
      You are using a development version of the JPlag Report Viewer.
    </div>

    <div v-else-if="newestVersion.compareTo(reportViewerVersion) > 0" class="text-left text-error">
      You are using an outdated version of the JPlag Report Viewer ({{
        reportViewerVersion.toString()
      }}).<br />
      Version {{ newestVersion.toString() }} is available on
      <a href="https://github.com/jplag/JPlag/releases/latest" class="underline text-link">GitHub</a
      >.
    </div>

    <div v-else>JPlag v.{{ reportViewerVersion.toString() }}</div>
  </div>
</template>

<script setup lang="ts">
import { Version } from '@/model/Version'
import versionJson from '@/version.json'
import { ref } from 'vue'
import { OverviewFactory } from '@/model/factories/OverviewFactory'

const reportViewerVersion: Version =
  versionJson['report_viewer_version'] !== undefined
    ? OverviewFactory.extractVersion(versionJson['report_viewer_version'])
    : new Version(-1, -1, -1)

const newestVersion = ref(new Version(-1, -1, -1))

fetch('https://api.github.com/repos/jplag/JPlag/releases/latest')
  .then((response) => response.json())
  .then((data) => {
    const versionString = data.tag_name
    // remove the 'v' from the version string and split it into an array
    const versionArray = versionString.substring(1).split('.')
    newestVersion.value = new Version(
      parseInt(versionArray[0]),
      parseInt(versionArray[1]),
      parseInt(versionArray[2])
    )
  })
</script>
