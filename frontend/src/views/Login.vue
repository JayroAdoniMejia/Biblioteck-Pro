<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios'; // Importamos axios para la conexión real

const router = useRouter();
const username = ref('');
const password = ref('');
const loading = ref(false);

const handleLogin = async () => {
  loading.value = true;
  
  try {
    // 1. Petición POST a tu nuevo AuthController
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    });

    // 2. Si es exitoso, guardamos el rol (ADMIN o LECTOR)
    const { role, username: authenticatedUser } = response.data;
    localStorage.setItem('userRole', role);
    localStorage.setItem('userName', authenticatedUser);

    // 3. Navegamos al panel
    router.push('/admin'); 

  } catch (error) {
    // Manejo de errores si las credenciales no existen en MongoDB
    console.error('Error en el login:', error);
    if (error.response && error.response.status === 401) {
      alert('Usuario o contraseña incorrectos');
    } else {
      alert('Error de conexión con el servidor backend');
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="book-animation">
        <div class="book-icon"></div>
      </div>
      
      <h2>Biblioteck Pro</h2>
      <p>Acceso al Sistema de Biblioteca</p>

      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label>Usuario</label>
          <input v-model="username" type="text" placeholder="Ej: admin o lector" required />
        </div>
        
        <div class="input-group">
          <label>Contraseña</label>
          <input v-model="password" type="password" placeholder="••••••••" required />
        </div>

        <button type="submit" class="btn-login" :disabled="loading">
          {{ loading ? 'Verificando...' : 'Entrar' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Tus estilos se mantienen intactos ya que definen la identidad visual del proyecto */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #010409;
}
.login-box {
  background: rgba(13, 17, 23, 0.9);
  padding: 40px;
  border-radius: 16px;
  border: 1px solid #30363d;
  width: 100%;
  max-width: 350px;
  text-align: center;
}
.book-icon { width: 30px; height: 40px; background: #8957e5; border-radius: 4px; margin: 0 auto 20px; }
h2 { color: #f0f6fc; }
p { color: #8b949e; }
.input-group { text-align: left; margin-bottom: 20px; }
label { color: #8957e5; font-weight: bold; }
input { width: 100%; padding: 12px; background: #0d1117; border: 1px solid #30363d; border-radius: 8px; color: white; outline: none; }
input:focus { border-color: #8957e5; }
.btn-login { width: 100%; padding: 14px; background: #8957e5; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: bold; transition: 0.3s; }
.btn-login:hover { background: #a371f7; }
.btn-login:disabled { opacity: 0.5; cursor: not-allowed; }
</style>