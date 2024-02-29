<template>
  <div class="text-left text-sm font-bold">
    <div v-if="!isDemo">
      <div v-if="reportViewerVersion.isInvalid()">Could not load version information</div>
      <div v-else-if="reportViewerVersion.isDevVersion()">
        You are using a development version of the JPlag Report Viewer.
      </div>

      <div v-else>
        <div v-if="newestVersion.compareTo(reportViewerVersion) > 0" class="text-left text-error">
          You are using an outdated version of the JPlag Report Viewer ({{
            reportViewerVersion.toString()
          }}).<br />
          Version {{ newestVersion.toString() }} is available on
          <a href="https://github.com/jplag/JPlag/releases/latest" class="text-link underline"
            >GitHub</a
          >.
        </div>

        <div v-else>JPlag v{{ reportViewerVersion.toString() }}</div>

        <div v-if="!minimalReportVersion.isInvalid()">
          The minimal version of JPlag that is supported by the viewer is v{{
            minimalReportVersion.toString()
          }}.
        </div>
      </div>
    </div>

    <div v-else>
      <div v-if="reportViewerVersion.isInvalid()">Could not load version information</div>
      <div v-else>Demo of JPlag v{{ reportViewerVersion.toString() }}</div>
      <div>Displays the result of JPlag on the Progpedia dataset.</div>
    </div>
    <RepositoryReference :override-style="false" />
  </div>
</template>

<script setup lang="ts">
import { Version } from '@/model/Version'
import { ref } from 'vue'
import { minimalReportVersion, reportViewerVersion } from '@/model/Version'
import RepositoryReference from './RepositoryReference.vue'

const newestVersion = ref(new Version(-1, -1, -1))
const isDemo = import.meta.env.MODE == 'demo'

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
  .catch(() => {
    newestVersion.value = new Version(-1, -1, -1)
  })
</script>
