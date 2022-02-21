import {createApp} from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store/store"
import './libs/highlightjs/styles/edited/vs.min.css'
import 'gitart-vue-dialog/dist/style.css'
import 'highlight.js/lib/common';


createApp(App).use(router).use(store).mount("#app");
