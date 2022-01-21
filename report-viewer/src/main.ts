import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import './libs/highlightjs/styles/edited/vs.min.css'
import 'highlight.js/lib/common';
import hljsVuePlugin from "@highlightjs/vue-plugin";
createApp(App).use(router).use(hljsVuePlugin).mount("#app");
