<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <div class="flex-1 md:overflow-hidden print:flex-none">
      <div class="flex max-h-full flex-col gap-5 md:overflow-hidden">
        <ContainerComponent class="row-start-1 h-fit">
          <div class="grid grid-cols-[1fr_auto] grid-rows-[auto_1fr] gap-2">
            <h1 class="col-start-1 row-start-1 text-2xl">JPlag</h1>

            <InteractableComponent
              class="col-start-2 row-span-2 row-start-1 h-fit border-green-900! bg-green-700! text-white"
              @click="run()"
            >
              <div class="flex items-center gap-x-2 px-5">
                <FontAwesomeIcon :icon="faPlay" />
                Run
              </div>
            </InteractableComponent>

            <p class="col-start-1 row-start-2 text-sm">
              More information on each parameter can be found here:
              <a
                href="https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag"
                class="dark:text-link text-link-dark underline"
                >Parameter Description</a
              >
            </p>
          </div>
        </ContainerComponent>

        <div
          ref="scrollContainer"
          class="grid flex-1 grid-cols-1 gap-3 overflow-auto md:grid-cols-2"
          @scroll="updateScrollOffset()"
        >
          <CliViewCategory class="h-full min-h-fit md:col-start-1 md:row-start-1">
            <template #heading> Comparison Options </template>

            <CliUiOption
              label="Language"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.LANGUAGE"
            >
              <DropDownSelector v-model="language" :options="languageList" class="w-60" />
            </CliUiOption>
            <CliUiOption
              label="Minimum Token Match"
              :error="minTokenMatch.error"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.MIN_TOKENS"
            >
              <InputWrapper v-model="minTokenMatch.value" class="w-60" />
            </CliUiOption>
            <CliUiOption
              label="Normalize"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.NORMALIZE"
            >
              <SwitchComponent v-model="doNormalization" />
            </CliUiOption>
          </CliViewCategory>

          <CliViewCategory class="h-full min-h-fit md:col-start-2 md:row-start-1">
            <template #heading> Report Options </template>

            <CliUiOption
              label="Result File Name"
              :error="resultFileName.error"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.RESULT_FILE"
            >
              <InputWrapper v-model="resultFileName.value" class="w-60" />
            </CliUiOption>
            <CliUiOption
              :error="shownComparisons.error"
              label="Shown Comparisons"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.SHOWN_COMPARISONS"
            >
              <InputWrapper v-model="shownComparisons.value" class="w-60" />
            </CliUiOption>
            <CliUiOption
              :error="similarityThreshold.error"
              label="Similarity Treshold"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.SIMILARITY_TRESHOLD"
            >
              <InputWrapper v-model="similarityThreshold.value" class="w-60" />
            </CliUiOption>
            <CliUiOption
              label="Overwrite Existing"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.OVERWRITE"
            >
              <SwitchComponent v-model="overwriteResultFile" />
            </CliUiOption>
            <CliUiOption
              label="CSV File"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.CSV"
            >
              <SwitchComponent v-model="generateCsvFile" />
            </CliUiOption>
          </CliViewCategory>

          <CliViewCategory class="h-fit md:col-span-2 md:row-start-2">
            <template #heading> Submission Files </template>

            <CliUiOption
              label="Submission Directory"
              :error="submissionDirectories.error"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.ROOT_DIRS"
            >
              <div class="flex flex-wrap gap-x-2 gap-y-1">
                <div
                  v-for="[idx, dir] in submissionDirectories.value.entries()"
                  :key="dir"
                  class="border-interactable-border-light dark:border-interactable-border-dark bg-interactable-light dark:bg-interactable-dark flex items-center gap-x-2 rounded-full border px-2 text-xs"
                >
                  {{ dir }}
                  <span class="cursor-pointer" @click="submissionDirectories.value.splice(idx, 1)"
                    >X</span
                  >
                </div>

                <button>
                  <InteractableComponent class="py-0!" @click="addSubmissionDirectory()"
                    >Add</InteractableComponent
                  >
                </button>
              </div>
            </CliUiOption>
            <CliUiOption
              label="Basecode Directory"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.BASE_CODE"
            >
            </CliUiOption>
            <CliUiOption
              label="Old Submission Directories"
              :error="oldDirectories.error"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.OLD"
            >
              <div class="flex flex-wrap gap-x-2 gap-y-1">
                <div
                  v-for="[idx, dir] in oldDirectories.value.entries()"
                  :key="dir"
                  class="border-interactable-border-light dark:border-interactable-border-dark bg-interactable-light dark:bg-interactable-dark flex items-center gap-x-2 rounded-full border px-2 text-xs"
                >
                  {{ dir }}
                  <span class="cursor-pointer" @click="oldDirectories.value.splice(idx, 1)">X</span>
                </div>

                <button>
                  <InteractableComponent class="py-0!" @click="addOldDirectory()"
                    >Add</InteractableComponent
                  >
                </button>
              </div>
            </CliUiOption>
          </CliViewCategory>

          <CliViewCategory
            class="min-h-fit md:col-start-1 md:row-start-3"
            :class="enableClustering ? 'h-full' : 'h-fit'"
            :show-content="enableClustering"
          >
            <template #heading>
              <span class="flex items-center gap-2">
                <SwitchComponent v-model="enableClustering" clas="col-start-1" />
                <span class="col-start-2">Clustering</span>

                <ToolTipComponent direction="left">
                  <span class="flex h-full items-start text-xs text-slate-500">
                    <FontAwesomeIcon :icon="faInfoCircle" />
                  </span>
                  <template #tooltip>
                    <p class="max-w-32 text-sm font-normal whitespace-pre-wrap">
                      {{ CliToolTip.CLUSTERING }}
                    </p>
                  </template>
                </ToolTipComponent>
              </span>
            </template>

            <CliUiOption
              label="Algorithm"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.CLUSTER_ALGORITHM"
            >
              <OptionsSelectorComponent
                :labels="ClusteringAlgorithmList"
                @selection-changed="(i) => (clusteringAlgorithm = ClusteringAlgorithmList[i])"
              />
            </CliUiOption>
            <CliUiOption
              label="Metric"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.CLUSTER_METRIC"
            >
              <OptionsSelectorComponent
                :labels="ClusterMetricList"
                @selection-changed="(i) => (clusterMetric = ClusterMetricList[i])"
              />
            </CliUiOption>
          </CliViewCategory>

          <CliViewCategory
            class="min-h-fit md:col-start-2 md:row-start-3"
            :class="enableMatchMerging ? 'h-full' : 'h-fit'"
            :show-content="enableMatchMerging"
          >
            <template #heading>
              <span class="flex items-center gap-2">
                <SwitchComponent v-model="enableMatchMerging" clas="col-start-1" />
                <span class="col-start-2">Match Merging</span>

                <ToolTipComponent direction="left">
                  <span class="flex h-full items-start text-xs text-slate-500">
                    <FontAwesomeIcon :icon="faInfoCircle" />
                  </span>
                  <template #tooltip>
                    <p class="max-w-60 text-sm font-normal whitespace-pre-wrap">
                      {{ CliToolTip.MATCH_MERGING }}
                    </p>
                  </template>
                </ToolTipComponent>
              </span>
            </template>

            <CliUiOption
              :error="smmGapSize.error"
              label="Max Gap Size"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.SMM_GAP_SIZE"
            >
              <InputWrapper v-model="smmGapSize.value" class="w-60" />
            </CliUiOption>
            <CliUiOption
              :error="smmNeighbourLength.error"
              label="Neighbour Length"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.SMM_NEIGHBOUR_LENGTH"
            >
              <InputWrapper v-model="smmNeighbourLength.value" class="w-60" />
            </CliUiOption>
            <CliUiOption
              :error="smmRequiedMerges.error"
              label="Required Merges"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.SMM_REQUIERED_MERGES"
            >
              <InputWrapper v-model="smmRequiedMerges.value" class="w-60" />
            </CliUiOption>
          </CliViewCategory>

          <CliViewCategory
            class="h-fit md:col-span-2 md:row-start-4"
            :show-content="showAdvancedOptions"
          >
            <template #heading>
              <span class="cursor-pointer" @click="showAdvancedOptions = !showAdvancedOptions">
                <div class="flex items-center gap-x-2">
                  <FontAwesomeIcon :icon="showAdvancedOptions ? faCaretDown : faCaretRight" />
                  Advanced Options
                </div>
              </span>
            </template>

            <CliUiOption
              label="Exlcusion File"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.EXCLUSION_FILE"
            ></CliUiOption>
            <CliUiOption label="File Extensions"></CliUiOption>
            <CliUiOption
              label="Subdirectory"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.SUBDIRECTORY"
            ></CliUiOption>
            <CliUiOption label="Debug" :scroll-offset-y="scrollOffsetY" :tooltip="CliToolTip.DEBUG">
              <SwitchComponent v-model="doDebug" />
            </CliUiOption>
            <CliUiOption
              label="Log Level"
              :scroll-offset-y="scrollOffsetY"
              :tooltip="CliToolTip.LOG_LEVEL"
            >
              <OptionsSelectorComponent
                :default-selected="2"
                :labels="LogLevelList"
                @selection-changed="(i) => (logLevel = LogLevelList[i])"
              />
            </CliUiOption>
          </CliViewCategory>
        </div>
      </div>
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import CliUiOption from '@/components/CliUiOption.vue'
import CliViewCategory from '@/components/CliViewCategory.vue'
import ContainerComponent from '@/components/ContainerComponent.vue'
import InteractableComponent from '@/components/InteractableComponent.vue'
import SwitchComponent from '@/components/input/SwitchComponent.vue'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import { faCaretDown, faCaretRight, faInfoCircle, faPlay } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { ref, type Ref } from 'vue'
import InputWrapper from '@/components/input/InputWrapper.vue'
import { languageList, ParserLanguage } from '@/model/Language'
import DropDownSelector from '@/components/input/DropDownSelector.vue'
import OptionsSelectorComponent from '@/components/optionsSelectors/OptionsSelectorComponent.vue'
import { CliToolTip } from '@/model/ui/CliToolTip'
import ToolTipComponent from '@/components/ToolTipComponent.vue'

