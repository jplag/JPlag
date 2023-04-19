import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'FileUploadView',
      component: () => import('@/views/FileUploadView.vue')
    },
    {
      path: '/overview',
      name: 'OverviewView',
      component: () => import('@/views/OverviewView.vue')
    },
    {
      path: '/comparison/:firstId/:secondId',
      name: 'ComparisonView',
      component: () => import('@/views/ComparisonView.vue'),
      props: true
    },
    {
      path: '/error',
      name: 'ErrorView',
      component: () => import('@/views/ErrorView.vue')
    }
  ]
})

export default router
