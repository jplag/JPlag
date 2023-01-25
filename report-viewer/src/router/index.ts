import "./public-path"
import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";
import OverviewView from "@/views/OverviewView.vue";
import ComparisonView from "@/views/ComparisonView.vue";
import FileUploadView from "@/views/FileUploadView.vue";
import OverviewErrorView from "@/views/OverviewErrorView.vue";

/**
 * Router containing the navigation destinations.
 */
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "FileUploadView",
    component: FileUploadView,
  },
  {
    path: "/overview",
    name: "OverviewView",
    component: OverviewView,
  },
  {
    path: "/comparison/:firstId/:secondId",
    name: "ComparisonView",
    component: ComparisonView,
    props: true,
  },
  {
    path: "/overview/error",
    name: "OverviewErrorView",
    component: OverviewErrorView,
  },
];

const router = createRouter({
  history: createWebHistory(__webpack_public_path__),
  routes,
});

export default router;
