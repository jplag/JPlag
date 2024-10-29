import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import { router } from './router'
import VueVirtualScroller from 'vue-virtual-scroller'
import 'highlight.js/lib/common'

import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'

import './style.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueVirtualScroller)

app.config.errorHandler = (err, vm, info) => {
  console.error(err)
  console.error(info)
  alert('An unhandled error occurred. Please check the console for more details.')
}

app.mount('#app')