const scrollContainer = ref<HTMLElement | null>(null)
const scrollOffsetY = ref(0)
function updateScrollOffset() {
  if (scrollContainer.value) {
    scrollOffsetY.value = scrollContainer.value.scrollTop
  }
}

const showAdvancedOptions = ref(false)
const enableClustering = ref(true)
const enableMatchMerging = ref(false)

const minTokenMatch = ref<Information<string>>({
  value: 'default'
})
const language = ref<ParserLanguage>(ParserLanguage.JAVA)
const doNormalization = ref(false)

const resultFileName = ref<Information<string>>({
  value: 'result.jplag'
})
const shownComparisons = ref<Information<string>>({
  value: '2500'
})
const similarityThreshold = ref<Information<string>>({
  value: '0.0'
})
const overwriteResultFile = ref(false)
const generateCsvFile = ref(false)

const submissionDirectories = ref<Information<string[]>>({
  value: []
})
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const baseCodeDirectory = ref('')
const oldDirectories = ref<Information<string[]>>({
  value: []
})

type ClusteringAlgorithm = 'SPECTRAL' | 'AGGLOMERATIVE'
const ClusteringAlgorithmList: ClusteringAlgorithm[] = ['SPECTRAL', 'AGGLOMERATIVE']
type ClusterMetric = 'AVG' | 'MAX' | 'MIN' | 'INTERSECTION'
const ClusterMetricList: ClusterMetric[] = ['AVG', 'MAX', 'MIN', 'INTERSECTION']
const clusteringAlgorithm = ref<ClusteringAlgorithm>('SPECTRAL')
const clusterMetric = ref<ClusterMetric>('AVG')

