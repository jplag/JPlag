import { createStore } from "vuex";

/**
 * Local store. Stores the state of the application.
 */

interface State {
  /**
   * The set of ids to be hidden.
   */
  anonymous: Set<string>;
  /**
   * Stored files if zip mode is used. Stores the files as key - file name, value - file string
   */
  files: Record<string, string>;
  submissions: Record<string, Map<string, string>>;
  /**
   * Indicates whether local mode is used.
   */
  local: boolean;
  /**
   * Indicates whether zip mode is used.
   */
  zip: boolean;
  /**
   * Indicates whether single file mode is used.
   */
  single: boolean;
  /**
   * Files string if single mode is used.
   */
  fileString: string;

  fileIdToDisplayName: Map<string, string>;
  submissionIdsToComparisonFileName: Map<string, Map<string, string>>;
}

interface File {
  fileName: string;
  data: string;
}
interface SubmissionFile {
  name: string;
  file: File;
}

interface LoadConfiguration {
  local: boolean;
  zip: boolean;
  single: boolean;
  fileString: string;
}

const store = createStore<State>({
  state: {
    submissionIdsToComparisonFileName: new Map<string, Map<string, string>>(),
    /**
     * The set of ids to be hidden.
     */
    anonymous: new Set(),
    /**
     * Stored files if zip mode is used. Stores the files as key - file name, value - file string
     */
    files: {},
    submissions: {},
    /**
     * Indicates whether local mode is used.
     */
    local: false,
    /**
     * Indicates whether zip mode is used.
     */
    zip: false,
    /**
     * Indicates whether single file mode is used.
     */
    single: false,
    /**
     * Files string if single mode is used.
     */
    fileString: "",
    fileIdToDisplayName: new Map(),
  },
  getters: {
    filesOfSubmission: (state) => (name: string) => {
      return Array.from(state.submissions[name], ([name, value]) => ({
        name,
        value,
      }));
    },
    submissionDisplayName: (state) => (id: string) => {
      return state.fileIdToDisplayName.get(id);
    },
    getSubmissionIds(state): Array<string> {
      return Array.from(state.fileIdToDisplayName.keys());
    },
    getComparisonFileName:
      (state) => (submissionId1: string, submissionId2: string) => {
        return state.submissionIdsToComparisonFileName
          .get(submissionId1)
          ?.get(submissionId2);
      },
    getComparisonFileForSubmissions:
      (state, getters) => (submissionId1: string, submissionId2: string) => {
        const expectedFileName = getters.getComparisonFileName(
          submissionId1,
          submissionId2
        );
        const index = Object.keys(store.state.files).find((name) =>
          name.endsWith(expectedFileName)
        );
        return index != undefined ? store.state.files[index] : undefined;
      },
  },
  mutations: {
    clearStore(){
      store.replaceState({
        submissionIdsToComparisonFileName: new Map<string, Map<string, string>>(),
        anonymous: new Set(),
        files: {},
        submissions: {},
        local: false,
        zip: false,
        single: false,
        fileString: "",
        fileIdToDisplayName: new Map(),
      });
    },
    addAnonymous(state: State, id) {
      for (let i = 0; i < id.length; i++) {
        state.anonymous.add(id[i]);
      }
    },
    saveComparisonFileLookup(
      state: State,
      map: Map<string, Map<string, string>>
    ) {
      state.submissionIdsToComparisonFileName = map;
    },
    removeAnonymous(state, id) {
      for (let i = 0; i < id.length; i++) {
        state.anonymous.delete(id[i]);
      }
    },
    resetAnonymous(state) {
      state.anonymous = new Set();
    },
    saveFile(state, file: File) {
      state.files[file.fileName] = file.data;
    },
    saveSubmissionNames(state, names: Map<string, string>) {
      state.fileIdToDisplayName = names;
    },
    saveSubmissionFile(state, submissionFile: SubmissionFile) {
      if (!state.submissions[submissionFile.name]) {
        state.submissions[submissionFile.name] = new Map();
      }
      state.submissions[submissionFile.name].set(
        submissionFile.file.fileName,
        submissionFile.file.data
      );
    },
    setLoadingType(state, payload: LoadConfiguration) {
      state.local = payload.local;
      state.zip = payload.zip;
      state.single = payload.single;
      state.fileString = payload.fileString;
    },
  },
});

export default store;
