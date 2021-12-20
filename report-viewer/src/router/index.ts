import {createRouter, createWebHashHistory, createWebHistory, RouteRecordRaw} from "vue-router";
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
    path: "/comparison/:id1/:id2",
    name: "ComparisonView",
    component: ComparisonView,
    props: true
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
