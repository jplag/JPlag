import { createRouter, createWebHistory } from 'vue-router'
import FileUploadView from '@/views/FileUploadView.vue'
import OverviewView from '@/views/OverviewView.vue'
import ComparisonView from '@/views/ComparisonView.vue'
import ErrorView from '@/views/ErrorView.vue'
import ClusterView from '@/views/ClusterView.vue'
import InformationView from '@/views/InformationView.vue'

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
      component: OverviewView
    },
    {
      path: '/comparison/:firstId/:secondId',
      name: 'ComparisonView',
      component: ComparisonView,
      props: true
    },
    {
      path: '/error',
      name: 'ErrorView',
      component: ErrorView
    },
    {
      path: '/cluster/:clusterIndex',
      name: 'ClusterView',
      component: ClusterView,
      props: true
    },
    {
      path: '/info',
      name: 'InfoView',
      component: InformationView
    },
    {
      path: '/test',
      name: 'TestView',
      component: () => import('@/views/TestView.vue')
    }
  ]
})

export { router }
