import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import Overview from "@/views/Overview.vue";
import FileUpload from "@/views/FileUpload.vue"
import ComparisonView from "@/views/ComparisonView.vue"

const routes: Array<RouteRecordRaw> = [
  {
    path: "/f",
    name: "FileUpload",
    component: FileUpload
  },
  {
    path: "/",
    name: "Overview",
    component: Overview,
    //props: route => ({ jsonString : route.params.str })
  },
  {
    path: "/comparison",
    name: "ComparisonView",
    component: ComparisonView,
    props: route => ({ jsonString : route.params.str})
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
