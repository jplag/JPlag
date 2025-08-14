import { router } from '../router'
import { minimalReportVersion, reportViewerVersion } from '../version/versions'
import {
  CliOptions,
  Cluster,
  ComparisonListElement,
  DistributionMap,
  File,
  RunInformation,
  SubmissionFile,
  Version
} from '@jplag/model'
import {
  BaseCodeReportFactory,
  ClusterFactory,
  ComparisonFactory,
  DistributionFactory,
  OptionsFactory,
  RunInformationFactory,
  SubmissionMappingsFactory,
  TopComparisonFactory
} from '@jplag/parser'
import { defineStore } from 'pinia'
import { ref, Ref } from 'vue'

export const reportStore = defineStore('reportStore', () => {
  const cliOptions: Ref<CliOptions | null> = ref(null)
  const cluster: Ref<Cluster[] | null> = ref(null)
  const distribution: Ref<DistributionMap | null> = ref(null)
  const runInformation: Ref<RunInformation | null> = ref(null)
  const topComparisons: Ref<ComparisonListElement[] | null> = ref(null)
  const idToDisplayNameMap: Ref<Map<string, string> | null> = ref(null)
  const comparisonFilesLookup: Ref<Map<string, Map<string, string>> | null> = ref(null)

  const reportFileName = ref<string | null>(null)
  const files = ref(new Map<string, File>())
  const submissionFiles = ref(new Map<string, SubmissionFile[]>())

  const anonymizedSet = ref(new Set<string>())
  const anonymizedNumber = ref(new Map<string, number>())

  function loadReport(
    _files: File[],
    _submissionFiles: SubmissionFile[],
    fileName: string
  ): boolean {
    _files.forEach((f) => files.value.set(f.fileName, f))
    reportFileName.value = fileName
    // check for version and redirect to old version if neccessary
    const version = getVersionFromRaw(
      files.value.get('runInformation.json'),
      files.value.get('overview.json')
    )
    if (!version) {
      throw new Error('Could not find a version in the report')
    }
    const r = compareVersions(version, reportViewerVersion, minimalReportVersion)
    if (!r) {
      files.value.clear()
      router.push({ name: 'OldVersionRedirectView', params: { version: version.toString() } })
      return false
    }

    // we delay the loading of the submission files till after the version check, since this step might take longer, due to the large amount of files
    _submissionFiles.forEach((f) => {
      const files = submissionFiles.value.get(f.submissionId) ?? []
      files.push(f)
      submissionFiles.value.set(f.submissionId, files)
    })

    // load all non submission or comparison specific information
    cluster.value = ClusterFactory.getClusters(getFile('cluster.json').data)
    distribution.value = DistributionFactory.getDistributions(getFile('distribution.json').data)
    cliOptions.value = OptionsFactory.getCliOptions(getFile('options.json').data)
    runInformation.value = RunInformationFactory.getRunInformation(
      getFile('runInformation.json').data
    )
    topComparisons.value = TopComparisonFactory.getTopComparisons(
      getFile('topComparisons.json').data,
      cluster.value
    )
    const mappings = SubmissionMappingsFactory.getSubmissionMappings(
      getFile('submissionMappings.json').data
    )
    idToDisplayNameMap.value = mappings.idToDisplayNameMap
    comparisonFilesLookup.value = mappings.comparisonFilesLookup

    return true
  }

  function isReportLoaded() {
    return (
      cliOptions.value !== null &&
      cluster.value !== null &&
      distribution.value !== null &&
      runInformation.value !== null &&
      topComparisons.value !== null &&
      idToDisplayNameMap.value !== null &&
      comparisonFilesLookup.value !== null &&
      reportFileName.value !== null
    )
  }

  function getComparisonFile(firstSubmissionId: string, secondSubmissionId: string) {
    if (!comparisonFilesLookup.value) {
      throw new Error('Comparison files lookup is not initialized')
    }
    const comparisonFile = comparisonFilesLookup.value
      .get(firstSubmissionId)
      ?.get(secondSubmissionId)
    if (!comparisonFile) {
      throw new Error(
        `Expected to find comparison file for ${firstSubmissionId} and ${secondSubmissionId} but did not`
      )
    }
    return comparisonFile
  }
  function getComparison(firstSubmissionId: string, secondSubmissionId: string) {
    const comparisonFileName = getComparisonFile(firstSubmissionId, secondSubmissionId)

    const comparisonFile = getFile(`comparisons/${comparisonFileName}`)
    const submissionFileIndex = getFile('submissionFileIndex.json')
    const filesOfFirstSubmission = submissionFiles.value.get(firstSubmissionId)
    const filesOfSecondSubmission = submissionFiles.value.get(secondSubmissionId)
    if (!filesOfFirstSubmission) {
      throw new Error(`Expected to find submission files for ${firstSubmissionId} but did not`)
    }
    if (!filesOfSecondSubmission) {
      throw new Error(`Expected to find submission files for ${secondSubmissionId} but did not`)
    }

    return ComparisonFactory.getComparison(
      comparisonFile.data,
      submissionFileIndex.data,
      filesOfFirstSubmission,
      filesOfSecondSubmission
    )
  }
  function getBaseCodeReport(submissionId: string) {
    const baseCodeFile = getFile(`basecode/${submissionId}.json`)
    return BaseCodeReportFactory.getReport(baseCodeFile.data)
  }

  function getSubmissionCount() {
    if (!idToDisplayNameMap.value) {
      throw new Error('Submission files are not initialized')
    }
    return idToDisplayNameMap.value.size
  }
  function getSubmissionIds() {
    if (!idToDisplayNameMap.value) {
      throw new Error('Submission files are not initialized')
    }
    return Array.from(idToDisplayNameMap.value.keys())
  }

  function includedComparisonCount() {
    if (!topComparisons.value) {
      throw new Error('Top comparisons are not initialized')
    }
    return topComparisons.value.length
  }

  function getReportFileName() {
    if (!reportFileName.value) {
      throw new Error('Report file name is not set')
    }
    return reportFileName.value
  }

  function getFile(fileName: string) {
    const file = files.value.get(fileName)
    if (!file) {
      throw new Error(`Expected to find file ${fileName} in report but did not`)
    }
    return file
  }

  function getCluster(index: number) {
    if (!cluster.value) {
      throw new Error('Clusters are not initialized')
    }
    return cluster.value[index]
  }
  function getAllClusters() {
    if (!cluster.value) {
      throw new Error('Clusters are not initialized')
    }
    return cluster.value
  }
  function getCliOptions() {
    if (!cliOptions.value) {
      throw new Error('CLI options are not initialized')
    }
    return cliOptions.value
  }
  function getDistributions() {
    if (!distribution.value) {
      throw new Error('Distribution is not initialized')
    }
    return distribution.value
  }
  function getRunInformation() {
    if (!runInformation.value) {
      throw new Error('Run information is not initialized')
    }
    return runInformation.value
  }
  function getTopComparisons() {
    if (!topComparisons.value) {
      throw new Error('Top comparisons are not initialized')
    }
    return topComparisons.value
  }

  function getAnonymizedName(submissionId: string) {
    let number = anonymizedNumber.value.get(submissionId)
    if (number === undefined) {
      number = anonymizedNumber.value.size + 1
      anonymizedNumber.value.set(submissionId, number)
    }
    return `anon${number}`
  }
  function getPlainDisplayName(submissionId: string) {
    if (!idToDisplayNameMap.value) {
      throw new Error('ID to display name map is not initialized')
    }
    return idToDisplayNameMap.value.get(submissionId) ?? submissionId
  }
  function isAnonymized(submissionId: string) {
    return anonymizedSet.value.has(submissionId)
  }
  function getDisplayName(submissionId: string) {
    if (isAnonymized(submissionId)) {
      return getAnonymizedName(submissionId)
    }
    return getPlainDisplayName(submissionId)
  }
  function toggleAnonymous(submissionId: string) {
    if (anonymizedSet.value.has(submissionId)) {
      anonymizedSet.value.delete(submissionId)
    } else {
      anonymizedSet.value.add(submissionId)
    }
  }
  function setAnonymous(id: string, anonymized: boolean) {
    if (anonymized) {
      anonymizedSet.value.add(id)
    } else {
      anonymizedSet.value.delete(id)
    }
  }
  function allAreAnonymized() {
    return anonymizedSet.value.size === getSubmissionCount()
  }
  function toggleAnonymousForAll() {
    if (allAreAnonymized()) {
      anonymizedSet.value.clear()
    } else {
      idToDisplayNameMap.value!.forEach((_, id) => {
        anonymizedSet.value.add(id)
      })
    }
  }

  function reset() {
    cliOptions.value = null
    cluster.value = null
    distribution.value = null
    runInformation.value = null
    topComparisons.value = null
    idToDisplayNameMap.value = null
    comparisonFilesLookup.value = null
    reportFileName.value = null
    files.value.clear()
    submissionFiles.value.clear()
    anonymizedSet.value.clear()
    anonymizedNumber.value.clear()
  }

  return {
    loadReport,
    isReportLoaded,
    getComparison,
    getSubmissionCount,
    getReportFileName,
    includedComparisonCount,
    getCluster,
    getCliOptions,
    getDistributions,
    getRunInformation,
    getTopComparisons,
    getAllClusters,
    reset,
    getDisplayName,
    isAnonymized,
    toggleAnonymous,
    getPlainDisplayName,
    getAnonymizedName,
    toggleAnonymousForAll,
    allAreAnonymized,
    setAnonymous,
    getBaseCodeReport,
    getSubmissionIds
  }
})

