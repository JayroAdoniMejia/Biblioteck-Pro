import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router' // Importamos la configuración del router

const app = createApp(App)

// Le indicamos a Vue que utilice el sistema de rutas
app.use(router)

app.mount('#app')


