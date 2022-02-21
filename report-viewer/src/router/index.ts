import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";
import Overview from "@/views/OverviewView.vue";
import ComparisonView from "@/views/ComparisonView.vue"
import FileUploadView from "@/views/FileUploadView.vue"

const routes: Array<RouteRecordRaw> = [
    {
        path: "/",
        name: "FileUploadView",
        component: FileUploadView
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
            id1: route.query.firstId,
            id2: route.query.secondId
        })
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
