import { createRouter, createWebHistory } from 'vue-router'
import LanguageView from './views/LanguageView.vue'
import SubmissionView from './views/SubmissionView.vue'
import ComparisonView from './views/ComparisonView.vue'
import EmptyView from './views/EmptyView.vue'
import { store } from './store'
import { ParserLanguage } from '@jplag/model'


/**
 * The router is used to navigate between the different views of the application.
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/language/',
      name: 'Language',
      component: LanguageView
    },
    {
      path: '/',
      name: 'Submissions',
      component: SubmissionView
    },
    {
      path: '/comparison',
      name: 'Comparison',
      component: ComparisonView
    },
    {
      path: '/report-viewer',
      name: 'Report Viewer',
      component: EmptyView
    }
  ]
})

router.afterEach((to) => {
  if (to.name === 'Language') {
    if (store().cliOptions.submissionDirectories.length + store().cliOptions.oldSubmissionDirectories.length > 0) {
      store().cliOptions.language = [
        ParserLanguage.PYTHON,
        ParserLanguage.TEXT
      ]
    }
  }
  if (to.name === 'Report Viewer') {
    const json = JSON.stringify(store().cliOptions)
    const blob = new Blob([json], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
  
    const a = document.createElement('a')
    a.href = url
    a.download = 'cli-options-2.json'
    a.click()
  
    URL.revokeObjectURL(url)
  }
})


export { router }
