import { defineStore } from 'pinia'
import type { State, SubmissionFile, File, LoadConfiguration } from './state'

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
    filesOfSubmission: (state) => (name: string) => {
      return Array.from(state.submissions[name], ([name, value]) => ({
        name,
        value
      }))
    },
    submissionDisplayName: (state) => (id: string) => {
      return state.fileIdToDisplayName.get(id)
    },
    getSubmissionIds(state): Array<string> {
      return Array.from(state.fileIdToDisplayName.keys())
    },
    getComparisonFileName: (state) => (submissionId1: string, submissionId2: string) => {
      return state.submissionIdsToComparisonFileName.get(submissionId1)?.get(submissionId2)
    },
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
    clearStore() {
      this.$reset()
    },
    addAnonymous(id: string[]) {
      for (let i = 0; i < id.length; i++) {
        this.anonymous.add(id[i])
      }
    },
    saveComparisonFileLookup(map: Map<string, Map<string, string>>) {
      this.submissionIdsToComparisonFileName = map
    },
    removeAnonymous(id: string[]) {
      for (let i = 0; i < id.length; i++) {
        this.anonymous.delete(id[i])
      }
    },
    resetAnonymous() {
      this.anonymous = new Set()
    },
    saveFile(file: File) {
      this.files[file.fileName] = file.data
    },
    saveSubmissionNames(names: Map<string, string>) {
      this.fileIdToDisplayName = names
    },
    saveSubmissionFile(submissionFile: SubmissionFile) {
      if (!this.submissions[submissionFile.name]) {
        this.submissions[submissionFile.name] = new Map()
      }
      this.submissions[submissionFile.name].set(
        submissionFile.file.fileName,
        submissionFile.file.data
      )
    },
    setLoadingType(payload: LoadConfiguration) {
      this.local = payload.local
      this.zip = payload.zip
      this.single = payload.single
      this.fileString = payload.fileString
    }
  }
})

export default store
