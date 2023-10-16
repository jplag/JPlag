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
          class="mx-auto mt-8 h-auto w-60"
          src="@/assets/jplag-light-transparent.png"
          alt="JPlag Logo"
          v-if="store().uiState.useDarkMode"
        />
        <img
          class="mx-auto mt-8 h-auto w-60"
          src="@/assets/jplag-dark-transparent.png"
          alt="JPlag Logo"
          v-else
        />
      </div>
      <h1 class="text-7xl">JPlag Report Viewer</h1>
      <div v-if="!hasQueryFile && !loadingFiles">
        <div
          class="mx-auto mt-10 flex w-96 cursor-pointer flex-col justify-center rounded-md border-1 border-accent-dark bg-accent bg-opacity-25 px-5 py-5"
          @click="uploadFileThroughWindow()"
        >
          <div>Drag and Drop zip/Json file on this page</div>
          <div>Or click here to select a file</div>
        </div>
        <div>(No files will be uploaded)</div>
        <Button class="mx-auto mt-8 w-fit" @click="continueWithLocal" v-if="localFiles">
          Continue with local files
        </Button>
      </div>
      <LoadingCircle v-else class="space-y-5 pt-5" />
    </div>
    <VersionInfoComponent class="absolute bottom-3 left-3" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { router } from '@/router'
import { store } from '@/stores/store'
import Button from '@/components/ButtonComponent.vue'
import VersionInfoComponent from '@/components/VersionInfoComponent.vue'
import LoadingCircle from '@/components/LoadingCircle.vue'
import { JsonFileHandler } from '@/utils/fileHandling/JsonFileHandler'
import { ZipFileHandler } from '@/utils/fileHandling/ZipFileHandler'
import { BaseFactory } from '@/model/factories/BaseFactory'

store().clearStore()
const localFiles = ref(false)
// Checks whether local files exist
fetch('/files/overview.json')
  .then((response) => {
    if (response.status == 200) {
      localFiles.value = true
    }
  })
  .catch(() => {})

BaseFactory.useLocalZipMode().then((value) => {
  if (value) {
    navigateToOverview()
  }
})

const loadingFiles = ref(false)

// Loads file passed in query param, if any.
const queryParams = useRoute().query
let queryFileURL: URL | null = null
if (typeof queryParams.file === 'string' && queryParams.file !== '') {
  try {
    queryFileURL = new URL(queryParams.file)
  } catch (e) {
    if (e instanceof TypeError) {
      console.warn(`Invalid URL '${queryParams.file}'`)
      queryFileURL = null
    } else {
      throw e
    }
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

function navigateToComparisonView(firstId: string, secondId: string) {
  router.push({
    name: 'ComparisonView',
    params: {
      firstId,
      secondId
    }
  })
}

/**
 * Handles a json file on drop. It read the file and passes the file string to next window.
 * @param file The json file to handle
 */
async function handleJsonFile(file: Blob) {
  store().setLoadingType('single')
  const fileContentType = await new JsonFileHandler().handleFile(file)
  if (fileContentType.fileType === 'overview') {
    navigateToOverview()
  } else if (fileContentType.fileType === 'comparison') {
    navigateToComparisonView(fileContentType.id1, fileContentType.id2)
  }
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
    case 'application/json':
      return await handleJsonFile(file)
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
      await handleFile(dropped[0])
    } else {
      throw new Error('Not exactly one file')
    }
  } catch (e) {
    alert((e as Error).message)
    throw e
  }
}

async function uploadFileThroughWindow() {
  let input = document.createElement('input')
  input.type = 'file'
  input.accept = '.zip,.json'
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
    console.warn(e)
    alert(e)
  }
}

/**
 * Handles click on Continue with local files.
 */
function continueWithLocal() {
  store().setLoadingType('local')
  navigateToOverview()
}
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: var(--primary-color-light);
}

.drop-container {
  background: lightgrey;
  border: dashed dodgerblue;
  align-items: center;
  justify-content: space-around;
  border-radius: 10px;
  padding: 2%;
}

.drop-container > p {
  color: dodgerblue;
  text-align: center;
}

.local-files-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.local-files-text {
  font-weight: bold;
  color: var(--on-background-color);
}

.local-files-button {
  font-size: large;
  background: var(--primary-color);
  color: var(--on-primary-color);
  font-weight: bold;
  padding: 5%;
  border: none;
  border-radius: 10px;
  box-shadow: var(--shadow-color) 2px 3px 3px;
}

.local-files-button:hover {
  cursor: pointer;
  background: var(--primary-color-dark);
}

input {
  display: none;
}

label {
  font-weight: bold;
  font-size: larger;
  background: #ececec;
  border-radius: 10px;
  box-shadow: #777777 2px 3px 3px;
  padding: 2%;
  margin-top: 1%;
}
</style>
