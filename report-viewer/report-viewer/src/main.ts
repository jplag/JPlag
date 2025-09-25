import { createApp } from 'vue'

import App from './App.vue'
import VueVirtualScroller from '@jplag/ui-components/widget/comparisonTable/VirtualScrollerReexport'

import '@jplag/ui-components/style/style.css'
import { router } from './router'
import { createPinia } from 'pinia'
import { Logger } from '@jplag/logger'
//import { loggerInstaller } from '@jplag/logger/src/vuePlugin'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(VueVirtualScroller)
//app.use(loggerInstaller)
testLog()
app.mount('#app')

function testLog() {
  Logger.info('Info log')
  Logger.warn('Warn log')
  Logger.error('Error log')
  Logger.debug('Debug log')
  Logger.log('Log log')
}
