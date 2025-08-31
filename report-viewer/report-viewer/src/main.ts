import { createApp } from 'vue'

import App from './App.vue'
import VueVirtualScroller from '@jplag/ui-components/widget/comparisonTable/VirtualScrollerReexport'

import '@jplag/ui-components/style/style.css'
import { router } from './router'
import { createPinia } from 'pinia'
import { loggerInstaller } from '@jplag/logger/src/vue'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(VueVirtualScroller)
app.use(loggerInstaller)

app.mount('#app')
