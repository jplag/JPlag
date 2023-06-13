<!--
  Starting view of the application. Presents the options for loading a JPlag report.
-->
<template>
  <div
    class="h-screen text-center flex items-center"
    @dragover.prevent
    @drop.prevent="uploadFileOnDrag"
  >
    <div class="w-screen">
      <div>
        <img
          class="mt-8 w-60 h-auto mx-auto"
          src="@/assets/jplag-light-transparent.png"
          alt="JPlag Logo"
          v-if="store().uiState.useDarkMode"
        />
        <img
          class="mt-8 w-60 h-auto mx-auto"
          src="@/assets/jplag-dark-transparent.png"
          alt="JPlag Logo"
          v-else
        />
      </div>
      <h1 class="text-7xl">JPlag Report Viewer</h1>
      <div v-if="!hasQueryFile">
        <div
          class="px-5 py-5 mt-10 w-96 mx-auto flex flex-col justify-center cursor-pointer border-2 rounded-md border-accent-dark bg-accent bg-opacity-25"
          @click="uploadFileThroughWindow()"
        >
          <div>Drag and Drop zip/Json file on this page</div>
          <div>Or click here to select a file</div>
        </div>
        <div>(No files will be uploaded)</div>
        <Button class="w-fit mx-auto mt-8" @click="continueWithLocal" v-if="hasLocalFile">
          Continue with local files
        </Button>
      </div>
      <div v-else>Loading file...</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import jszip from 'jszip'
import router from '@/router'
import store from '@/stores/store'
import slash from 'slash'
import Button from '@/components/ButtonComponent.vue'

class LoadError extends Error {}

store().clearStore()
const hasLocalFile = ref(false)
// Checks whether local files exist
fetch('/files/overview.json').then((response) => (hasLocalFile.value = response.status == 200))

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
 * Gets the root name of the given directory path.
 * @param directoryPath Path to extract the root name from.
 */
function extractRootName(directoryPath: string) {
  const folders = directoryPath.split('/')
  return folders[0]
}

/**
 * Extracts the submission file name from the given directory path.
 * @param directoryPath Path to extract the submission file name from.
 */
function extractSubmissionFileName(directoryPath: string) {
  const folders = directoryPath.split('/')
  const rootName = folders[0]
  let submissionFolderIndex = -1
  if (rootName === 'files') {
    submissionFolderIndex = folders.findIndex((folder) => folder === 'files')
  } else {
    submissionFolderIndex = folders.findIndex((folder) => folder === 'submissions')
  }
  return folders[submissionFolderIndex + 1]
}

/**
 * Gets the full path of the file
 * @param directoryPath Path to the file
 * @param fileBase Name of the file
 * @param originalFileName Original name of the file
 */
function extractFileNameWithFullPath(
  directoryPath: string,
  fileBase: string,
  originalFileName: string
) {
  let fullPath = ''
  const rootName = extractRootName(directoryPath)
  const filesOrSubmissionsIndex_filePath = directoryPath.indexOf(
    rootName === 'files' ? 'files' : 'submissions'
  )
  const filesOrSubmissionsIndex_originalFileName = originalFileName.indexOf(
    rootName === 'files' ? 'files' : 'submissions'
  )
  const unixSubfolderPathAfterSubmissions = directoryPath.substring(
    filesOrSubmissionsIndex_filePath +
      (rootName === 'files' ? 'files'.length : 'submissions'.length) +
      1
  )
  const originalPathWithoutSubmissions = originalFileName.substring(
    filesOrSubmissionsIndex_originalFileName +
      (rootName === 'files' ? 'files'.length : 'submissions'.length)
  )
  if (originalPathWithoutSubmissions.charAt(0) === '\\') {
    fullPath = unixSubfolderPathAfterSubmissions + '\\' + fileBase
    while (fullPath.includes('/')) {
      fullPath = fullPath.replace('/', '\\')
    }
  } else {
    fullPath = unixSubfolderPathAfterSubmissions + '/' + fileBase
  }
  return fullPath
}

/**
 * Handles zip file on drop. It extracts the zip and saves each file in the store.
 * @param file Zip file to handle
 */
async function handleZipFile(file: Blob) {
  console.log('Start handling zip file and storing necessary data...')
  return jszip.loadAsync(file).then(async (zip) => {
    for (const originalFileName of Object.keys(zip.files)) {
      const unixFileName = slash(originalFileName)
      if (
        /((.+\/)*)(files|submissions)\/(.+)\/(.+)/.test(unixFileName) &&
        !/^__MACOSX\//.test(unixFileName)
      ) {
        const directoryPath = unixFileName.substring(0, unixFileName.lastIndexOf('/'))
        const fileBase = unixFileName.substring(unixFileName.lastIndexOf('/') + 1)

        const submissionFileName = extractSubmissionFileName(directoryPath)
        const fullPathFileName = extractFileNameWithFullPath(
          directoryPath,
          fileBase,
          originalFileName
        )
        await zip.files[originalFileName].async('string').then((data) => {
          store().saveSubmissionFile({
            name: submissionFileName,
            file: { fileName: fullPathFileName, data: data }
          })
        })
      } else {
        await zip.files[originalFileName].async('string').then((data) => {
          store().saveFile({ fileName: unixFileName, data: data })
        })
      }
    }
    store().setLoadingType({
      local: false,
      zip: true,
      single: false,
      fileString: ''
    })
    navigateToOverview()
  })
}

/**
 * Handles a json file on drop. It read the file and passes the file string to next window.
 * @param str Content of the json file
 */
function handleJsonFile(str: string) {
  let json = JSON.parse(str)
  if (json['submission_folder_path']) {
    store().setLoadingType({
      local: false,
      zip: false,
      single: true,
      fileString: str
    })
    navigateToOverview()
  } else if (json['id1'] && json['id2']) {
    store().setLoadingType({
      local: false,
      zip: false,
      single: true,
      fileString: str
    })
    navigateToComparisonView(json['id1'], json['id2'])
  } else {
    throw new LoadError(`Invalid JSON: ${json}`)
  }
}

/**
 * Handles a file on drop. It determines the file type and passes it to the corresponding handler.
 * @param file File to handle
 */
async function handleFile(file: Blob) {
  switch (file.type) {
    case 'application/zip':
    case 'application/zip-compressed':
    case 'application/x-zip-compressed':
    case 'application/x-zip':
      return handleZipFile(file)
    case 'application/json':
      return file.text().then(handleJsonFile)
    default:
      throw new LoadError(`Unknown MIME type '${file.type}'`)
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
      throw new LoadError('Not exactly one file')
    }
  } catch (e) {
    if (e instanceof LoadError) {
      console.warn(e)
      alert(e.message)
    } else {
      throw e
    }
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
      throw new LoadError('Response not OK')
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
  store().setLoadingType({
    local: true,
    zip: false,
    single: false,
    fileString: ''
  })
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