const smmGapSize = ref<Information<string>>({
  value: '6'
})
const smmNeighbourLength = ref<Information<string>>({
  value: '2'
})
const smmRequiedMerges = ref<Information<string>>({
  value: '6'
})

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const fileExtensions = ref<string[]>([])
const doDebug = ref(false)
type LogLevel = 'ERROR' | 'WARN' | 'INFO' | 'DEBUG' | 'TRACE'
const LogLevelList: LogLevel[] = ['ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE']
const logLevel = ref<LogLevel>('INFO')

interface Information<T> {
  value: T
  error?: string
}

function run() {
  let foundError = false
  let mtm = Infinity
  if (resultFileName.value.value.trim() === '') {
    resultFileName.value.error = 'Please enter a result file name'
    foundError = true
  } else {
    resultFileName.value.error = undefined
  }
  if (minTokenMatch.value.value !== 'default') {
    if (!verifyNumber(minTokenMatch, true, 1)) {
      foundError = true
    } else {
      mtm = parseInt(minTokenMatch.value.value)
    }
  } else {
    minTokenMatch.value.error = undefined
  }
  if (!verifyNumber(shownComparisons, true, 1)) {
    foundError = true
  }
  if (!verifyNumber(similarityThreshold, false, 0, 1)) {
    foundError = true
  }

  if (submissionDirectories.value.value.length <= 0) {
    submissionDirectories.value.error = 'Please select at least one submission directory'
    foundError = true
  } else {
    submissionDirectories.value.error = undefined
  }

  if (enableMatchMerging.value) {
    if (!verifyNumber(smmGapSize, true, 1, mtm)) {
      foundError = true
    }
    if (!verifyNumber(smmNeighbourLength, true, 1, mtm)) {
      foundError = true
    }
    if (!verifyNumber(smmRequiedMerges, true, 1, 50)) {
      foundError = true
    }
  } else {
    smmGapSize.value.error = undefined
  }

  if (foundError) {
    return
  }

  alert('Run')
}

