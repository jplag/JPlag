import { createApp } from 'vue'

import App from './App.vue'

import '@jplag/ui-components/style/style.css'
//import { router } from './router'

const app = createApp(App)
//app.use(router)

app.config.errorHandler = (err, vm, info) => {
  console.error(err, info)
  alert('An unhandled error occurred. Please check the console for more details.')
}

app.mount('#app')
