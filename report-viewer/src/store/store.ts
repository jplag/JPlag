import { createStore} from "vuex";

const store = createStore({
    state () {
        return {
            anonymous : new Set(),
            files : Object,
            local: Boolean,
            zip: Boolean,
            single: Boolean,
            fileString: String
        }
    },
    getters: {
      getFileByName: (state: any) => (name: string) => {
          return state.files.find((file: any) => file.name.includes(name))
      }
    },
    mutations: {
        addAnonymous (state: any, id) {
            for (let i = 0; i < id.length; i++) {
                state.anonymous.add(id[i])
            }
        },
        removeAnonymous (state, id) {
            for (let i = 0; i < id.length; i++) {
                state.anonymous.delete(id[i])
            }
        },
        resetAnonymous (state) {
            state.anonymous = new Set()
        },
        saveFile (state, file: Record<string, any>) {
            state.files[file.fileName] = file.data
        },
        setLoadingType (state, payload: Record<string, any>) {
            state.local = payload.local
            state.zip = payload.zip
            state.single = payload.single
            state.fileString = payload.fileString
        }

    }
})

export default store