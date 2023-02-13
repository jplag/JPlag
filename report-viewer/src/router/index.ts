import "./public-path"
import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";

/**
 * Router containing the navigation destinations.
 */
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "FileUploadView",
    component: () => import('@/views/FileUploadView.vue'),
  },
  {
    path: "/overview",
    name: "OverviewView",
    component: () => import('@/views/OverviewView.vue'),
  },
  {
    path: "/comparison/:firstId/:secondId",
    name: "ComparisonView",
    component: () => import('@/views/ComparisonView.vue'),
    props: true,
  },
  {
    path: "/error",
    name: "ErrorView",
    component: () => import('@/views/ErrorView.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(__webpack_public_path__),
  routes,
});

export default router;
