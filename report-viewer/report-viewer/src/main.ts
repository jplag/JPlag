import { createApp } from 'vue'

import App from './App.vue'
import VueVirtualScroller from '@jplag/ui-components/widget/comparisonTable/VirtualScrollerReexport'

import '@jplag/ui-components/style/style.css'
import { router } from './router'
import { createPinia } from 'pinia'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(VueVirtualScroller)

app.config.errorHandler = (err, vm, info) => {
  console.error(err, info)
  alert('An unhandled error occurred. Please check the console for more details.')
}

app.mount('#app')
