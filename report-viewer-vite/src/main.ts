import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import VueVirtualScroller from 'vue-virtual-scroller'
import 'highlight.js/lib/common'

import 'highlight.js/styles/vs.css'
import 'gitart-vue-dialog/dist/style.css'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueVirtualScroller)

app.mount('#app')
