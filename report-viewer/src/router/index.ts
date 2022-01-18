import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";
import Overview from "@/views/Overview.vue";
import ComparisonView from "@/views/ComparisonView.vue"

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Overview",
    component: Overview,
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
