import { createRouter, createWebHistory } from 'vue-router'
import FileUploadView from '@/views/FileUploadView.vue'
import OverviewViewWrapper from '@/viewWrapper/OverviewViewWrapper.vue'
import ComparisonViewWrapper from '@/viewWrapper/ComparisonViewWrapper.vue'
import ErrorView from '@/views/ErrorView.vue'
import InformationViewWrapper from '@/viewWrapper/InformationViewWrapper.vue'
import ClusterViewWrapper from '@/viewWrapper/ClusterViewWrapper.vue'

/**
 * The router is used to navigate between the different views of the application.
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'FileUploadView',
      component: FileUploadView
    },
    {
      path: '/overview',
      name: 'OverviewView',
      component: OverviewViewWrapper
    },
    {
      path: '/comparison/:firstId/:secondId',
      name: 'ComparisonView',
      component: ComparisonViewWrapper,
      props: true
    },
    {
      path: '/error/:message/:to?/:routerInfo?',
      name: 'ErrorView',
      component: ErrorView,
      props: true
    },
    {
      path: '/cluster/:clusterIndex',
      name: 'ClusterView',
      component: ClusterViewWrapper,
      props: true
    },
    {
      path: '/info',
      name: 'InfoView',
      component: InformationViewWrapper
    }
  ]
})

export { router }
