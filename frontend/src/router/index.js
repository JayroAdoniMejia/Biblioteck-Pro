import { createRouter, createWebHistory } from 'vue-router';
// Importamos la vista de Login de forma directa (es la principal)
import Login from '../views/Login.vue';

const routes = [
  { 
    path: '/', 
    name: 'Login',
    component: Login 
  },
  { 
    path: '/admin', 
    name: 'Admin',
    // Usamos carga perezosa para el panel de administración
    component: () => import('../views/AdminPanel.vue') 
  },
  {
    // Ruta comodín: si el usuario escribe cualquier otra cosa, lo manda al Login
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({
  // Usamos el historial del navegador para rutas limpias sin #
  history: createWebHistory(),
  routes
});

export default router;