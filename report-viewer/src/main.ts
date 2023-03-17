import {createApp} from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store/store"
import VueVirtualScroller from "vue-virtual-scroller"
import 'highlight.js/styles/vs.css'
import 'gitart-vue-dialog/dist/style.css'
import 'highlight.js/lib/common';
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css';


createApp(App).use(router).use(store).use(VueVirtualScroller).mount("#app");
