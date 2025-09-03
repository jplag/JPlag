<template>
  <div class="flex h-screen text-center">
    <div class="w-screen">
      <div>
        <img
          v-if="uiStore().useDarkMode"
          class="mx-auto mt-32 h-auto w-60"
          src="../assets/jplag-light-transparent.png"
          alt="JPlag Logo"
        />
        <img
          v-else
          class="mx-auto mt-32 h-auto w-60"
          src="../assets/jplag-dark-transparent.png"
          alt="JPlag Logo"
        />
      </div>
      <ContainerComponent class="mx-auto mt-10 w-fit max-w-5xl space-y-5 p-5">
        <div class="space-y-2">
          <h3 class="text-2xl font-bold">
            You are trying to open a report created with an older version of JPlag
          </h3>
          <p class="text-xl">
            Your report was created with JPlag version {{ uploadedVersion.toString() }}. <br />
            The current version of JPlag is {{ reportViewerVersion.toString() }}. It supports
            reports starting from version {{ minimalReportVersion.toString() }}<br />
            <span v-if="requestedVersion !== null">You can still view the old report here:</span>
            <span v-else
              >Opening reports generated with version {{ uploadedVersion.toString() }} is not
              supported by this report viewer.</span
            >
          </p>
        </div>
        <a v-if="requestedVersion !== null" :href="buildOldVersionLink()">
          <InteractableComponent class="border-accent-dark! bg-accent/50! mx-auto mt-2">
            Open with old report viewer
          </InteractableComponent>
        </a>
        <RouterLink :to="{ name: 'FileUploadView' }">
          <InteractableComponent class="mx-auto mt-6"> Back to file upload </InteractableComponent>
        </RouterLink>
      </ContainerComponent>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ContainerComponent, InteractableComponent } from '@jplag/ui-components/base'
import { uiStore } from '@/stores/uiStore'
import { Version } from '@jplag/model'
import { reportViewerVersion, minimalReportVersion } from '../version/versions'
import { computed } from 'vue'
const props = defineProps({
  version: {
    type: String,
    required: true
  }
})

const uploadedVersion = computed(() => {
  if (!props.version.match(/^\d+\.\d+\.\d+$/)) {
    return Version.ERROR_VERSION
  }
  const parts = props.version.split('.').map(Number)
  return new Version(parts[0], parts[1], parts[2])
})

/**
 * @param minVersion is inclusive
 * @param maxVersion is inclusive
 */
interface VersionMapEntry {
  minVersion: Version
  maxVersion: Version
  pathName: string
}

const versionMap: VersionMapEntry[] = [
  {
    minVersion: new Version(4, 2, 0),
    maxVersion: new Version(5, 1, 0),
    pathName: 'v5'
  },
  {
    minVersion: new Version(6, 0, 0),
    maxVersion: new Version(6, 1, 0),
    pathName: 'v6_1'
  }
]

const requestedVersion = computed(() => {
  for (const entry of versionMap) {
    if (
      uploadedVersion.value.compareTo(entry.minVersion) >= 0 &&
      uploadedVersion.value.compareTo(entry.maxVersion) <= 0
    ) {
      return entry
    }
  }
  return null
})

function buildOldVersionLink() {
  return `${window.location.origin}/${requestedVersion.value?.pathName}/`
}
</script>
