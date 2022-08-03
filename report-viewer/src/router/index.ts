import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import OverviewView from "@/views/OverviewView.vue";
import ComparisonView from "@/views/ComparisonView.vue";
import FileUploadView from "@/views/FileUploadView.vue";

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
    path: "/comparison",
    name: "ComparisonView",
    component: ComparisonView,
    props: true,
  },
];

const router = createRouter({
  history: createWebHistory("/JPlag"),
  routes,
});

export default router;
