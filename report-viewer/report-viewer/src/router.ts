import { createRouter, createWebHistory } from 'vue-router'
import FileUploadView from './views/FileUploadView.vue'
import OverviewViewWrapper from './viewWrapper/OverviewViewWrapper.vue'
import ComparisonViewWrapper from './viewWrapper/ComparisonViewWrapper.vue'
import ErrorView from './views/ErrorView.vue'
import InformationViewWrapper from './viewWrapper/InformationViewWrapper.vue'
import ClusterViewWrapper from './viewWrapper/ClusterViewWrapper.vue'
import OldVersionRedirectView from './views/OldVersionRedirectView.vue'
import { Version } from '@jplag/model'

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
      path: '/comparison/:firstSubmissionId/:secondSubmissionId',
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
    },
    {
      path: '/old/:version',
      name: 'OldVersionRedirectView',
      component: OldVersionRedirectView,
      props: true
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/error/Could not find the requested page/FileUploadView/Back to file upload'
    }
  ]
})

function redirectOnError(
  error: Error,
  prefix: string = '',
  redirectRoute: string = 'FileUploadView',
  redirectRouteTitle: string = 'Back to file upload'
) {
  console.error(error)
  router.push({
    name: 'ErrorView',
    params: {
      message: prefix + (error.message ?? error),
      to: redirectRoute,
      routerInfo: redirectRouteTitle
    }
  })
}

export function redirectToOldVersion(version: Version) {
  router.push({
    name: 'OldVersionRedirectView',
    params: { version: version.toString() }
  })
}

let hasHadRouterError = false
router.onError((error) => {
  if (hasHadRouterError) {
    return alert('An error occurred while routing. Please reload the page.')
  }
  hasHadRouterError = true
  redirectOnError(error, 'An error occurred while routing. Please reload the page.\n')
})

// preserve query parameters
router.beforeEach((to, from, next) => {
  // Only add the old query if the target has none defined, this prevents an infinite redirect loop
  if (Object.keys(to.query).length === 0 && Object.keys(from.query).length > 0) {
    next({ ...to, query: from.query })
  } else {
    next()
  }
})

export { router, redirectOnError }
