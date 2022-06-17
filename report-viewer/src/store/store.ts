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
}

interface File {
  fileName: string;
  data: string;
}

interface LoadConfiguration {
  local: boolean;
  zip: boolean;
  single: boolean;
  fileString: string;
}

const store = createStore<State>({
  state: {
    /**
     * The set of ids to be hidden.
     */
    anonymous: new Set(),
    /**
     * Stored files if zip mode is used. Stores the files as key - file name, value - file string
     */
    files: {},
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
  },
  mutations: {
    addAnonymous(state: State, id) {
      for (let i = 0; i < id.length; i++) {
        state.anonymous.add(id[i]);
      }
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
    setLoadingType(state, payload: LoadConfiguration) {
      state.local = payload.local;
      state.zip = payload.zip;
      state.single = payload.single;
      state.fileString = payload.fileString;
    },
  },
});

export default store;
