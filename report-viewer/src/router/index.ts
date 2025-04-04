import { createRouter, createWebHistory } from 'vue-router'
import FileUploadView from '@/views/FileUploadView.vue'
import OverviewViewWrapper from '@/viewWrapper/OverviewViewWrapper.vue'
import ComparisonViewWrapper from '@/viewWrapper/ComparisonViewWrapper.vue'
import ErrorView from '@/views/ErrorView.vue'
import InformationViewWrapper from '@/viewWrapper/InformationViewWrapper.vue'
import ClusterViewWrapper from '@/viewWrapper/ClusterViewWrapper.vue'
import OldVersionRedirectView from '@/views/OldVersionRedirectView.vue'
import { VersionChecker } from '@/model/factories/VersionChecker'

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
      path: '/comparison/:comparisonFileName',
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

let hasHadRouterError = false
router.onError((error) => {
  if (hasHadRouterError) {
    return alert('An error occurred while routing. Please reload the page.')
  }
  hasHadRouterError = true
  redirectOnError(error, 'An error occurred while routing. Please reload the page.\n')
})

router.beforeEach(async (to, from) => {
  if (to.name === 'ErrorView') {
    return true
  }
  if (to.name === 'OldVersionRedirectView') {
    return true
  }
  if (from.name === 'FileUploadView') {
    return true
  }
  const versionResult = await VersionChecker.verifyVersion()
  if (versionResult && !versionResult.valid) {
    router.push({
      name: 'OldVersionRedirectView',
      params: { version: versionResult.version.toString() }
    })
    return false
  }
  return true
})

export { router, redirectOnError }
