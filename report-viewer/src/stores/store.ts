import { defineStore } from 'pinia'
import type { State, SubmissionFile, File, LoadConfiguration } from './state'
import router from '@/router'

/**
 * The store is a global state management system. It is used to store the state of the application.
 */
const store = defineStore('store', {
  state: (): State => ({
    submissionIdsToComparisonFileName: new Map<string, Map<string, string>>(),
    anonymous: new Set(),
    files: {},
    submissions: {},
    local: false,
    zip: false,
    single: false,
    fileString: '',
    fileIdToDisplayName: new Map()
  }),
  getters: {
    /**
     * @param name the name of the submission
     * @returns files in the submission of the given name
     */
    filesOfSubmission: (state) => (name: string) => {
      return Array.from(state.submissions[name], ([name, value]) => ({
        name,
        value
      }))
    },
    /**
     * @param name the name of the submission
     * @returns the display name of the submission
     */
    submissionDisplayName: (state) => (id: string) => {
      return state.fileIdToDisplayName.get(id)
    },
    /**
     * @returns the Ids of all submissions
     */
    getSubmissionIds(state): Array<string> {
      return Array.from(state.fileIdToDisplayName.keys())
    },
    /**
     * @param submissionId1 the id of the first submission
     * @param submissionId2 the id of the second submission
     * @returns the name of the comparison file between the two submissions
     */
    getComparisonFileName: (state) => (submissionId1: string, submissionId2: string) => {
      return state.submissionIdsToComparisonFileName.get(submissionId1)?.get(submissionId2)
    },
    /**
     * @param submissionId1 the id of the first submission
     * @param submissionId2 the id of the second submission
     * @returns the comparison file between the two submissions
     */
    getComparisonFileForSubmissions() {
      return (submissionId1: string, submissionId2: string) => {
        const expectedFileName = this.getComparisonFileName(submissionId1, submissionId2)
        const index = expectedFileName
          ? Object.keys(this.files).find((name) => name.endsWith(expectedFileName))
          : undefined
        return index != undefined ? this.files[index] : undefined
      }
    }
  },
  actions: {
    /**
     * Clears the store
     */
    clearStore() {
      this.$reset()
    },
    /**
     * Adds the given ids to the set of anonymous submissions
     * @param id the id of the submission to hide
     */
    addAnonymous(id: string[]) {
      for (let i = 0; i < id.length; i++) {
        this.anonymous.add(id[i])
      }
    },
    /**
     * Removes the given ids from the set of anonymous submissions
     * @param id the id of the submission to show
     */
    removeAnonymous(id: string[]) {
      for (let i = 0; i < id.length; i++) {
        this.anonymous.delete(id[i])
      }
    },
    /**
     * Clears the set of anonymous submissions
     */
    resetAnonymous() {
      this.anonymous = new Set()
    },
    /**
     * Sets the map of submission ids to comparison file names
     * @param map the map to set
     */
    saveComparisonFileLookup(map: Map<string, Map<string, string>>) {
      this.submissionIdsToComparisonFileName = map
    },
    /**
     * Saves the given file
     * @param file the file to save
     */
    saveFile(file: File) {
      this.files[file.fileName] = file.data
    },
    /**
     * Saves the given submission names
     * @param names the names to save
     */
    saveSubmissionNames(names: Map<string, string>) {
      this.fileIdToDisplayName = names
    },
    /**
     * Saves the given submission file
     * @param submissionFile the submission file to save
     */
    saveSubmissionFile(submissionFile: SubmissionFile) {
      if (!this.submissions[submissionFile.name]) {
        this.submissions[submissionFile.name] = new Map()
      }
      this.submissions[submissionFile.name].set(
        submissionFile.file.fileName,
        submissionFile.file.data
      )
    },
    /**
     * Sets the loading type
     * @param payload Type used to input JPlag results
     */
    setLoadingType(payload: LoadConfiguration) {
      this.local = payload.local
      this.zip = payload.zip
      this.single = payload.single
      this.fileString = payload.fileString
    }
  }
})

export default store