function verifyNumber(
  information: Ref<Information<string>>,
  isInt = true,
  min: number = 0,
  max: number = Infinity
) {
  const value = Number(information.value.value)

  if (isNaN(value)) {
    information.value.error = 'Entered value needs to be a number'
    return false
  }
  if (isInt && Math.round(value) !== value) {
    information.value.error = 'Entered value needs to be an integer'
    return false
  }
  if (value < min) {
    information.value.error = `Value must be greater than ${min}`
    return false
  }
  if (value > max) {
    information.value.error = `Value must be less than ${max}`
    return false
  }

  information.value.error = undefined
  return true
}

async function getFolderName() {
  const r = await fetch(location.origin + '/folder')
  if (r.status >= 200 && r.status < 300) {
    return (await r.text()) + '/' + Math.random().toString(36).substring(2, 7)
  } else {
    return null
  }
}

async function addSubmissionDirectory() {
  const name = await getFolderName()
  if (name) {
    if (submissionDirectories.value.value.includes(name)) {
      submissionDirectories.value.error = 'Directory already added'
      return
    } else {
      submissionDirectories.value.error = undefined
      submissionDirectories.value.value.push(name)
    }
  }
}

async function addOldDirectory() {
  const name = await getFolderName()
  if (name) {
    if (oldDirectories.value.value.includes(name)) {
      oldDirectories.value.error = 'Directory already added'
      return
    } else {
      oldDirectories.value.error = undefined
      oldDirectories.value.value.push(name)
    }
  }
}
</script>
