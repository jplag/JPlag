import { createApp } from 'vue'

import App from './App.vue'

import '@jplag/ui-components/style/style.css'
import { router } from './router'
import { createPinia } from 'pinia'
import { reportStore } from '@jplag/report-viewer/src/stores/reportStore'
import { ReportFileHandler } from '@jplag/parser'
import reportUrl from './assets/results.jplag?url'

const app = createApp(App)
app.use(createPinia())
app.use(router)

fetch(reportUrl)
  .then((r) => r.blob())
  .then((b) => new ReportFileHandler().extractContent(b))
  .then((report) => reportStore().loadReport(report.files, report.submissionFiles, 'internal'))

app.mount('#app')
