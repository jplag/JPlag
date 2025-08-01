import { createApp } from 'vue'

import App from './App.vue'

import '@jplag/ui-components/style/style.css'

const app = createApp(App)

app.config.errorHandler = (err, vm, info) => {
  console.error(err)
  console.error(info)
  alert('An unhandled error occurred. Please check the console for more details.')
}

app.mount('#app')
