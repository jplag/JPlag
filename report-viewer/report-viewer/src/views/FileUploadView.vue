<!--
  Starting view of the application. Presents the options for loading a JPlag report.
-->
<template>
  <div
    class="flex h-screen items-center text-center"
    @dragover.prevent
    @drop.prevent="uploadFileOnDrag"
  >
    <div class="w-screen">
      <div>
        <img
          v-if="uiStore().useDarkMode"
          class="mx-auto mt-8 h-auto w-60"
          height="168"
          width="240"
          src="@/assets/jplag-light-transparent.png"
          alt="JPlag Logo"
        />
        <img
          v-else
          class="mx-auto mt-8 h-auto w-60"
          height="168"
          width="240"
          src="@/assets/jplag-dark-transparent.png"
          alt="JPlag Logo"
        />
      </div>
      <h1 class="text-7xl">JPlag Report Viewer</h1>
      <div v-if="!loadingFiles && !exampleFiles">
        <div
          class="border-accent-dark bg-accent/25 mx-auto mt-10 flex w-96 cursor-pointer flex-col justify-center rounded-md border px-5 py-5"
          @click="uploadFileThroughWindow()"
        >
          <div>Drag and Drop report file on this page</div>
          <div>Or click here to select a file</div>
        </div>
        <div>(No files will be uploaded)</div>
        <a
          href="https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag"
          target="_blank"
          class="text-link-dark dark:text-link underline"
        >
          How to use JPlag
        </a>
      </div>
      <LoadingCircle v-else-if="loadingFiles || exampleFiles" class="space-y-5 pt-5" />
      <div v-if="errors.length > 0" class="text-error">
        <p>{{ getErrorText() }}</p>
        <p>For more details check the console.</p>
      </div>
    </div>
    <VersionInfoComponent class="absolute bottom-5 left-5" />
  </div>
</template>

<script setup lang="ts">
import { onErrorCaptured, ref, type Ref } from 'vue'
import { router } from '@/router'
import VersionInfoComponent from '../components/VersionInfoComponent.vue'
import { reportStore } from '@/stores/reportStore'
import { canLoadFile } from '@/stores/fileLoading'
import { uiStore } from '@/stores/uiStore'
import { ReportFileHandler } from '@jplag/parser'
import { LoadingCircle } from '@jplag/ui-components/base'

reportStore().reset()

const exampleFiles = ref(import.meta.env.MODE == 'demo' || import.meta.env.MODE == 'dev-demo')

canLoadFile().then((value) => {
  if (value) {
    navigateToOverview()
  }
})

document.title = 'JPlag Report Viewer'

const loadingFiles = ref(false)
type fileMethod = 'query' | 'upload' | 'unknown'
const errors: Ref<{ error: Error; source: fileMethod }[]> = ref([])

function navigateToOverview() {
  router.push({
    name: 'OverviewView'
  })
}

/**
 * Handles a file on drop. It determines the file type and passes it to the corresponding handler.
 * @param file File to handle
 */
async function handleFile(file: Blob, fileName: string) {
  loadingFiles.value = true
  // empty case is for .jplag files
  switch (file.type) {
    case 'application/zip':
    case 'application/zip-compressed':
    case 'application/x-zip-compressed':
    case 'application/x-zip':
    case '': {
      const report = await new ReportFileHandler().extractContent(file)
      const r = reportStore().loadReport(report.files, report.submissionFiles, fileName)
      if (r) {
        return navigateToOverview()
      }
      break
    }
    default:
      throw new Error(`Unknown MIME type '${file.type}'`)
  }
}

/**
 * Handles file drop.
 * @param e Drag event of file drop
 */
async function uploadFileOnDrag(e: DragEvent) {
  let dropped = e.dataTransfer?.files
  try {
    if (dropped?.length === 1) {
      await handleFile(dropped[0], dropped[0].name)
    } else {
      throw new Error('Not exactly one file')
    }
  } catch (e) {
    registerError(e as Error, 'upload')
  }
}

async function uploadFileThroughWindow() {
  let input = document.createElement('input')
  input.type = 'file'
  input.accept = '.jplag,.zip'
  input.multiple = false
  input.onchange = () => {
    const files = input.files
    if (!files) {
      return
    }
    const file = files.item(0)
    if (!file) {
      return
    }
    handleFile(file, file.name)
  }
  input.click()
}

function registerError(error: Error, source: fileMethod) {
  loadingFiles.value = false
  errors.value.push({ error, source })
  console.error(error)
}

function getErrorText() {
  function getSourceText(source: fileMethod) {
    if (source == 'unknown') {
      return 'Error:'
    }
    const longNames = {
      query: 'querying files',
      upload: 'loading files'
    }
    return 'Error during ' + longNames[source]
  }

  return errors.value.map((e) => `${getSourceText(e.source)}: ${e.error.message}`).join('\n')
}

onErrorCaptured((error) => {
  registerError(error, 'unknown')
  return false
})

if (exampleFiles.value) {
  navigateToOverview()
}
</script>
