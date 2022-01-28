import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";
import Overview from "@/views/Overview.vue";
import ComparisonView from "@/views/ComparisonView.vue"

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Overview",
    component: Overview,
    props:true
  },
  {
    path: "/comparison",
    name: "ComparisonView",
    component: ComparisonView,
    props: route => ({ notAnonymized : route.params.notAnonymized, id1: route.query.id1, id2: route.query.id2})
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
