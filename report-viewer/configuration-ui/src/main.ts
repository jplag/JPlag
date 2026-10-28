import { createApp } from 'vue'

import App from './App.vue'
import VueVirtualScroller from 'vue-virtual-scroller'

import '@jplag/ui-components/style/style.css'
import { createPinia } from 'pinia'
import {router} from '@jplag/report-viewer'


const app = createApp(App)
app.use(createPinia())
app.use(router)

app.use(VueVirtualScroller)

app.config.errorHandler = (err, vm, info) => {
  console.error(err, info)
  alert('An unhandled error occurred. Please check the console for more details.')
}

app.mount('#app')