function getVersionFromRaw(runInformation?: File, overview?: File) {
  if (runInformation) {
    return Version.fromJsonField(JSON.parse(runInformation.data).version)
  }
  if (overview) {
    return Version.fromJsonField(JSON.parse(overview.data).jplag_version)
  }
  return undefined
}

/**
 * Compares the two versions and shows an alert if they are not equal and puts out a warning if they are not
 * @param jsonVersion the version of the json file
 * @param reportViewerVersion the version of the report viewer
 * @param minimalVersion the minimal report version expected
 * @return true if the version is supported, false if the version is old
 */
function compareVersions(
  jsonVersion: Version,
  reportViewerVersion: Version,
  minimalVersion: Version = new Version(0, 0, 0)
) {
  if (sessionStorage.getItem('versionAlert') === null) {
    if (reportViewerVersion.isInvalid()) {
      console.warn(
        "The report viewer's version cannot be read from version.json file. Please configure it correctly."
      )
    } else if (
      !reportViewerVersion.isDevVersion() &&
      jsonVersion.compareTo(reportViewerVersion) > 0
    ) {
      alert(
        "The result's version(" +
          jsonVersion.toString() +
          ") is newer than the report viewer's version(" +
          reportViewerVersion.toString() +
          '). ' +
          'Trying to read it anyhow but be careful.'
      )
    }
    sessionStorage.setItem('versionAlert', 'true')
  }
  return jsonVersion.compareTo(minimalVersion) >= 0
}
