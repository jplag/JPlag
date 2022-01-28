import { createStore} from "vuex";

const store = createStore({
    state () {
        return {
            anonymous : new Set(),
            files: Array
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
        saveFiles (state, files) {
            state.files = files
        }

    }
})

export default store