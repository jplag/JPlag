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
          v-if="store().uiState.useDarkMode"
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
      <div v-if="!hasQueryFile && !loadingFiles && !exampleFiles">
        <div
          class="mx-auto mt-10 flex w-96 cursor-pointer flex-col justify-center rounded-md border-1 border-accent-dark bg-accent bg-opacity-25 px-5 py-5"
          @click="uploadFileThroughWindow()"
        >
          <div>Drag and Drop zip file on this page</div>
          <div>Or click here to select a file</div>
        </div>
        <div>(No files will be uploaded)</div>
        <Button v-if="localFiles" class="mx-auto mt-8 w-fit" @click="continueWithLocal">
          Continue with local files
        </Button>
        <a
          href="https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag"
          target="_blank"
          class="text-link-dark underline dark:text-link"
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
import { useRoute } from 'vue-router'
import { router } from '@/router'
import { store } from '@/stores/store'
import Button from '@/components/ButtonComponent.vue'
import VersionInfoComponent from '@/components/VersionInfoComponent.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { ZipFileHandler } from '@/model/fileHandling/ZipFileHandler'
import { BaseFactory } from '@/model/factories/BaseFactory'

store().clearStore()

const exampleFiles = ref(import.meta.env.MODE == 'demo')
const localFiles = ref(false)
// Checks whether local files exist
BaseFactory.getLocalFile('files/overview.json')
  .then(() => {
    localFiles.value = true
  })
  .catch(() => {})

BaseFactory.useLocalZipMode().then((value) => {
  if (value) {
    store().state.uploadedFileName = BaseFactory.zipFileName
    navigateToOverview()
  }
})

document.title = 'JPlag Report Viewer'

const loadingFiles = ref(false)
type fileMethod = 'query' | 'local' | 'upload' | 'unknown'
const errors: Ref<{ error: Error; source: fileMethod }[]> = ref([])

// Loads file passed in query param, if any.
const queryParams = useRoute().query
let queryFileURL: URL | null = null
if (typeof queryParams.file === 'string' && queryParams.file !== '') {
  try {
    queryFileURL = new URL(queryParams.file)
  } catch (e) {
    registerError(e as Error, 'query')
    queryFileURL = null
  }
}
if (queryFileURL !== null) {
  loadQueryFile(queryFileURL)
}
const hasQueryFile = queryFileURL !== null

function navigateToOverview() {
  router.push({
    name: 'OverviewView'
  })
}

/**
 * Handles a file on drop. It determines the file type and passes it to the corresponding handler.
 * @param file File to handle
 */
async function handleFile(file: Blob) {
  loadingFiles.value = true
  switch (file.type) {
    case 'application/zip':
    case 'application/zip-compressed':
    case 'application/x-zip-compressed':
    case 'application/x-zip':
      store().setLoadingType('zip')
      await new ZipFileHandler().handleFile(file)
      return navigateToOverview()
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
      store().state.uploadedFileName = dropped[0].name
      await handleFile(dropped[0])
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
  input.accept = '.zip'
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
    store().state.uploadedFileName = file.name
    handleFile(file)
  }
  input.click()
}

/**
 * Handles click on Continue with query file.
 */
async function loadQueryFile(url: URL) {
  try {
    const response = await fetch(url)
    if (!response.ok) {
      throw new Error('Response not OK')
    }
    await handleFile(await response.blob())
  } catch (e) {
    registerError(e as Error, 'query')
  }
}

/**
 * Handles click on Continue with local files.
 */
function continueWithLocal() {
  store().state.uploadedFileName = exampleFiles.value ? 'progpedia.zip' : BaseFactory.zipFileName
  store().setLoadingType('local')
  navigateToOverview()
}

function registerError(error: Error, source: fileMethod) {
  loadingFiles.value = false
  store().state.uploadedFileName = ''
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
      local: 'getting local files',
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
  continueWithLocal()
}
</script>
