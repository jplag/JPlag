import { createApp } from 'vue'

import App from './App.vue'
import { DynamicScroller, DynamicScrollerItem } from 'vue-virtual-scroller'

import '@jplag/ui-components/style/style.css'
import { router } from './router'
import { createPinia } from 'pinia'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.component('DynamicScroller', DynamicScroller)
app.component('DynamicScrollerItem', DynamicScrollerItem)

app.config.errorHandler = (err, vm, info) => {
  console.error(err, info)
  alert('An unhandled error occurred. Please check the console for more details.')
}

app.mount('#app')
