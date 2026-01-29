import { createRouter, createWebHistory } from 'vue-router'
import LanguageView from './views/LanguageView.vue'
import SubmissionView from './views/SubmissionView.vue'
import ComparisonView from './views/ComparisonView.vue'
import { router as reportViewerRouter } from '@jplag/report-viewer/src/router'


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
    ...reportViewerRouter.getRoutes().map(r => ({
      ...r,
      path: '/viewer' + r.path
    }))
  ]
})




export { router }
