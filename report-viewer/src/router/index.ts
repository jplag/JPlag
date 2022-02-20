import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";
import Overview from "@/views/OverviewView.vue";
import ComparisonView from "@/views/ComparisonView.vue"
import FileUpload from "@/views/FileUpload.vue"

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "FileUpload",
    component: FileUpload
  },
  {
    path: "/overview",
    name: "OverviewView",
    component: Overview,
  },
  {
    path: "/comparison",
    name: "ComparisonView",
    component: ComparisonView,
    props: route => ({
      id1: route.query.id1,
      id2: route.query.id2
    })
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
