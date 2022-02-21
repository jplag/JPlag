import {createStore} from "vuex";

/**
 * Local store. Stores the state of the application.
 */
const store = createStore({
    state() {
        return {
            /**
             * The set of ids to be hidden.
             */
            anonymous: new Set(),
            /**
             * Stored files if zip mode is used. Stores the files as key - file name, value - file string
             */
            files: Object,
            /**
             * Indicates whether local mode is used.
             */
            local: Boolean,
            /**
             * Indicates whether zip mode is used.
             */
            zip: Boolean,
            /**
             * Indicates whether single file mode is used.
             */
            single: Boolean,
            /**
             * Files string if single mode is used.
             */
            fileString: {
                type: String,
                required: false
            }
        }
    },
    mutations: {
        addAnonymous(state: any, id) {
            for (let i = 0; i < id.length; i++) {
                state.anonymous.add(id[i])
            }
        },
        removeAnonymous(state, id) {
            for (let i = 0; i < id.length; i++) {
                state.anonymous.delete(id[i])
            }
        },
        resetAnonymous(state) {
            state.anonymous = new Set()
        },
        saveFile(state, file: Record<string, any>) {
            state.files[file.fileName] = file.data
        },
        setLoadingType(state, payload: Record<string, any>) {
            state.local = payload.local
            state.zip = payload.zip
            state.single = payload.single
            state.fileString = payload.fileString
        }

    }
})

export default store